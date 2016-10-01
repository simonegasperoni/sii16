package main_package;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;


import sparqlclient.JenaUriRetriever;

public final class xml_util {	
	
	
	public static void generate_ontologies() throws Exception {
		ArrayList<String> dbo = JenaUriRetriever.get_all_dbo();
		PrintWriter writer = new PrintWriter("features.txt", "UTF-8");
		for (String ontology : dbo) {
			writer.println(ontology);
		}
		writer.println("dbpedia_entities");
		writer.println("post_length");	
		writer.close();
	}
	
	
	public static LinkedHashMap<String,Double> all_features() throws IOException {
		LinkedHashMap<String,Double> features = new LinkedHashMap<String,Double>();
		BufferedReader fr = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File("features.txt"))));
		String line = "";
		while((line = fr.readLine()) != null) {
		    features.put(line, 0.0);
		}
		fr.close();
		return features;
	}
}
