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
	
	public static HashMap<String,Double> getOntologiesCount(String uri) throws Exception {
		ResultSet results = executeQuery(Sparql_queries.generateOntologiesCount(uri));
		return transformResults(results);	
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
