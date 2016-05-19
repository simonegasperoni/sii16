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
esempio di file di input nel file demo.xml

------------------------------------------------
SIMILITUDINE POST-DISCUSSIONE:
considerando più importante la presenza di uri rispetto alle categorie (o viceversa)

similitudine tra post p e discussione d
sim(p,d) (w) #uris p_d + (1-w) #categories p_d

* con w compreso tra 0 ed 1 
* con #uris p_d numero di uri in comune
* con #categories p_d numero di categorie in comune tra post e discussione

con frequenze

   #uris p_d = Sum{Exist(uri_p&uri_d)*#occurences}
   #categories p_d = Sum{Exist(category_p&category_d)*#occurences}

(analogamente se vogliamo confrontare i post con le sezioni)
------------------------------------------------
PACKAGE:

* sparqlclient: esegue interrogazioni sparql su dbpedia per trovare nuove uri e nuove categorie
* spotlightclient: spotlight, cerca le uri su dbpedia per tutti i corpi testuali
* xpathquey: modulo responsabile delle interrogazioni xpath sul file xml di input

METODI IMPORTANTI:

sparqlclient: client per sparql.dbpedia.org
getUrisFromCategory(categoryname): uri da categoria
getCategoriesFromUri(uriname): categorie da uri

spotlightclient: client per spotlight
evaluate(stringa): ritorna un array di uri (spotlight)

xpathquery: per interfacciarsi ad un xml mediante interrogazioni xpath
getAttributes(): restituisce titoli discussioni
getPosts(discussion): restituisce un array di post di una data discussione



----------------------------------------------------------------------
TDA SemanticFeature { stringa discussion/corpus, spotlight uris set, sparql uris set (categories) }:
rappresentazione della coppia discussione/post - features
----------------------------------------------------------------------
PSEUDOCODICE DEL MAIN:


getSemanticFeatureForForum: ho così una lista di semantic feature (una per ogni discussione)

	ArrayList di SemanticFeature ris, vettore dei risultati finali
	ArrayList<String> discussions=xmlq.getAttributes(), array delle discussioni
	Per ogni discussione s dell array discussion
			ottengo la lista dei post 'posts' tramite xpath query
                        uris=getResources(posts), le uri dei post nel set uris
                        sf: nuovs feature semantica: SemanticFeature(s);	
			sf.addUri(uris), aggiungo le uri per la discussione corrente
			sf.addOneLevelOfFeaturesByCategories(), aggiungo le categorie di tutte le uri
			ris.add(sf), aggiungo nel vettore dei risultati la 'semantic feature'
	fine ciclo

getSemanticFeatureForPost: ottengo la semantic feature per il post con procedure simili


main:

	FORUM = getSemanticFeatureForForum
        POST = getSemanticFeatureForPost

FORUM E POST SONO LA RAPPRESENTAZIONE DEL FORUM E DEL POST MEDIANTE TDA SEMANTICFEATURE

	getClassification(POST, FORUM)
        restituisce una classifica di discussioni basata sulla metrica di similitudine definita prima

end main

