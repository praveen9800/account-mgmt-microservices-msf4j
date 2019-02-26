package com.hcl.msf4j.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import com.hcl.msf4j.model.AccountModel;
import com.hcl.msf4j.model.AccountTransferModel;
import com.hcl.msf4j.service.AccountService;

@Component
@Path("/bank")
public class AccountResource {

	AccountService accountService = new AccountService();
	
	@POST
	@Path("/fundTransfer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String accountFundTransfer(AccountTransferModel accountTransferModel) {
		System.out.println ("Req: Account Fund Transfer");
		
		if(accountService.fundTransfer(accountTransferModel)){
			return "Transaction Success!!! " + accountTransferModel.getCurrency() + " " + accountTransferModel.getAmount() + " debited from the account " + accountTransferModel.getFromAccount() + " and credited to the account " + accountTransferModel.getToAccount();
		}else
			return "Transaction Failed!!!";
	}
	
	@GET
	@Path("/fundTransfer/transactions/{accNo}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<AccountModel> accountTransactions(@PathParam("accNo") Integer accNo) {
		System.out.println ("Req: Account Transactions");
		
		List<AccountModel> accountModel = null;
		if(accNo!=null && accNo.intValue()>0){
			accountModel = accountService.retrieveTransactions(accNo);
		}
		return accountModel;
	}
}
