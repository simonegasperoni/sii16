package sparqlclient;
import java.util.ArrayList;
import java.util.HashMap;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;


public class JenaUriRetriever {
	
	public static double get_beer_relationships_count(String uri) throws Exception {
		ResultSet results = executeQuery(Sparql_queries.beer_relationships_count_query(uri));
		return get_count(results);
	}
	
	public static double get_anime_relationships_count(String uri) throws Exception {
		ResultSet results = executeQuery(Sparql_queries.anime_relationships_count_query(uri));
		return get_count(results);
	}
	
	public static double get_fitness_relationships_count(String uri) throws Exception {
		ResultSet results = executeQuery(Sparql_queries.fitness_relationships_count_query(uri));
		return get_count(results);
	}
	
	public static HashMap<String,Double> getOntologiesCount(String uri) throws Exception {
		ResultSet results = executeQuery(Sparql_queries.generateOntologiesCount(uri));
		return transformResults(results);	
	}
	
	public static ArrayList<String> get_all_relevant_resources(String uri) throws Exception {
		ArrayList<ResultSet> results_list = new ArrayList<ResultSet>();
		ArrayList<String> string_blob = new ArrayList<String>();
		for (String query : Sparql_queries.resources_queries(uri)) {
			ResultSet results = executeQuery(query);
			results_list.add(results);
		}
		for (ResultSet res : results_list) {
			while (res.hasNext()) {
				QuerySolution soln = res.nextSolution();
				String s = soln.getResource("resource").toString();
				string_blob.add(s.substring(s.lastIndexOf("/")+1));
			}
		}
		return string_blob;
	}
	
	public static ArrayList<String> get_all_dbo() throws Exception {
		ResultSet results = executeQuery(Sparql_queries.allOntologies());
		ArrayList<String> res = new ArrayList<String>();
		while(results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			String s = soln.getResource("ontology").toString();
			res.add(s.substring(s.lastIndexOf("/")+1));
		}
		return res;
	}
	
	private static ResultSet executeQuery(String queryString) throws Exception {
		 Query query = QueryFactory.create(queryString) ;
		 QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://dbpedia.org/sparql", query);
		 ResultSet results = qexec.execSelect() ;
		 return results;
	}
	
	public static double get_count(ResultSet results) {
		QuerySolution soln = results.nextSolution();
		String count = soln.getLiteral("count").toString();
		double c = Double.parseDouble(count.substring(0, count.indexOf("^^")));
		return c;
	}
	
	public static HashMap<String,Double> transformResults(ResultSet results) {
		HashMap<String,Double> res = new HashMap<String,Double>();
		while(results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			String s = soln.getResource("ontology").toString();
			String count = soln.getLiteral("count").toString();
			double c = Double.parseDouble(count.substring(0, count.indexOf("^^")));
			res.put(s.substring(s.lastIndexOf("/")+1),c);
		}
		System.out.println(res.toString());
		return res;
	}
}
