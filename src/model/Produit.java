package model;

import annotations.DAOColumn;
import annotations.DAORelation;
import annotations.DAOTable;

@DAOTable(name = "Produit")
public class Produit {
	@DAOColumn(name = "id")
	String idProduit;
	String nomProduit;
	
	@DAORelation(onField = "idFournisseur", withField = "id")
	Fournisseur fournisseur;
	
	public Fournisseur getFournisseur() {
		return fournisseur;
	}

	public void setFournisseur(Fournisseur fournisseur) {
		this.fournisseur = fournisseur;
	}

	public String getIdProduit() {
		return idProduit;
	}

	public void setIdProduit(String idProduit) {
		this.idProduit = idProduit;
	}

	public Produit() {}
	
	public String getNomProduit() {
		return nomProduit;
	}

	public void setNomProduit(String nomProduit) {
		this.nomProduit = nomProduit;
	}
}
