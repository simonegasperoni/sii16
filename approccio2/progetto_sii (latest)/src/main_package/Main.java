package main_package;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Main {
	/*
	private final static String[] classes = {"anime","astronomy","beer","chemistry","economics",
			"fitness","gardening","hinduism","latin","robotics"};
	*/
	private final static String path_prefix = "/home/antonio/Scaricati/Forums/";
	private final static String path_suffix = "/Posts.xml";
	private final static String tagName = "row";
	private final static double confidence = 0.35;
	
	private final static String[] anime_keywords = {"anime","manga","character","comic","fiction"};
	private final static String[] beer_keywords = {"beer","brew","ferment","beverage","carbon","yeast","sugar","bottle","ale"};
	private final static String[] economics_keywords = {"cost","econom","production","currenc","market","exchange","financ","business","management","monetary","policy"};
	private final static String[] fitness_keywords = {"gym","health","physical","exercise","sport","weigth","training","body","physiology","muscle","amino","acid","supplement","protein"};
	private final static String[] robotics_keywords = {"robot","android","automa","control","cybernetic","motor","mechani","servo"};
	
	private final static LinkedHashMap<String,String[]> keywords = new LinkedHashMap<String,String[]>();

	public static void main(String[] args) throws Exception {
		
		init_keywords();
		
		/*
		String body = "Assuming the world in the One Piece universe is round, then there is not really a beginning or an end of the Grand Line.&lt;/p&gt;&#xA;&#xA;&lt;p&gt;The Straw Hats started out from the first half and are now sailing across the second half.&lt;/p&gt;&#xA;&#xA;&lt;p&gt;Wouldn't it have been quicker to set sail in the opposite direction from where they started?";
		
		PrintWriter train_writer = new PrintWriter("train_anime.arff", "UTF-8");
		Post post = new Post("anime",body);
		post.init_features(confidence, keywords, 0);
		post.printFeatures();
		post.write_to_arff(train_writer, false);
		train_writer.close();
		
		*/
		
		build_arff();
		
		
	}
		
	
	public static void init_keywords() {
		keywords.put("anime_keywords", anime_keywords);
		keywords.put("beer_keywords", beer_keywords);
		keywords.put("economics_keywords", economics_keywords);
		keywords.put("fitness_keywords", fitness_keywords);
		keywords.put("robotics_keywords", robotics_keywords);
	}
	
	public static void build_arff() throws IOException {
		
		PrintWriter train_writer = new PrintWriter("train_robotics.arff", "UTF-8");
		PrintWriter test_writer = new PrintWriter("test_robotics.arff", "UTF-8");
		
		/*
		init_arff(train_writer);
		init_arff(test_writer);	
		
		for (String topic : classes) {
			write_arff_from_xml(path_prefix + topic + path_suffix, tagName, topic, train_writer, test_writer);
		}
		*/
		
		
		write_arff_from_xml(path_prefix + "robotics" + path_suffix, tagName, "robotics", train_writer, test_writer);
		
		train_writer.close();
		test_writer.close();		
	}
	
	public static void init_arff(PrintWriter writer) throws IOException {
		writer.println("@relation forum");
		writer.println("");
		BufferedReader fr = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File("features.txt"))));
		String line = "";
		while((line = fr.readLine()) != null) {
			writer.println("@attribute " + line + " real");
		}
		fr.close();
		writer.println("@attribute topic {anime, astronomy, beer, chemistry, economics, fitness, gardening, hinduism, latin, robotics}");
		writer.println();
		writer.println("@data");				
	}
	
	public static void write_arff_from_xml(String path, String tagName, String topic, PrintWriter train_writer, PrintWriter test_writer) {		
		try {
			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			NodeList nList = doc.getElementsByTagName(tagName);
			
			// mettere qui il numero max di post sovrascrivendo list.getLength

			for (int temp = 0; temp < 220; temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					Post post = new Post(topic, eElement.getAttribute("Body"));
					//sono arrivato qui
					post.init_features(confidence, keywords, 0);
					if (temp % 10 == 0) {
						post.write_to_arff(test_writer, false);
						System.out.println("Scritto il post " + temp + " in test");
					}
					else {
						post.write_to_arff(train_writer, false);
						System.out.println("Scritto il post " + temp + " in train");
					}
					
				}
			}
		   } catch (Exception e) {
			   e.printStackTrace();
		   }		
	}

}
