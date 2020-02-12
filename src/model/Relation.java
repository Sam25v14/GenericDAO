package model;

import annotations.DAOTable;

@DAOTable(name = "Relation")
public class Relation {
	String idClient;
	String idRelation;

	public String getIdClient() {
		return idClient;
	}

	public void setIdClient(String idClient) {
		this.idClient = idClient;
	}

	public String getIdRelation() {
		return idRelation;
	}

	public void setIdRelation(String idRelation) {
		this.idRelation = idRelation;
	}
}
