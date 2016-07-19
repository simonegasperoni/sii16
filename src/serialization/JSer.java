package serialization;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import mainpkg.SemanticFeature;
public class JSer {
	public static boolean writeOnFile(String fileName, Object object){
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
		   fileOutputStream = new FileOutputStream(fileName);
		   objectOutputStream = new ObjectOutputStream(fileOutputStream);
		   objectOutputStream.writeObject(object);
		   objectOutputStream.close();
		   fileOutputStream.close();
		   System.out.println("Oggetto correttamente salvato su file.");
		} catch (IOException ex) {  
			return false;
		}
		return true;
	}
	public static boolean addSemanticFeature(String fileName, ArrayList<SemanticFeature> object){
		ArrayList<SemanticFeature> forum=readAnIndexedForumOnFile(fileName);
		forum.addAll(object);
		return writeOnFile(fileName, forum);
	}
	@SuppressWarnings("unchecked")
	public static ArrayList<SemanticFeature> readAnIndexedForumOnFile(String fileName){	
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		ArrayList<SemanticFeature> indexedForum=null;
		try {
		      fileInputStream = new FileInputStream(fileName);
		      objectInputStream = new ObjectInputStream(fileInputStream);
		      indexedForum = (ArrayList<SemanticFeature>) objectInputStream.readObject();
		      objectInputStream.close();
		      fileInputStream.close();
		} 
		catch (IOException ex) { ex.printStackTrace(); } 
		catch (ClassNotFoundException ex) { ex.printStackTrace();	}
		return indexedForum; 
	}
}