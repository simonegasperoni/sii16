package main_package;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;



public final class util {
	
	public static LinkedHashMap<String,Double> init_features_list() throws IOException {
		LinkedHashMap<String,Double> features_list = new LinkedHashMap<String,Double>();
		BufferedReader fr = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File("features.txt"))));
		String line = "";
		while((line = fr.readLine()) != null) {
			features_list.put(line, 0.0);
		}
		fr.close();
		return features_list;
	}
	
	/*
	Prende in input una lista non ordinata di risorse (stringhe) ed una lista di parole chiave.
	Confronta ogni parola chiave con la lista di risorse e 
	 */
	public static LinkedHashMap<String,Integer> get_keyword_features(String[] resources_list, LinkedHashMap<String,String[]> keywords) {
		LinkedHashMap<String,Integer> keyword_features = new LinkedHashMap<String,Integer>();
		for (String k : keywords.keySet()) {
			int count = 0;
			for (String keyword : keywords.get(k)) {
				for (String resource : resources_list) {
					if (containsIgnoreCase(resource,keyword)) 
						count++;
				}				
			}
			keyword_features.put(k, count);
		}
		return keyword_features;
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
	
	public static boolean containsIgnoreCase(String src, String what) {
	    final int length = what.length();
	    if (length == 0)
	        return true; // Empty string is contained

	    final char firstLo = Character.toLowerCase(what.charAt(0));
	    final char firstUp = Character.toUpperCase(what.charAt(0));

	    for (int i = src.length() - length; i >= 0; i--) {
	        // Quick check before calling the more expensive regionMatches() method:
	        final char ch = src.charAt(i);
	        if (ch != firstLo && ch != firstUp)
	            continue;

	        if (src.regionMatches(true, i, what, 0, length))
	            return true;
	    }

	    return false;
	}
}
