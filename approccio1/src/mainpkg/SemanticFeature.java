package mainpkg;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import sparqlclient.JenaUriRetriever;

public class SemanticFeature implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String discussion;
	private ArrayList<String> spotlighturis;
	private ArrayList<String>sparqluris;
	
	
	public SemanticFeature(String d){
		discussion=d;
		spotlighturis=new ArrayList<String>();
		sparqluris=new ArrayList<String>();
	}
	public String toString(){
		return "\n CORPUS/DISCUSSION: "+ discussion
				+ "\n -> URIS from spotlight: "+spotlighturis.toString()
				+ "\n -> URIS from sparql: "+sparqluris.toString();
	}
	public String geDiscussion(){ return this.discussion; }
	public ArrayList<String> getSpotlightUris(){ return this.spotlighturis; }
	public ArrayList<String> getSparqlCategories(){ return this.sparqluris; }
	public void setDiscussion(String d){ 
		discussion = d; 
	}
	public void addUri(ArrayList<String> uris2){ 
		spotlighturis.addAll(uris2); 
	}
	public void addUri(String l){ 
		spotlighturis.add(l); 
	}
	
	//aggiunge le categorie partendo dal set delle uri
	public void addOneLevelOfFeaturesByCategories() throws Exception{
		Iterator<String> i=spotlighturis.iterator();
		while(i.hasNext()){
			String feature=i.next();
			ArrayList<String> categories=(JenaUriRetriever.getCategoriesFromUri(feature));
			sparqluris.addAll(categories);
		}
	}

}
