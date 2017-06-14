package CacheModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.swing.filechooser.FileFilter;

import CacheIO.CacheIO;

public class LRUDiskCache<K, V>{
	private File mCacheDir;
	private long maxBytes;
	private LinkedHashMap<Integer, Entry> mCacheEntries;
	private long mCurrentBytes;
	CacheIO<V> mCacheIO;
	
	public LRUDiskCache(File cacheDir, long maxByteSize, CacheIO<V> cacheIO) {
		this.mCacheDir = cacheDir;
		this.maxBytes = maxByteSize;
		this.mCacheIO = cacheIO;
		//Put the entries in the access order. Special constructor for LinkedHashMap
		this.mCacheEntries = new LinkedHashMap<>(20, 0.8f, true);
		mCacheDir.mkdirs(); // make dir if it's not there.
		//Build cache if the cache files are already there.
		ArrayList<File> files = new ArrayList<>(Arrays.asList(mCacheDir.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".cache");
		    }
		})));
		// The LRU order will be in the order of last modified. 
		// Since in LRU the newly added entry is at the last we will
		// arrange the entries in the order old to new. This assumption
		// might be wrong but we will be losing the order only for the first 
		// time. Since it is a cache, it is acceptable.
		Collections.sort(files, new Comparator<File>() {

			@Override
			public int compare(File arg0, File arg1) {
				long timeDiff = arg0.lastModified() - arg1.lastModified();
				return timeDiff > 0 ? -1 : timeDiff < 0 ? 1 : 0;
			}
			
		});
		for(File file : files) {
			if(file.isFile()) {
				String fileName = file.getName().substring(0, 
						file.getName().length() - 6);
				addEntry(new Entry(Integer.parseInt(fileName), file, file.length()));
			}
		}
	}
	
	public synchronized V get(K key) {
		Entry entry = mCacheEntries.get(key.hashCode());
		if(entry != null) {
			try {
				return mCacheIO.read(entry.mFileObject);
			} catch (IOException e) {
				return null;
			}
		}
		return null;
	}

	public synchronized boolean put(K key, V value) {
		int index = key.hashCode();
		Entry entry = mCacheEntries.get(index);
		if(entry != null) {
			deleteEntry(entry, index);
		}
		
		long size = mCacheIO.getBytes(value);
		if(size > maxBytes) {
			return false;
		} else {
			adjustCache(size);
		}
		
		File file = new File(mCacheDir, String.valueOf(index) + ".cache");
		try {
			mCacheIO.write(file, value);
			Entry newEntry = new Entry(index, file, size);
			addEntry(newEntry);
			return true;
		} catch(IOException e) {
			return false;
		}
	}

	private void adjustCache(long bytes) {
		while(bytes > maxBytes - mCurrentBytes && !(mCacheEntries.size() <= 0)) {
			Iterator<Entry> iter = mCacheEntries.values().iterator();
			Entry entry = iter.next();
			if(entry != null) {
				deleteEntry(entry, entry.mHhash);
			}
		}
	}

	private void addEntry(Entry entry) {
		mCurrentBytes += entry.mBytes;
		mCacheEntries.put(entry.mHhash, entry);
		System.out.println("INFO: " + mCurrentBytes + " " + maxBytes);
	}

	private void deleteEntry(Entry entry, int index) {
		entry.mFileObject.delete();
		mCacheEntries.remove(index);
		mCurrentBytes -= entry.mBytes;
	}
}
