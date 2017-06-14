package Test;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;

import CacheIO.DefaultStringCacheIO;
import CacheModel.LRUDiskCache;

public class LRUDiskCacheTest {
  LRUDiskCache<String, String> cache;
  File cacheDir;
  @BeforeTest
  public void beforeTest() {
	cacheDir = new File("./Cache"); 
	cache = new LRUDiskCache<String, String>(
				cacheDir, 100, new DefaultStringCacheIO());
	for(int i = 0; i<100; i++) {
		cache.put(String.valueOf(i), String.valueOf(i) + " : value");
	}
  }

  @AfterTest
  public void afterTest() {
	cacheDir.delete();
  }


  @Test
  public void get() {
    for(int i = 0; i<90; i++) {
    	Assert.assertEquals(null, cache.get(String.valueOf(i)));
    }
    for(int i = 90; i<100; i++) {
    	Assert.assertEquals(String.valueOf(i) + " : value", cache.get(String.valueOf(i)));
    }
  }

  @Test
  public void put() {
    cache.put(String.valueOf(100), String.valueOf(100) + " : value" );
    Assert.assertEquals(null, cache.get(String.valueOf(90)));
    Assert.assertEquals(null, cache.get(String.valueOf(91)));
    for(int i = 92; i<=100; i++) {
    	Assert.assertEquals(String.valueOf(i) + " : value", cache.get(String.valueOf(i)));
    }
  }
}
