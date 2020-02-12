package test;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import dao.DAO;
import database.Database;
import database.Oracle;
import database.Postgres;
import model.Client;
import model.Fournisseur;
import model.Produit;

public class Test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Connection con = null;
		
		try {
			Oracle oracle = new Oracle("testDAO", "1234");
			DAO<Client> dao = (new DAO<Client>(oracle));
			con = oracle.connect();
//			
//			System.out.println("Connecté");
//						
			oracle.alterDateTimestamp(con);
//			
			Date d = new Date(11,05,2002);
			List<Client> prods = dao.select(con, (new Client()), null, null, null);
//			
			System.out.println(prods.size());
//			System.out.println(prods.get(0).getRelations());
//			DAO<Produit> dao = (new DAO<Produit>(oracle));
//			con = oracle.connect();
			
//			Postgres postgres = new Postgres("testdao", "1234", "tests");
//			DAO<Produit> dao = new DAO<Produit>(postgres);
//			con = postgres.connect();
//			dao.addToCache(con, Produit.class);
			
//			List<Produit> prods = dao.select(con, (new Produit()), null, null, null);
//			List<Produit> prods = dao.find(con, (new Produit()), null, null, 1, 2, null);
//			System.out.println(prods.size());
			
//			DAO<Client> dao = (new DAO<Client>(oracle));
//			con = oracle.connect();
//			oracle.alterDateTimestamp(con);
//			Date d = new Date(11,05,2002);
//			Client c = new Client("Yvan",18,d);
//			String[] indice = {"id"};
//			String id = dao.insert(con, c, null,null, "id");
//			System.out.println(id);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			if(con != null) con.close();
		}
	}

}
