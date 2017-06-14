package CacheModel;

import java.io.File;

public class Entry {
	public int mHhash;
	public File mFileObject;
	public long mBytes;
	
	public Entry(int hash, File file, long bytes) {
		this.mHhash = hash;
		this.mFileObject = file;
		this.mBytes = bytes;
	}
}
