import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class LocalLoad {
	File file;
	FileInputStream fin;

	public LocalLoad(String path) throws FileNotFoundException {
		file = new File(path);
		
		fin = new FileInputStream(file);
	}
	
	public InputStream getInputStream() {
		return fin;
	}
}
