# sii16
----------------------------------------------------------------------

SISTEMI INTELLIGENTI PER INTERNET
AA 15 16

 *  simone gasperoni
 *  antonio d'innocente

------------------------------------------------------------------------

Task assegnato: 
Titolo: Tecniche di filtraggio dei contenuti più rilevanti nel Web 2.0 (per 2 persone)
Descrizione: Classificazione basata su features estratte dal testo per comprendere se i commenti inviati dagli utenti sui siti web (es. Youtube, forum) sono di particolare rilevanza per la comunità di utenti, ovvero possono essere considerati spam.

Lavoro svolto:
Titolo: Raccomandazione automatica della sezione e delle discussioni per nuovi post mediante l'uso di feature ontologiche
Implementazione di un framework in java che indicizza le sezioni e le discussioni di un forum tramite feature ontologiche estratte con spotlight su dbpedia. Le feature vengono utilizzate da un modulo sparql che estrae le categorie di apparteneza delle uri di spotlight. 
Sia le categorie che le uri sono utilizzate come metrica di similitudine per la determinazione della rilevanza del post rispetto ad una determinata sezione e/o discussione.


nel file xml di input sono riportati solo i post che rappresentano meglio le discussioni e le sezioni:
esempio di file di input: file demo.xml

------------------------------------------------
SIMILITUDINE POST-DISCUSSIONE:

similitudine tra post p e discussione d
sim(p,d) (w) #uris pd + (1-w) #categories pd

* con w compreso tra 0 ed 1 
* con #urispd numero di uri in comune tra il post e la discussione
* con #categoriespd numero di categorie in comune tra il post e la discussione

come è facile notare con il parametro w è possibile regolare l'importanza del numero di occorrenze delle uri rispetto alle categorie

------------------------------------------------
PACKAGE:

* serialization: contiene metodi statici utili x la serializzazione delle semantic feature del forum
* sparqlclient: esegue interrogazioni sparql su dbpedia per trovare le categorie
* spotlightclient: spotlight, cerca le uri su dbpedia per tutti i corpi testuali grazie alle funzioni rest
* xpathquey: modulo responsabile delle interrogazioni xpath sul file xml di input

------------------------------------------------
SemanticFeature

è stato indispensabile definire un tipo astratto di dato: SemanticFeature

TDA SemanticFeature:
	 String: nome discussione o corpus del post
	 ArrayList<String>: uri spotlight
	 ArrayList<String>: uri sparql (categorie)

Il forum viene rappresentato da un lista di SemanticFeature

-------------------------------------------------------------------
Metodi importanti di alto livello:

* serializeIndexedForum: determinazione delle feature semantiche del forum (discussione per discussione) e serializzazione su file dati

STRING indexedForum: file dati che conterrà le semantic feature dell'intero forum
DOUBLE confidence:   confidenza per spotlight su dbpedia
STRING xmlforum:     file di input in formato xml che contiene discussioni del post nel formato specificato

public static void serializeIndexedForum(String indexedforum, double confidence,String xmlforum) 
throws Exception{

		ArrayList<SemanticFeature> ris=getSemanticFeatureForForum(xmlforum, confidence);
		Iterator<SemanticFeature> i=ris.iterator();
		while(i.hasNext()){
			SemanticFeature s=i.next();
			System.out.println(s.toString());
		}
		JSer.writeOnFile(indexedforum,ris);
}

* evaluatePostInForum: determinazione feature semantiche per post e calcolo degli score delle discussioni rispetto ad un dato forum
  
STRING indexedForum: file dati che contiene le semantic feature dell'intero forum
DOUBLE confidence:   confidenza per spotlight su dbpedia
DOUBLE w:            trade-off importanza occorrenza uri/categoria
INT n:               numero delle discussioni con score più alto rispetto al post riportate come output
STRING postcorpus:   corpus testuale del nuovo post

public static Map<String,Double> evaluatePostInForum(String indexedforum, double confidence,double w, int n, String postcorpus) 
throws Exception{

		SemanticFeature post=getSemanticFeatureForPost(postcorpus, confidence);
		Object forum=JSer.readAnIndexedForumOnFile(indexedforum);
		return getClassificationBeta(n,post,(ArrayList<SemanticFeature>) forum,w);
}
