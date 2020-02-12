package dao;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import annotations.DAOColumn;
import annotations.DAORelation;
import annotations.DAOTable;
import database.CacheManager;
import database.Database;

public class DAO<T> {
	Database db;

	public static List<Class<?>> tablesInQuery = new ArrayList<Class<?>>();

	public void addToCache(Connection con, Class<T> c) throws Exception {
		CacheManager.getCaches().putIfAbsent(c, null);
		refreshCache(con, c);
	}

	public void refreshCache(Connection con, Class<T> c) throws Exception {
		if (CacheManager.getCaches().containsKey(c)) {
			T obj = (T) c.newInstance();
			List<T> list = this.select(con, obj, null, null, null);

			CacheManager.getCaches().replace(c, list);
		}
	}

	public DAO(Database db) {
		setDb(db);
	}

	public Database getDb() {
		return db;
	}

	public void setDb(Database db) {
		this.db = db;
	}

	/**
	 * Retourne les enregistrements compris entre deb et fin inclus
	 * 
	 * @param con
	 * @param instance
	 * @param tableName
	 * @param cols
	 * @param deb
	 * @param fin
	 * @param where
	 * @param values
	 * @return
	 * @throws Exception
	 */
	public List<T> find(Connection con, T instance, String tableName, String cols, int deb, int fin, String where,
			Object... values) throws Exception {
		Class<?> c = instance.getClass();

		tableName = getTableName(c, tableName);
		cols = getCols(cols);
		String sql = getDb().getSelectSQL(cols, tableName, where, deb, fin);
		List<T> results = executeSelectQuery(con, c, sql, cols, values);

		return results;
	}

	public String getTableFromClass(Class<?> c) {
		String table_name = c.getName();
		int n = table_name.lastIndexOf('.') + 1;
		table_name = table_name.substring(n);

		return table_name;
	}

	private String getTableName(Class<?> c, String tableName) {
		if (tableName == null || tableName.trim() != "") {
			DAOTable table = (DAOTable) c.getDeclaredAnnotation(DAOTable.class);
			tableName = getTableFromClass(c);

			if (table != null && table.name() != null && table.name().trim() != "") {
				tableName = table.name();
			}
		}

		return tableName;
	}
	
	private void setValueOfStatement(PreparedStatement stat, int index, Object value) throws SQLException {
		if(value != null && value.getClass().equals(java.util.Date.class)) {
			java.util.Date d = (java.util.Date)value; 
			value = new Date(d.getTime());
		}
		
		stat.setObject(index, value);
	}
	
	private List<T> executeSelectQuery(Connection con, Class<?> c, String sql, String cols, Object... values)
			throws Exception {
		List<T> results = new ArrayList<T>();

		PreparedStatement stat = null;
		ResultSet res = null;

		try {
			stat = con.prepareStatement(sql);
			List<Field> fields = getFields(c, cols);

			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					setValueOfStatement(stat, i + 1, values[i]);
//					stat.setObject(i + 1, values[i]);
				}
			}

			res = stat.executeQuery();
			DAO.tablesInQuery.add(c);

			while (res.next()) {
				T obj = (T) c.newInstance();

				for (Field f : fields) {
					String att = f.getName();
					Class<?> fieldType = f.getType();

					String foncName = fName("set", att);
					Object temp = null;

					temp = getValue(con, obj, c, f, res);

					Method m = c.getMethod(foncName, fieldType);

					m.invoke(obj, temp);
				}

				results.add(obj);
			}

			DAO.tablesInQuery.remove(c);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (res != null)
				res.close();
			if (stat != null)
				stat.close();
		}

		return results;
	}

	public String getCols(String cols) {
		if (cols == null || cols.trim() == "") {
			cols = "*";
		}

		return cols;
	}

	public List<T> select(Connection con, T instance, String tableName, String cols, String where, Object... values)
			throws Exception {
		Class<?> c = instance.getClass();

		tableName = getTableName(c, tableName);
		cols = getCols(cols);
		String sql = getDb().getSelectSQL(cols, tableName, where);
		List<T> results = executeSelectQuery(con, c, sql, cols, values);

		return results;
	}

	private boolean checkType(Class<?> c) {
		return c.getDeclaredAnnotation(DAOTable.class) != null;
	}

	private Object getValueNative(Field field, ResultSet res, String att) throws Exception {
		String dbGet = fName("get", field.getType().getName());
		Method resultSetM = ResultSet.class.getMethod(dbGet, String.class);
		Object temp = resultSetM.invoke(res, att.toLowerCase());

		return temp;
	}

	private Class<?> getNativeFieldType(Field field) {
		Type listType = field.getGenericType();
		Class<?> fieldT = field.getType();

		if (listType instanceof ParameterizedType) {
			fieldT = (Class<?>) ((ParameterizedType) listType).getActualTypeArguments()[0];
		}
		if (fieldT.isArray()) {
			fieldT = fieldT.getComponentType();
		}

		return fieldT;
	}

	private Object getValue(Connection con, T o, Class<?> c, Field field, ResultSet res) throws Exception {
		Object value = null;

		Class<?> fieldT = getNativeFieldType(field);

		if (checkType(fieldT)) {
			value = getValueNotNative(con, fieldT, field, c, res);
		} else {
			String col = getColFromField(field);
			value = getValueNative(field, res, col);
		}

		return value;
	}

	public String getColFromField(Field f) {
		DAOColumn daoCol = f.getAnnotation(DAOColumn.class);

		if (daoCol != null) {
			return daoCol.name();
		}

		return f.getName();
	}

	private Object getValueNotNative(Connection con, Class<?> fieldT, Field field, Class<?> c, ResultSet res)
			throws Exception {
		Object val = null;

		DAORelation relation = field.getAnnotation(DAORelation.class);

		if (relation == null) {
			throw new Exception("DAORelation must be specified between 2 DAOTable");
		}

		String key = relation.withField();
		String value = relation.onField();
		String where = "WHERE " + key + " = ?";
		Field f = null;

		try {
			f = c.getDeclaredField(value);
		} catch (NoSuchFieldException ex) {
			f = fieldT.getDeclaredField(key);
		}

		Object onFieldValue = getValueNative(f, res, value);

		val = getFieldValue(con, fieldT, field, where, onFieldValue);

		return val;
	}

	public String insert(Connection con, T o, String tableName, String cols, String indice)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, SQLException {
		String key = null;
		Class c = o.getClass();
		if (tableName == null) {
			tableName = getTableName(c, tableName);
		}
		Field[] f = c.getDeclaredFields();
		String sql = getDb().getInsertSQL(tableName, f, cols);
		System.out.println(sql);
		PreparedStatement stat = con.prepareStatement(sql, new String[] { indice });

		try {
			int v = 1;
			for (int i = 0; i < f.length; i++) {
				String att = f[i].getName();
				String nomFonction = fName("get", att);
				Method m = c.getMethod(nomFonction);
				Object obj = m.invoke(o);
				String res = "";

				if (obj == null) {
					res = "null";
				} else {
					res = obj.toString();
				}

				if (!checkSequence(res)) {
					setValueOfStatement(stat, v, obj);
//					stat.setObject(v, obj);
					if (v <= i) {
						v++;
					}
				}

				sql += res;

				if (i != (f.length - 1)) {
					sql += ",";
				}
			}
			stat.executeUpdate();
			ResultSet rs = stat.getGeneratedKeys();
			try {
				if (rs.next()) {
					key = rs.getString(1);
				}
			} catch (Exception ex) {
				throw ex;
			} finally {
				rs.close();
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			stat.close();
		}
		return key;
	}

	private Object getFieldValue(Connection con, Class<?> fieldT, Field field, String where, Object value)
			throws InstantiationException, IllegalAccessException, Exception {
		Object val = null;
		DAO<Object> fieldDAO = new DAO<Object>(getDb());
		Object obj = fieldT.newInstance();

		List<?> results = fieldDAO.select(con, obj, null, null, where, value);
		val = results;

		if (field.getType().isArray()) {
			Object[] tab = (Object[]) Array.newInstance(fieldT, results.size());
			val = field.getType().cast(results.toArray(tab));
		}
		if (field.getType() == fieldT && results.size() > 0) {
			val = results.get(0);
		}

		return val;
	}

	private List<Field> getFields(Class<?> c, String cols) {
		List<Field> fields = new ArrayList<Field>();
		int nbReq = cols.split(",").length;

		while (c != Object.class) {
			Field[] classF = c.getDeclaredFields();

			for (Field f : classF) {
				if (!DAO.tablesInQuery.contains(getNativeFieldType(f))) {
					if (cols.toLowerCase().contains(f.getName().toLowerCase())
							|| cols.toLowerCase().contains(getColFromField(f).toLowerCase())
							|| cols.equalsIgnoreCase("*")) {
						fields.add(f);
						nbReq--;
					}
				}

				if (nbReq == 0 && !cols.equalsIgnoreCase("*")) {
					break;
				}
			}

			c = c.getSuperclass();
		}

		return fields;
	}

	public List<Field> getAllFields(Class<?> c) {
		ArrayList<Field> fields = new ArrayList<Field>();

		while (c != Object.class) {
			Field[] classF = c.getDeclaredFields();

			for (Field f : classF) {
				fields.add(f);
			}

			c = c.getSuperclass();
		}

		return fields;
	}

	public boolean checkSequence(String res) {
		return res.toLowerCase().contains("nextval");
	}

	private String fName(String type, String att) {
		String nomF = "";
		String[] table = att.split("\\.");
		att = table[table.length - 1];

		if (type != null) {
			nomF += type;
		}

		nomF += att.substring(0, 1).toUpperCase() + att.substring(1);

		return nomF;
	}
}
