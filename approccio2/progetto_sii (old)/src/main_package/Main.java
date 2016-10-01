package main_package;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Main {
	
	private final static String[] classes = {"anime","astronomy","beer","chemistry","economics",
			"fitness","gardening","hinduism","latin","robotics"};
	private final static String path_prefix = "/home/antonio/Scaricati/Forums/";
	private final static String path_suffix = "/Posts.xml";
	private final static String tagName = "row";
	private final static double confidence = 0.5;

	public static void main(String[] args) throws Exception {
		
		build_arff();
		
	}
	
	public static void build_arff() throws IOException {
		
		PrintWriter train_writer = new PrintWriter("train.arff", "UTF-8");
		PrintWriter test_writer = new PrintWriter("test.arff", "UTF-8");
		
		init_arff(train_writer);
		init_arff(test_writer);
		
		for (String topic : classes) {
			write_arff_from_xml(path_prefix + topic + path_suffix, tagName, topic, train_writer, test_writer);
		}
		
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

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					Post post = new Post(topic, eElement.getAttribute("Body"));
					post.init_features(confidence);
					if (temp % 10 == 0) {
						post.write_to_arff(test_writer, true);
					}
					else {
						post.write_to_arff(train_writer, false);
					}

				}
			}
		   } catch (Exception e) {
			   e.printStackTrace();
		   }		
	}

}
