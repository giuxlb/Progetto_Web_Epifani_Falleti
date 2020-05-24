package it.polimi.web.project.beans;

import java.sql.Date;

public class Trasferimento {
	private int trasferimentoID;
	private Date data;
	private int importo;
	private String causale;
	private int ContoID;
	private int DestContoId;

	public int getTrasferimentoID() {
		return this.trasferimentoID;
	}

	public void setTrasferimentoID(int id) {
		this.trasferimentoID = id;
	}

	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getImporto() {
		return importo;
	}

	public void setImporto(int importo) {
		this.importo = importo;
	}

	public String getCausale() {
		return causale;
	}

	public void setCausale(String causale) {
		this.causale = causale;
	}

	public int getContoID() {
		return ContoID;
	}

	public void setContoID(int contoID) {
		ContoID = contoID;
	}

	public int getDestContoId() {
		return DestContoId;
	}

	public void setDestContoId(int destContoId) {
		DestContoId = destContoId;
	}
}
