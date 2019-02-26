package com.hcl.msf4j.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import com.hcl.msf4j.model.AccountModel;
import com.hcl.msf4j.service.DebitDAO;

@Component
@Path("/bank")
public class AccountResource {

	DebitDAO debitDAO = new DebitDAO();
	
	@POST
	@Path("/debit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String accountDebit(AccountModel account) {
		
		boolean debit = false;
		try{
			System.out.println ("Req: Account Debit");
			if(account!=null){
				debit = debitDAO.accountDebit(account);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		if(debit){
			return "Transaction Success!!! " + account.getCurrency() + " " + account.getAmount() + " successfully debited from the account " + account.getAccNo();
		}else
			return "Transaction Failed!!!";
	}
	
}