package com.hcl.msf4j.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@XmlRootElement
@Document(collection = "audit")
public class AccountModel {

	private int fromAccount;
	private int toAccount;
	private String accName;
	private double amount;
	private String currency;
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date startTime;
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date endTime;
	private String status;

	public AccountModel() {

	}

	public AccountModel(int fromAccount, int toAccount, String accName, double amount, String currency,
			Date startTime, Date endTime, String status) {
		super();
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.accName = accName;
		this.amount = amount;
		this.currency = currency;
		this.startTime = startTime;
		this.endTime = endTime;
		this.setStatus(status);
	}


	public int getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(int fromAccount) {
		this.fromAccount = fromAccount;
	}

	public int getToAccount() {
		return toAccount;
	}

	public void setToAccount(int toAccount) {
		this.toAccount = toAccount;
	}

	public String getAccName() {
		return accName;
	}

	public void setAccName(String accName) {
		this.accName = accName;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
