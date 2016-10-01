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
		System.out.println("start");
		for (double v : this.features.values()) {
			System.out.println("feature " + v);
			line = line + v + ",";
		}
		/*
		if (test) 
			line = line + "?";		
		else 
			line = line + this.topic;
		*/
		line = line + this.topic;
		System.out.println(line);
		writer.println(line);		
	}
	
	/*
	 * devo inizializzare le features diversamente
	 * il primo metodo, getUris, va bene e non va cambiato
	 * ottiene la lista di uri.
	 * il secondo metodo va modificato per ottenere le features non normalizzate dalle uri.
	 */
	public void init_features(double confidence, LinkedHashMap<String,String[]> keywords, int n)  {
		try {
		ArrayList<String> uris = this.getUris(confidence);
		LinkedHashMap<String,Double> target = util.init_features_list();
		HashMap<String,Double> relationships_features = generate_relationships_features(uris);
		HashMap<String,Double> keyword_features = generate_keyword_features(uris, keywords);
		HashMap<String,Double> other_features = new HashMap<String,Double>();
		other_features.put("dbpedia_entities", (double) uris.size());
		other_features.put("post_length", (double) this.body.length());
		target.putAll(relationships_features);
		target.putAll(keyword_features);
		target.putAll(other_features);
		relationships_features.clear();
		keyword_features.clear();
		other_features.clear();
		this.setFeatures(target);	
		} catch (Exception e) {
			if (n > 10) {
				System.out.println("aborting");
			}
			else {
			System.out.println("exception, retry " + n);
			this.init_features(confidence, keywords, n++);
			}
		}
	}
	
	
	
	public HashMap<String,Double> generate_keyword_features(ArrayList<String> uris, LinkedHashMap<String,String[]> keywords) throws Exception {
		HashMap<String,Double> keyword_features = new HashMap<String,Double>();
		ArrayList<String> string_blob = new ArrayList<String>();
		for (String uri : uris) {
			ArrayList<String> blob = JenaUriRetriever.get_all_relevant_resources(uri);
			string_blob.addAll(blob);
		}
		//adesso ho la lista intera di risorse.
		for (String k : keywords.keySet()) {
			double count = 0;
			for (String keyword : keywords.get(k)) {
				for (String s : string_blob) {
					if (util.containsIgnoreCase(s, keyword))
						count++;
				}
			}
			if (keyword_features.containsKey(k)) {
				keyword_features.put(k, keyword_features.get(k) + count);
			}
			else
				keyword_features.put(k, count);
		}		
		return keyword_features;
	}
	
	//prima tiro fuori il count delle relazioni rilevanti
	public HashMap<String,Double> generate_relationships_features(ArrayList<String> uris) throws Exception {
		HashMap<String,Double> relationships_features = new HashMap<String,Double>();
		for (String uri : uris) {
			LinkedHashMap<String, Double> relationships_count = new LinkedHashMap<String,Double>();
			relationships_count.put("anime_relationships_count", JenaUriRetriever.get_anime_relationships_count(uri));
			relationships_count.put("beer_relationships_count", JenaUriRetriever.get_beer_relationships_count(uri));
			relationships_count.put("fitness_relationships_count", JenaUriRetriever.get_fitness_relationships_count(uri));
			for (String k : relationships_count.keySet()) {
				if (relationships_features.containsKey(k)) {
					relationships_features.put(k, relationships_features.get(k) + relationships_count.get(k));
				}
				else relationships_features.put(k, relationships_count.get(k));
			}
		}
		return relationships_features;
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
