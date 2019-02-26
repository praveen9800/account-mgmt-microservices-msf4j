package com.hcl.msf4j.service;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.hcl.msf4j.model.AccountModel;

@Component
public class AccountDAO {
	
	ApplicationContext  ctx= null;
	JdbcTemplate jdbcTemplate = null;
	
	public AccountDAO() {
		ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		jdbcTemplate = (JdbcTemplate)ctx.getBean("jdbcTemplate");
	}
	
	public AccountModel getAccount(int accNo){
		String query="select * from account where acc_id =" + "'" + accNo + "'";

	    Map map =  jdbcTemplate.queryForMap(query);
	    AccountModel accountModel = new AccountModel();
	    accountModel.setAccNo((Integer) map.get("acc_id"));
	    accountModel.setAccName((String)map.get("acc_name"));
	    accountModel.setAmount((Double)map.get("amount"));
	    accountModel.setCurrency((String)map.get("currency"));

	    return accountModel;
	}

}
