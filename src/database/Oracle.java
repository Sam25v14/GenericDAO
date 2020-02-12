package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import config.ConfigManager;

public class Oracle extends Database {
	private String sid;
	
	public Oracle() {
		loadConfig();
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	@Override
	public void loadConfig() {
		Properties prop = ConfigManager.loadConfig(CONFIG_FILE);
		setDriver("oracle.jdbc.driver.OracleDriver");
		setHost(prop.getProperty("oracle.host"));
		setPort(prop.getProperty("oracle.port"));
		setUser(prop.getProperty("oracle.user"));
		setPassword(prop.getProperty("oracle.password"));
		setSid(prop.getProperty("oracle.sid"));
	}

	@Override
	public String getSequence(String prefix, String name) {
		// TODO Auto-generated method stub
		return "CONCAT('" + prefix + "'," + name + ".nextval)";
	}

	@Override
	public String getSelectSQL(String cols, String tableName, String where, int deb, int fin) {
		String sql = "SELECT * FROM (" + "SELECT tab.*,rownum r__ " + "FROM ("
				+ super.getSelectSQL(cols, tableName, where) + ") tab" + ") WHERE r__ BETWEEN " + deb + " AND " + fin
				+ " ORDER BY r__";

		return sql;
	}

	public boolean alterDateTimestamp(Connection con) throws SQLException {
		Statement s = con.createStatement();
		boolean timestampAlter = false;
		boolean dateAlter = false;

		try {
			timestampAlter = s.execute("ALTER SESSION SET nls_Timestamp_format = 'YYYY-MM-DD HH24:MI:SS.ff'");
			dateAlter = s.execute("ALTER SESSION SET nls_Date_format = 'YYYY-MM-DD HH24:MI:SS'");
		} catch (Exception ex) {
			throw ex;
		} finally {
			s.close();
		}

		return timestampAlter && dateAlter;
	}

	public Oracle(String user, String password) {
		loadConfig();
		setUser(user);
		setPassword(password);
	}

	@Override
	public Connection connect() throws Exception {
		// TODO Auto-generated method stub
		if(getDriver() != null) { Class.forName(getDriver()); }
		
		String connString = "jdbc:oracle:thin:@" + getHost() + ":" + getPort() + ":" + getSid();
		Connection con = DriverManager.getConnection(connString, user, password);

		return con;
	}
}
