package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import config.ConfigManager;

public class Postgres extends Database {
	public Postgres() {
		loadConfig();
	}
	
	public Postgres(String user, String password, String database) {
		loadConfig();
		setUser(user);
		setPassword(password);
		setDatabase(database);
	}
	
	@Override
	public String getSelectSQL(String cols, String tableName, String where, int deb, int fin) throws Exception {
		// TODO Auto-generated method stub
		if(deb < 0 || fin < 0) { throw new Exception("deb and fin must be positive"); }
		
		if(deb > 0) { deb--; }
		
		String sql = super.getSelectSQL(cols, tableName, where) + " LIMIT " + fin + " OFFSET " + deb;
		
		return sql;
	}

	@Override
	public Connection connect() throws Exception {
		// TODO Auto-generated method stub*
		if(getDriver() != null) { Class.forName(getDriver()); }
		String connString = "jdbc:postgresql://" + getHost() + ":" + getPort() + "/" + getDatabase();
		Connection con = DriverManager.getConnection(connString, getUser(), getPassword());
		
		return con;
	}

	@Override
	public void loadConfig() {
		Properties prop = ConfigManager.loadConfig(CONFIG_FILE);
		setDriver("org.postgresql.Driver");
		setHost(prop.getProperty("postgres.host"));
		setPort(prop.getProperty("postgres.port"));
		setUser(prop.getProperty("postgres.user"));
		setPassword(prop.getProperty("postgres.password"));
		setDatabase(prop.getProperty("postgres.database"));
	}

	@Override
	public String getSequence(String prefix, String name) {
		// TODO Auto-generated method stub
		String sql = "CONCAT('" + prefix + "', nextval('" + name + "'))";
		
		return sql;
	}

}
