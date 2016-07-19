package mainpkg;
import xpathquery.*;
import spotlightclient.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import serialization.JSer;
public class Main {
	public static SortedMap<String,Double> putFirstEntries(int max, Map<String, Double> map) {
		int count = 0;
		TreeMap<String,Double> target = new TreeMap<String,Double>();
		for (Map.Entry<String,Double> entry:map.entrySet()) {
		    if (count >= max) break;
		    target.put(entry.getKey(), entry.getValue());
		    count++;
		}
		return target;
	}
	@SuppressWarnings({ "rawtypes" })
	private static <K extends Comparable, V extends Comparable> Map<K, V> sortByValues(Map<K, V> map) {
        List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
        @SuppressWarnings("unchecked")
		@Override
           public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
               return o2.getValue().compareTo(o1.getValue());
           }
        });
        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K, V> sortedMap = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : entries) {
           sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
	//dati i post di una discussione il modulo spotlight restituisce le uri in una lista
	public static ArrayList<String> getResources(ArrayList<String> posts,double confidence) throws Exception{
		DBpediaspotlightClient sl=new DBpediaspotlightClient(confidence);
		ArrayList<String> resources=new ArrayList<String>();
		Iterator<String> i=posts.iterator();
		String post;
		while(i.hasNext()){
			post=i.next();
			resources.addAll(sl.evaluate(post));
		}
		return resources;
	}
	public static ArrayList<SemanticFeature> getSemanticFeatureForForum(String file, double confidence) throws Exception{
		ArrayList<SemanticFeature> ris=new ArrayList<SemanticFeature>();
		XMLquery xmlq=new XMLquery(file);
		
		//lista delle discussioni:
		HashSet<String> discussions=xmlq.getAttributes();
		System.out.println(discussions);
		Iterator<String> disc=discussions.iterator();
		
		while(disc.hasNext()){
			
			//s:discussione corrente
			String s=disc.next();
			System.out.println(s);
			
			//posts:tutti i post della discussione s
			ArrayList<String> posts=xmlq.getPosts(s);
			System.out.println(posts.size());
			ArrayList<String> uris=getResources(posts, confidence);
			SemanticFeature sf=new SemanticFeature(s);
			
			sf.addUri(uris);
			sf.addOneLevelOfFeaturesByCategories();
			ris.add(sf);
		}
		return ris;
	}	
	//intersezione tra insiemi hash
	public static ArrayList<String> intersect(ArrayList<String> hs1, ArrayList<String> hs2){
		ArrayList<String> result=new ArrayList<String>();
		Iterator<String> i1=hs1.iterator();
		while(i1.hasNext()){
			String curr=i1.next();
			if (hs2.contains(curr)) result.add(curr);
		}
		return result;
	}
	public static SemanticFeature getSemanticFeatureForPost(String post, double confidence) throws Exception{
		SemanticFeature ris=new SemanticFeature(post);
		ArrayList<String> l=new ArrayList<String>();
		l.add(post);
		ArrayList<String> uris=getResources(l,confidence);
		ris.addUri(uris);
		ris.addOneLevelOfFeaturesByCategories();
		return ris;
	}
	public static Map<String, Double> getClassificationBeta(int number,SemanticFeature post, ArrayList<SemanticFeature> forum, double w){
		HashMap<String, Double> res=new HashMap<String, Double>();
		Iterator<SemanticFeature> i=forum.iterator();
		while(i.hasNext()){
			SemanticFeature curr= (SemanticFeature) i.next();
			int uris=intersect(curr.getSpotlightUris(),post.getSpotlightUris()).size();
			int categories=intersect(curr.getSparqlCategories(),post.getSparqlCategories()).size();
			double result= (uris*(w))+(categories*(1-w));
			res.put(curr.geDiscussion(),result);
		}
		return sortByValues(putFirstEntries(number,res));
	}
	
//METODI TOP LEVEL:
	
	/* serializeIndexedForum: determinazione delle feature semantiche e serializzazione su file dati
	 * --------------------------------------------------------------------------------------
	 * 
	 *  STRING indexedForum: file dati che conterrà le semantic feature dell'intero forum
	 *  DOUBLE confidence:   confidenza per spotlight su dbpedia
	 *  STRING xmlforum:     file di input in formato xml che contiene discussioni del post nel formato specificato
	 *  BOOLEAN update:      se aggiorniamo un file esistente è 'true'	    
	 *  
	 */
	public static void serializeIndexedForum(String indexedforum, double confidence, String xmlforum, boolean update) throws Exception{
		ArrayList<SemanticFeature> ris=getSemanticFeatureForForum(xmlforum, confidence);
		Iterator<SemanticFeature> i=ris.iterator();
		while(i.hasNext()){
			SemanticFeature s=i.next();
			System.out.println(s.toString());
		}
		if(update) JSer.addSemanticFeature(indexedforum, ris);
		else JSer.writeOnFile(indexedforum,ris);
	}
	/* evaluatePostInForum: determinazione feature semantiche per post e calcolo degli score delle discussioni
	 * --------------------------------------------------------------------------------------
	 * 
	 * STRING indexedForum: file dati che contiene le semantic feature dell'intero forum
	 * DOUBLE confidence:   confidenza per spotlight su dbpedia
	 * DOUBLE w:            trade-off importanza occorrenza uri/categoria
	 * INT n:               numero delle discussioni con score più alto rispetto al post riportate come output
	 * STRING postcorpus:   corpus testuale del nuovo post
	 *
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,Double> evaluatePostInForum(String indexedforum, double confidence,double w, int n, String postcorpus) throws Exception{
		SemanticFeature post=getSemanticFeatureForPost(postcorpus, confidence);
		Object forum=JSer.readAnIndexedForumOnFile(indexedforum);
		return getClassificationBeta(n,post,(ArrayList<SemanticFeature>) forum,w);
	}
	/*
	 * 
	 * MAIN
	 * 
	 */
	public static void main(String[] args) throws Exception {
		//serializeIndexedForum("forum2.bin", 0.8,"/home/simone/input xml sii/outputantonio.xml", false);
		System.out.println(evaluatePostInForum("forum2.bin", 0.9, 0.5, 5,"We are taking the new AMD FX-8150 and giving it the power of Dual and Triple-SLI GeForce GTX 580 video cards. "));
		//ArrayList<SemanticFeature> al=JSer.readAnIndexedForumOnFile("forum2.bin");
		//System.out.println(al);
	}
}
