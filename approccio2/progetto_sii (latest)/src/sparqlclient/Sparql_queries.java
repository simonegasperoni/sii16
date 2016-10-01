package sparqlclient;

import java.util.ArrayList;

public class Sparql_queries {
	
	private static final String resourcePrefix = "http://dbpedia.org/resource/";
	
	public static String beer_relationships_count_query(String uri) {
		String r = "<" + resourcePrefix + uri + ">";
		String query =
				"select (COUNT(*) AS ?count) where { "
				+ "?r1 <http://dbpedia.org/ontology/ingredient> "+r+" . "
				+ r+" <http://dbpedia.org/ontology/product> ?r2 . "
				+ r+" <http://dbpedia.org/property/products> ?r3 . "
				+ "}";
		return query;
	}
	
	public static String anime_relationships_count_query(String uri) {
		String r = "<" + resourcePrefix + uri + ">";
		String query =
				"select (COUNT(*) AS ?count) where { "
				+ r+" <http://dbpedia.org/ontology/magazine> ?r1 . "
				+ r+" <http://dbpedia.org/ontology/publisher> ?r2 . "
				+ r+" <http://dbpedia.org/ontology/numberOfVolumes> ?r3 . "
				+ r+" <http://dbpedia.org/property/author> ?r4 . "
				+ r+" <http://dbpedia.org/property/director> ?r5 . "
				+ r+" <http://dbpedia.org/property/episodeList> ?r6 . "
				+ r+" <http://dbpedia.org/property/episodes> ?r7 . "
				+ r+" <http://dbpedia.org/property/magazine> ?r8 . "
				+ r+" <http://dbpedia.org/property/music> ?r9 . "
				+ r+" <http://dbpedia.org/property/volumes> ?r10 . "
				+ r+" <http://dbpedia.org/property/volumeList> ?r11 . "
				+ r+" <http://dbpedia.org/property/writer> ?r12 . "
				+ "}";
		return query;
	}
	
	public static String fitness_relationships_count_query(String uri) {
		String r = "<" + resourcePrefix + uri + ">";
		String query =
				"select (COUNT(*) AS ?count) where { "
				+ "?resource <http://dbpedia.org/property/sport> "+r+" . "
				+ "}";
		return query;
	}
	
	public static ArrayList<String> resources_queries(String uri) {
		String r = "<" + resourcePrefix + uri + ">";
		ArrayList<String> queries = new ArrayList<String>();
		String e =
				"select ?resource where { "
				+ "?resource <http://dbpedia.org/ontology/wikiPageRedirects> "+r+" . "
				+ "}";
		queries.add(e);
		e =
				"select ?resource where { "
				+ "?resource <http://dbpedia.org/ontology/wikiPageDisambiguates> "+r+" . "
				+ "}";
		queries.add(e);
		e = 
				"select ?resource where { "
				+ r+" <http://purl.org/dc/terms/subject> ?resource . "
				+ "}";
		queries.add(e);
		e = "select ?resource where { "
			+ r+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?resource . "
			+ "}";
		queries.add(e);
		e = "select ?resource where { "
			+ r+" <http://www.w3.org/2000/01/rdf-schema#seeAlso> ?resource . "
			+ "}";
		queries.add(e);		
		return queries;
	}
	
	public static String subject_query(String uri) {
		String r = "<" + resourcePrefix + uri + ">";
		String query =
				"select ?resource where { "
				+ "?resource <http://dbpedia.org/ontology/wikiPageRedirects> "+r+" . "
				+ "}";
		return query;
	}
	
	public static String disambiguates_query(String uri) {
		String r = "<" + resourcePrefix + uri + ">";
		String query =
				"select ?resource where { "
				+ "?resource <http://dbpedia.org/ontology/wikiPageDisambiguates> "+r+" . "
				+ "}";
		return query;
	}
	

	public static String generateDisambiguatesOfQuery(String uri) {
		String query=
				  "SELECT ?resource " 
				+ "WHERE { "
				+ "?resource <http://dbpedia.org/ontology/wikiPageDisambiguates> <" + resourcePrefix + uri + "> ."
				+ "}";
		return query;
	}
	
	public static String generateRedirectsToOfQuery(String uri) {
		String query=
				  "SELECT ?resource " 
				+ "WHERE { "
				+ "?resource <http://dbpedia.org/ontology/wikiPageRedirects> <" + resourcePrefix + uri + "> ."
				+ "}";
		return query;
	}
	
	public static String generateOntologiesCount(String uri) {
		String query = 
				  "SELECT ?ontology (count (?ontology) AS ?count) "
				+ "WHERE { "
				+ "<" + resourcePrefix + uri + "> ?ontology ?x . "
				+ "FILTER regex(str(?ontology), '^http://dbpedia.org/ontology') . "
				+ "} "
				+ "GROUP BY ?ontology";
		return query;
	}
	
	public static String allOntologies() {
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "SELECT DISTINCT ?ontology WHERE { "
				+ "?ontology a rdf:Property . "
				+ "FILTER regex(str(?ontology), '^http://dbpedia.org/ontology') . "
				+ "} "
				+ "ORDER BY ?ontology";
		return query;
	}
}
