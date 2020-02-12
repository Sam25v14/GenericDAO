package model;

import java.util.Date;

import annotations.DAORelation;
import annotations.DAOSequence;
import annotations.DAOTable;

@DAOTable(name = "Client")
public class Client {
	@DAOSequence(name = "ClientSeq", prefix = "CL")
	String id;
	String nom;
	int nbRes;
	Date dateNaissance;
	
	@DAORelation(onField = "id", withField = "idClient")
	Relation[] relations;
	
	public Client() {}
	
	public Client(String nom, int i, Date d) {
		// TODO Auto-generated constructor stub
		setNom(nom);
		setNbRes(i);
		setDateNaissance(d);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Relation[] getRelations() {
		return relations;
	}

	public void setRelations(Relation[] relations) {
		this.relations = relations;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public int getNbRes() {
		return nbRes;
	}

	public void setNbRes(int nbRes) {
		this.nbRes = nbRes;
	}

	public Date getDateNaissance() {
		return dateNaissance;
	}

	public void setDateNaissance(Date dateNaissance) {
		this.dateNaissance = dateNaissance;
	}
}
