package main_package;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import sparqlclient.JenaUriRetriever;
import spotlightclient.DBpediaspotlightClient;

public class Post implements Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UUID id;
	private String topic;
	private String body;
	private LinkedHashMap<String,Double> features;
	
	public Post(String topic, String body) {
		
		this.setId(UUID.randomUUID());
		this.setTopic(topic);
		this.setBody(body);		
		this.setFeatures(new LinkedHashMap<String,Double>());
		
	}
	
	public void write_to_arff(PrintWriter writer, boolean test) {
		String line = "";
		int counter = 0;
		for (double v : this.features.values()) {
			counter++;
			line = line + v + ",";
		}
		if (test) 
			line = line + "?";		
		else 
			line = line + this.topic;
		if (counter == 2697)
			writer.println(line);		
	}
	
	public void init_features(double confidence) throws Exception {
		ArrayList<String> uris = this.getUris(confidence);
		HashMap<String,Double> nn_features = generate_nn_features(uris);
		HashMap<String,Double> features = normalize_features(nn_features);
		features.put("dbpedia_entities", (double) uris.size());
		features.put("post_length", (double) this.body.length());
		LinkedHashMap<String,Double> target = xml_util.all_features();
		target.putAll(features);
		features.clear();
		this.setFeatures(target);		
	}
	
	public HashMap<String,Double> generate_nn_features(ArrayList<String> uris) throws Exception {
		HashMap<String,Double> nn_features = new HashMap<String,Double>();
		for (String uri : uris) {
			HashMap<String, Double> uri_ontologies;
			uri_ontologies = JenaUriRetriever.getOntologiesCount(uri);
			for (String k : uri_ontologies.keySet()) {
				if (nn_features.containsKey(k)) {
					nn_features.put(k, nn_features.get(k) + uri_ontologies.get(k));
				}
				else nn_features.put(k, uri_ontologies.get(k));
			}
		}
		return nn_features;
	}
	
	public HashMap<String,Double> normalize_features(HashMap<String,Double> nn_features) {
		double max = 0;
		HashMap<String,Double> normalized_features = new HashMap<String,Double>();
		for (double v : nn_features.values()) {
			if (v > max) 
				max = v;
		}
		for (String k : nn_features.keySet()) {
			double value = nn_features.get(k) / max;
			normalized_features.put(k, value);
		}
		return normalized_features;
	}
	
	public ArrayList<String> getUris(double confidence) throws Exception{
		DBpediaspotlightClient sl = new DBpediaspotlightClient(confidence);
		ArrayList<String> uris=new ArrayList<String>();
		uris.addAll(sl.evaluate(this.body));		
		return uris;
	}
	
	public void printFeatures() {
		for (String k : this.features.keySet()) {
			System.out.println(k + " : " + this.features.get(k));
		}
	}
    
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public HashMap<String,Double> getFeatures() {
		return features;
	}

	public void setFeatures(LinkedHashMap<String,Double> features) {
		this.features = features;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

}
