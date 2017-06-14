package CacheIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// Simple CacheIO for storing String type values.
public class DefaultStringCacheIO extends CacheIO<String> {

	@Override
	public String read(File inputFile) throws IOException {
		return new String(Files.readAllBytes(Paths.get(inputFile.toURI())));
	}

	@Override
	public boolean write(File outputFile, String value) throws IOException {
		boolean written = false;
		FileOutputStream outStream = null;
		try{
			outStream = new FileOutputStream(outputFile);
			outStream.write(value.getBytes());
			written = true;
		} finally {
			if (outStream != null) outStream.close();
		}
		return written;
	}

	@Override
	public long getBytes(String value) {
		// TODO Auto-generated method stub
		return value.getBytes().length;
	}

}
