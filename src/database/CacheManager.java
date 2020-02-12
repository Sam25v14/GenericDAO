package database;

import java.util.HashMap;
import java.util.List;

public class CacheManager {
	static HashMap<Class<?>, List<?>> caches = new HashMap<Class<?>, List<?>>();
	
	public static HashMap<Class<?>, List<?>> getCaches() {
		return caches;
	}

	public static void setCaches(HashMap<Class<?>, List<?>> caches) {
		CacheManager.caches = caches;
	}
}
