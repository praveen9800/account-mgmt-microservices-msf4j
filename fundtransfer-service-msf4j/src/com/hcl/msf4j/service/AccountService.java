package com.hcl.msf4j.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.HttpHeaders;

import org.json.simple.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.hcl.msf4j.model.AccountModel;
import com.hcl.msf4j.model.AccountTransferModel;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

@Component
public class AccountService {

	String debitUrl = null;
	String creditUrl = null;
	//String authToken = null;
	String debitToken = null;
	String creditToken = null;
	Client client = null;
	ApplicationContext ctx = null;
	MongoOperations mongoOperation = null;

	public AccountService() {
		
		try{
			ctx = new GenericXmlApplicationContext("applicationContext.xml");
			mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		// To fetch config properties file to get URLs & token
		InputStream is = null;
		try {
			Properties prop = new Properties();
			is = this.getClass().getResourceAsStream("/config.properties");
			prop.load(is);
			debitUrl = prop.getProperty("DEBITURL");
			creditUrl = prop.getProperty("CREDITURL");
			//authToken = prop.getProperty("AUTHTOKEN");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// To create client to communicate with debit/credit micro-services
		try{
			client = Client.create();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		// To communicate with services which are hosted on HTTPS
		TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {

					@Override
					public void checkClientTrusted(
							X509Certificate[] chain, String authType)
							throws CertificateException {
					}

					@Override
					public void checkServerTrusted(
							X509Certificate[] chain, String authType)
							throws CertificateException {
					}

					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}}
        };
		
		// Install the all-trusting trust manager
        SSLContext sc;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
        
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {

			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
        };
        
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}

	// FUND TRANSFER
	@SuppressWarnings("unchecked")
	public boolean fundTransfer(AccountTransferModel accountTransferModel) {
		
		Date d = new Date();
		accountTransferModel.setStartTime(d);

		if (accountTransferModel !=null && accountTransferModel.getFromAccount() != 0 && accountTransferModel.getToAccount() != 0 
				&& accountTransferModel.getAmount() != 0 && accountTransferModel.getCurrency() != null){
			//	&& accountTransferModel.getDebitAuth() !=null && accountTransferModel.getCreditAuth() !=null) {
			
			try {
				//Client debitClient = Client.create();
				WebResource debitBuilder = client.resource(debitUrl);
				//Builder debitBuilder = client.resource(debitUrl).header(HttpHeaders.AUTHORIZATION, "Bearer " + accountTransferModel.getDebitAuth());
				JSONObject debitObj = new JSONObject();
				
				debitObj.put("accNo", accountTransferModel.getFromAccount());
				debitObj.put("amount", accountTransferModel.getAmount());
				debitObj.put("currency", accountTransferModel.getCurrency());

				ClientResponse debitResponse = debitBuilder.type("application/json")
						.post(ClientResponse.class, debitObj);
				
				if(debitResponse == null){
					fundTransferAudit(accountTransferModel, "Failed");
					return false;
				}
				
				String debitOutput = debitResponse.getEntity(String.class);
				boolean debitSuccess = debitOutput.contains("Success");

				if (debitResponse.getStatus() == 200 && debitSuccess) {
					
					System.out.println("Output from Server .... \n");
					
					System.out.println(debitOutput);
					
					try {
						//Client creditClient = Client.create();
						WebResource creditBuilder = client.resource(creditUrl);
						//Builder creditBuilder = client.resource(creditUrl).header(HttpHeaders.AUTHORIZATION, "Bearer " + accountTransferModel.getCreditAuth());
						
						JSONObject creditObj = new JSONObject();
						
						creditObj.put("accNo", accountTransferModel.getToAccount());
						creditObj.put("amount", accountTransferModel.getAmount());
				        creditObj.put("currency", accountTransferModel.getCurrency());

						ClientResponse creditResponse = creditBuilder.type("application/json")
								.post(ClientResponse.class, creditObj); // Pass creditObj as null for testing debit roll back functionality
						
						if(creditResponse == null || creditResponse.getStatus() != 200){
							// To roll back the debit transaction if credit transaction failed
							boolean rollbackStatus= debitRollback(accountTransferModel);
							if(!rollbackStatus){
								debitRollback(accountTransferModel);
							}
							// To update Audit details in MongoDB
							fundTransferAudit(accountTransferModel, "Failed");
							return false;
						}
						
						String creditOutput = creditResponse.getEntity(String.class);
						boolean creditSuccess = creditOutput.contains("Success");

						if (creditResponse.getStatus() == 200 && creditSuccess) {
							System.out.println("Output from Server .... \n");
							System.out.println(creditOutput);
							fundTransferAudit(accountTransferModel, "Success");
							return true;
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		fundTransferAudit(accountTransferModel, "Failed");
		return false;
	}
	
	
	private boolean debitRollback(AccountTransferModel accountTransferModel){
		
		try {
			Builder creditBuilder = client.resource(creditUrl).header(HttpHeaders.AUTHORIZATION, "Bearer " + accountTransferModel.getCreditAuth());
			
			JSONObject creditObj = new JSONObject();
			
			creditObj.put("accNo", accountTransferModel.getFromAccount());
			creditObj.put("amount", accountTransferModel.getAmount());
	        creditObj.put("currency", accountTransferModel.getCurrency());

			ClientResponse creditResponse = creditBuilder.type("application/json")
					.post(ClientResponse.class, creditObj);
			
			if(creditResponse == null || creditResponse.getStatus() != 200){
				return false;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	// To update historical data in Audit table MongoDB	
	private void fundTransferAudit(AccountTransferModel accountTransferModel, String status){
		
		Date d = new Date();
		accountTransferModel.setEndTime(d);
		accountTransferModel.setStatus(status);
		
		try{
			mongoOperation.insert(accountTransferModel);
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	// To retrieve all the transactions done by a particular account
	public List<AccountModel> retrieveTransactions(int accNo){
		
		List<AccountModel> accountModel = null;
		
		try{
			accountModel = mongoOperation.find(new Query(Criteria.where("fromAccount").is(accNo)), AccountModel.class);
			System.out.println(accountModel);
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return accountModel;
	}
	
}
