package sparqlclient;
import java.util.HashSet;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

/* 
 * set=getUrisFromCategory(categoryname);
 * set=getCategoriesFromUri(uriname);
 * 
 * */

public class JenaUriRetriever {
	
	private static ResultSet executeQuery(String queryString) throws Exception {
		 Query query = QueryFactory.create(queryString) ;
		 QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest("http://dbpedia.org/sparql", query);
		 ResultSet results = qexec.execSelect() ;
		 return results;
	}
	public static HashSet<String> getCategoriesFromUri(String uri) throws Exception{
		HashSet<String> categories=new HashSet<String>();
		String query="SELECT *"
				+ "WHERE {<http://dbpedia.org/resource/"+uri+"> "
				+ "<http://purl.org/dc/terms/subject> "
				+ "?categories .}";
		ResultSet results = executeQuery(query);
		while(results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			String s=soln.getResource("categories").toString();
			
		    categories.add(s.substring(s.lastIndexOf(":")+1));
		}
		return categories;
	}
	public static HashSet<String> getUrisFromCategory(String category) throws Exception{
		HashSet<String> uris=new HashSet<String>();
		final ParameterizedSparqlString queryString = new ParameterizedSparqlString(
				"PREFIX dcterms: <http://purl.org/dc/terms/>"
						+ "SELECT *"
						+ "WHERE { ?s dcterms:subject ?entity }"
                ) {{setNsPrefix( "category", "http://dbpedia.org/resource/Category:" );}}; 
        final String entity = category;
        queryString.setIri( "?entity", queryString.getNsPrefixURI( "category" )+entity );
        
		//String query = " PREFIX category: <http://dbpedia.org/resource/Category:> "
		//		+ "PREFIX dcterms: <http://purl.org/dc/terms/>"
		//		+ "SELECT *"
		//		+ "WHERE { ?s dcterms:subject category:"+category+".}";
		
		ResultSet results = executeQuery(queryString.toString());
		while(results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
            String s=soln.getResource("s").toString();
		    uris.add(s.substring(s.lastIndexOf("/")+1));
			
		}
		return uris;
	}
	public static HashSet<String> getOneLevel(HashSet<String> start) throws Exception{
		HashSet<String> result=new HashSet<String>();
		HashSet<String> categories=new HashSet<String>();
		for(String s:start){
			categories.addAll(getCategoriesFromUri(s));
		}
		for(String c:categories){
			result.addAll(getUrisFromCategory(c));
		}
		return result;
	}
	/*
	public static ArrayList<HashSet<String>> getChain(HashSet<String> init, int nlevel) throws Exception{
		ArrayList<HashSet<String>> result=new  ArrayList<HashSet<String>>();
		result.add(init);
		for(int i=0;i<nlevel;i++){
			HashSet<String> aux=getOneLevel(result.get(i));
			System.out.println(aux);
			result.add(aux);
		}
		return result;
	}
	*/
}
