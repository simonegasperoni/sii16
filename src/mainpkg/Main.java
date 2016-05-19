package mainpkg;
import xpathquery.*;
import spotlightclient.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
/*
 * progetto sii aa2015 2016
 * PROSPETTO
 * ------------------------------------------------------------------------
 *  simone gasperoni
 *  antonio d'innocente
 * ------------------------------------------------------------------------  
 * 
 * packages_ 
 *			|
 * 			|_sparqlclient: client per sparql.dbpedia.org
 * 			|               |_getUrisFromCategory(categoryname): uri da categoria
 * 			|               |_getCategoriesFromUri(uriname): categorie da uri
 * 			|       
 * 			|_spotlightclient: client per spotlight
 * 			|				|_evaluate(stringa): ritorna un array di uri (spotlight)
 * 			|
 * 			|_xpathquery: per interfacciarsi ad un xml mediante interrogazioni xpath
 * 							|_getAttributes(): restituisce titoli discussioni
 * 							|_getPosts(discussion): restituisce un array di post di una 
 * 													data discussione
 * 
 *  TDA SemanticFeature { stringa discussion, set spotlight uris, set sparql uris }:
 * 	rappresentazione della coppia discussione - features
 * 
 * 
 */
public class Main {
	
	//dati i post di una discussione il modulo spotlight restituisce le uri in un set
	public static HashSet<String> getResources(ArrayList<String> posts) throws Exception{
		DBpediaspotlightClient sl=new DBpediaspotlightClient();
		HashSet<String> resources=new HashSet<String>();
		Iterator<String> i=posts.iterator();
		String post;
		while(i.hasNext()){
			post=i.next();
			resources.addAll(sl.evaluate(post));
		}
		return resources;
	}
	public static ArrayList<SemanticFeature> getSemanticFeatureForForum(String file) throws Exception{
		ArrayList<SemanticFeature> ris=new ArrayList<SemanticFeature>();
		XMLquery xmlq=new XMLquery(file);
		
		//lista delle discussioni:
		ArrayList<String> discussions=xmlq.getAttributes();
		
		Iterator<String> disc=discussions.iterator();
		
		while(disc.hasNext()){
			
			//s:discussione corrente
			String s=disc.next();
			System.out.println(s);
			
			//posts:tutti i post della discussione s
			ArrayList<String> posts=xmlq.getPosts(s);
			HashSet<String> uris=getResources(posts);
			SemanticFeature sf=new SemanticFeature(s);
			
			sf.addUri(uris);
			sf.addOneLevelOfFeaturesByCategories();
			ris.add(sf);
		}
		return ris;
	}	
	//intersezione tra insiemi hash
	public static HashSet<String> intersect(HashSet<String> hs1, HashSet<String> hs2){
		HashSet<String> result=new HashSet<String>();
		Iterator<String> i1=hs1.iterator();
		while(i1.hasNext()){
			String curr=i1.next();
			if (hs2.contains(curr)) result.add(curr);
		}
		return result;
	}
	public static SemanticFeature getSemanticFeatureForPost(String post) throws Exception{
		SemanticFeature ris=new SemanticFeature(post);
		ArrayList<String> l=new ArrayList<String>();
		l.add(post);
		HashSet<String> uris=getResources(l);
		ris.addUri(uris);
		ris.addOneLevelOfFeaturesByCategories();
		return ris;
	}
	public static void getClassificationBeta(SemanticFeature post, ArrayList<SemanticFeature> forum, double w){
		//ArrayList<String> li=new ArrayList<String>();
		Iterator<SemanticFeature> i=forum.iterator();
		while(i.hasNext()){
			SemanticFeature curr= (SemanticFeature) i.next();
			int uris=intersect(post.getSpotlightUris(),curr.getSpotlightUris()).size();
			int categories=intersect(post.getSparqlCategories(),curr.getSparqlCategories()).size();
			double result= (uris*(w))+(categories*(1-w));
			System.out.println("score per "+curr.geDiscussion()+" : "+result );
			System.out.println("---------------------------------------");
		}
	}
	
	public static void main(String[] args) throws Exception {
		ArrayList<SemanticFeature> ris=getSemanticFeatureForForum("/home/simone/input xml sii/demo.xml");
		Iterator<SemanticFeature> i=ris.iterator();
		while(i.hasNext()){
			System.out.println(i.next().toString());
		}
		
		//prova getClassificationBeta
		System.out.println("----------------------------------------------------");
		SemanticFeature post=getSemanticFeatureForPost("What's the difference in resource usage (cpu, memory, etc) between 60hz and 30hz 4k? I mean if Windows desktop is being laggy at 60hz (while watching 4k video), is it going to be any better at 30hz? I don't have any 4k monitors/tv to test out yet. Just curious about technicalities");
		System.out.println(post);
		System.out.println("----------------------------------------------------");
		
		getClassificationBeta(post,ris, 0.5);
		
	}
}
