package it.polimi.web.project.beans;

import java.sql.Date;

public class Transfer {
	private int transferID;
	private Date data;
	private int amount;
	private String causal;
	private int bankAccountID;
	private int destBackAccountId;

	public int getTransferID() {
		return this.transferID;
	}

	public void setTransferID(int id) {
		this.transferID = id;
	}

	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getCausal() {
		return causal;
	}

	public void setCausal(String causale) {
		this.causal = causale;
	}

	public int getBankAccountID() {
		return bankAccountID;
	}

	public void setBankAccountID(int contoID) {
		bankAccountID = contoID;
	}

	public int getDestBankAccountId() {
		return destBackAccountId;
	}

	public void setDestBankAccountId(int destContoId) {
		destBackAccountId = destContoId;
	}
}
