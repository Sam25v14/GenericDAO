package model;

import annotations.DAORelation;
import annotations.DAOTable;

@DAOTable(name = "Fournisseur")
public class Fournisseur {
	String id;
	
	@DAORelation(onField = "id", withField = "idFournisseur")
	Produit[] prods;

	public Produit[] getProds() {
		return prods;
	}

	public void setProds(Produit[] prods) {
		this.prods = prods;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
