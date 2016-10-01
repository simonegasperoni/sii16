package feature_normalization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedHashMap;

public class Main {
	
	private final static String source_filename = "train_robotics.arff";
	private final static String target_filename = "robotics_normalized.arff";
	
	public static LinkedHashMap<String,Double> normalize_standard(LinkedHashMap<String,Double> features) {
		double max = 1;
		for (double value : features.values()) {
			if (value > max) 
				max = value;
		}
		for (String k : features.keySet()) {
			double number = features.get(k) / max;
			features.put(k, number);
			//System.out.println(number);
		}
		return features;
	}

	public static void main(String[] args) throws IOException {
		
		BufferedReader sr = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(source_filename))));
		
		PrintWriter writer = new PrintWriter(target_filename, "UTF-8");
		
		String line = "";
		
		LinkedHashMap<String,Double> relationship_features = new LinkedHashMap<String,Double>();
		LinkedHashMap<String,Double> keyword_features = new LinkedHashMap<String,Double>();
		LinkedHashMap<String,Double> other_features = new LinkedHashMap<String,Double>();
		String topic = "";
		
		while((line = sr.readLine()) != null) {
			
			String[] features = line.split(","); 
			relationship_features.put("r1", Double.parseDouble(features[0]));
			relationship_features.put("r2", Double.parseDouble(features[1]));
			relationship_features.put("r3", Double.parseDouble(features[2]));
			
			keyword_features.put("k1", Double.parseDouble(features[3]));
			keyword_features.put("k2", Double.parseDouble(features[4]));
			keyword_features.put("k3", Double.parseDouble(features[5]));
			keyword_features.put("k4", Double.parseDouble(features[6]));
			keyword_features.put("k5", Double.parseDouble(features[7]));
			
			other_features.put("o1", Double.parseDouble(features[8]));
			other_features.put("o2", Double.parseDouble(features[9]));
			
			topic = features[10];
			
			normalize_standard(relationship_features);
			normalize_standard(keyword_features);
			
			String write_line = "";
			
			for (double v : relationship_features.values()) {
				write_line = write_line + v + ",";
			}
			for (double v : keyword_features.values()) {
				write_line = write_line + v + ",";
			}
			for (double v : other_features.values()) {
				write_line = write_line + v + ",";
			}
			write_line = write_line + topic;
			writer.println(write_line);			
			
		}
		
		writer.close();
		sr.close();
		
		
		
		
		
	}	
	

}
