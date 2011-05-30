import com.ufpr.gdd.file.FileManager;
import com.ufpr.gdd.file.FileManagerException;


public class Main
{
	public static void main(String[] args) {		
	    FileManager file;
	    
	    try {
	    	file = new FileManager(args[0],4024 * 1024);
	    	
	    	file.breakFile();
	    	file.finalize();
	    } catch (FileManagerException e){
	    	e.printStackTrace();
	    }
    }
}
