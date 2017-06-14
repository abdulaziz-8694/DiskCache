package CacheIO;

import java.io.File;
import java.io.IOException;

// Define read and write methods for serialization.
// This would be faster than using Serializable since we are defining
// our own serialization so the compiler won't have to infer it using 
// Reflection API. 
public abstract class CacheIO<V>{
	public abstract V read(File inputFile) throws IOException;
	public abstract boolean write(File outputFile, V value) throws IOException;
	public abstract long getBytes(V value);
}
