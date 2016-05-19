package mainpkg;
import java.util.HashSet;
import java.util.Iterator;

import sparqlclient.JenaUriRetriever;

public class SemanticFeature {
	private String discussion;
	private HashSet<String> spotlighturis;
	private HashSet<String>sparqluris;
	
	public SemanticFeature(String d){
		discussion=d;
		spotlighturis=new HashSet<String>();
		sparqluris=new HashSet<String>();
	}
	public String toString(){
		return "CORPUS/DISCUSSION: "+ discussion
				+ "\n -> URIS from spotlight: "+spotlighturis.toString()
				+ "\n -> URIS from sparql: "+sparqluris.toString();
	}
	public String geDiscussion(){ return this.discussion; }
	public HashSet<String> getSpotlightUris(){ return this.spotlighturis; }
	public HashSet<String> getSparqlCategories(){ return this.sparqluris; }
	public void setDiscussion(String d){ discussion = d; }
	public void addUri(HashSet<String> uris2){ spotlighturis.addAll(uris2); }
	public void addUri(String l){ spotlighturis.add(l); }
	
	//aggiunge le categorie partendo dal set delle uri
	public void addOneLevelOfFeaturesByCategories() throws Exception{
		Iterator<String> i=spotlighturis.iterator();
		while(i.hasNext()){
			String feature=i.next();
			HashSet<String> categories=(JenaUriRetriever.getCategoriesFromUri(feature));
			sparqluris.addAll(categories);
		}
	}

}
