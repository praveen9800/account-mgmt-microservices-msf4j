package com.hcl.msf4j.service;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.hcl.msf4j.model.AccountModel;

@Component
public class DebitDAO {

	JdbcTemplate jdbcTemplate = null;
	ApplicationContext  ctx= null;
	
	public DebitDAO() {
		
		ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		jdbcTemplate = (JdbcTemplate) ctx.getBean("jdbcTemplate");
	}
	
	// DEBIT
	public boolean accountDebit(AccountModel account) {
		
		if(account!=null && account.getCurrency()!=null && account.getAccNo()!=0 && account.getAmount()!=0){
			
			String selectQuery="select * from account where acc_id =" + "'" + account.getAccNo() + "'";
		    Map map =  jdbcTemplate.queryForMap(selectQuery);
		    if(map!=null && !map.isEmpty()){
		    	double existingAmount = (Double)map.get("amount");
		    	String existingCurrency = (String)map.get("currency");
		    	if(existingAmount-account.getAmount()>0){
		    		double newAmount = existingAmount-account.getAmount();
					if(existingAmount!=0 && existingCurrency.equalsIgnoreCase(account.getCurrency())){
						String updateQuery="update account set amount='"+ newAmount +"' where acc_id='"+account.getAccNo()+"'";
						int status = jdbcTemplate.update(updateQuery);
						if(status == 1)
						return true;
					}
		    	}
			}
		}
		return false;
	}
}