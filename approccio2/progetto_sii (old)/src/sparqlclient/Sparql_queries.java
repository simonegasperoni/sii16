package sparqlclient;

public class Sparql_queries {
	
	private static final String resourcePrefix = "http://dbpedia.org/resource/";

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
	
	public static String generateOntologiesCount2(String uri) {
		String query = "select ?a (count (?a) as ?count) where { <http://dbpedia.org/resource/Ivy_Bridge_(microarchitecture)> ?a ?b . FILTER regex(str(?a), '^http://dbpedia.org/ontology') . }";
		return query;
	}
}
