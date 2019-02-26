package com.hcl.msf4j.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import com.hcl.msf4j.model.AccountModel;
import com.hcl.msf4j.service.AccountDAO;

@Component
@Path("/bank")
public class AccountResource {
	
	AccountDAO accountDAO = new AccountDAO();

	@GET
	@Path("accounts/{accNo}")
	@Produces(MediaType.APPLICATION_JSON)
	public AccountModel getAccountInfo(@PathParam("accNo") Integer accNo) {
		
		AccountModel accountModel = null;
		try{
			System.out.println ("Req: Get Account Details");
			if(accNo!=null && accNo.intValue()>0){
				accountModel =  accountDAO.getAccount(accNo);
			}
		} catch(Exception e){
			e.printStackTrace();
		} /*finally {
			if (ctx != null)
                ((ConfigurableApplicationContext) ctx).close();
		}*/
		return accountModel;
	}
}
