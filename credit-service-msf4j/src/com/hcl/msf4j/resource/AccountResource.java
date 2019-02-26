package com.hcl.msf4j.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import com.hcl.msf4j.model.AccountModel;
import com.hcl.msf4j.service.CreditDAO;

@Component
@Path("/bank")
public class AccountResource {
	
	CreditDAO creditDAO = new CreditDAO();

	@POST
	@Path("/credit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String accountCredit(AccountModel account) {
		
		boolean credit = false;
		try{
			System.out.println ("Req: Account Credit");
			if(account!=null){
				credit = creditDAO.accountCredit(account);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		if(credit){
			return "Transaction Success!!! " + account.getCurrency() + " " + account.getAmount() + " successfully credited to the account " + account.getAccNo();
		}else
			return "Transaction Failed!!!";
	}
	
}
