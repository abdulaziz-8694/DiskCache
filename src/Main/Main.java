package Main;

import java.io.File;

import CacheIO.DefaultStringCacheIO;
import CacheModel.LRUDiskCache;

// A simple class to try out the DiskCache
public class Main {

	public static void main(String[] args) {
		File cacheDir = new File("./Cache"); 
		LRUDiskCache<String, String> cache = new LRUDiskCache<String, String>(
				cacheDir, 100, new DefaultStringCacheIO());
		for(int i = 0; i<100; i++) {
			cache.put(String.valueOf(i), String.valueOf(i) + " : value");
		}
		for(int i = 0; i<100; i++) {
			String ans = cache.get(String.valueOf(i));
			System.out.println(ans==null ? "Undefined" : ans);
		}
		cache.put(String.valueOf(100), String.valueOf(100) + " : value");
		
		for(int i = 0; i<=100; i++) {
			String ans = cache.get(String.valueOf(i));
			System.out.println(ans==null ? "Undefined" : ans);
		}

	}

}
