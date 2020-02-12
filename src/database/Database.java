package database;

import java.lang.reflect.Field;
import java.sql.Connection;

import annotations.DAORelation;
import annotations.DAOSequence;

public abstract class Database {
	String driver, host, port, user, password, database;
	public static final String CONFIG_FILE = "config.properties";

	public abstract Connection connect() throws Exception;

	public String getSelectSQL(String cols, String tableName, String where) {
		String sql = "SELECT " + cols + " FROM " + tableName;

		if (where != null) {
			sql += " " + where;
		}

		return sql;
	}

	public String[] getCols(Field[] f) {
		String[] result = { "", "" };
		for (int i = 0; i < f.length; i++) {
			if (f[i].getAnnotation(DAORelation.class) == null) {
				String attribut = f[i].getName();
				
				if (i > 0) {
					result[0] += ",";
					result[1] += ",";
				}
				
				result[0] += attribut;
				DAOSequence dSequence = f[i].getAnnotation(DAOSequence.class);
				if (dSequence != null) {
					result[1] += getSequence(dSequence.prefix(), dSequence.name());
				} else {
					result[1] += "?";
				}
			}
		}
		
		return result;
	}

	public String getInsertSQL(String tableName, Field[] f, String cols) {
		String[] colns = getCols(f);
		if (cols != null) {
			if (cols.trim() != "") {
				tableName += "(" + cols + ")";
			}
		} else {
			tableName += "(" + colns[0] + ")";
		}
		String sql = "INSERT INTO " + tableName + " values(" + colns[1] + ")";
		return sql;
	}

	public abstract String getSequence(String prefix, String name);

	public abstract void loadConfig();

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public abstract String getSelectSQL(String cols, String tableName, String where, int deb, int fin) throws Exception;
}
