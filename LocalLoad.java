import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class LocalLoad {
	File file;
	FileInputStream fin;
	long size;

	public LocalLoad(String path) throws FileNotFoundException {
		file = new File(path);
		
		size = file.length();
		fin = new FileInputStream(file);
	}
	
	public InputStream getInputStream() {
		return fin;
	}
}
