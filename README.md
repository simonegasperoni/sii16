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

Le feature ontologiche consistono in risorse "uri" estratte automaticamente da Dbpedia Spotlight in altre "uri" estratte mediante interrogazioni SPARQL che sono le categorie di appartenenza delle prime.
> DBpedia Spotlight – tool for annotating mentions of DBpedia resources in natural language text, providing capabilities useful for Named Entity Recognition, Name Resolution, amongst other information extraction tasks.

Nel file xml di input sono riportati solo i post che rappresentano meglio le discussioni e le sezioni: guarda file di input demo.xml

```
<forum name="hardforum">
      <post id="0bfca667-1e84-468d-85d8-54a765f9050e" section="Video Cards" discussion="Ashes of the Singularity Day 1 Benchmark Preview @ [H]">"Ashes of the Singularity Day 1 Benchmark Preview - The new Ashes of the Singularity game has finally been released on the PC. This game supports DX11 and the new DX12 API with advanced features. In this Day 1 Benchmark Preview we will run a few cards through the in-game canned benchmark comparing DX11 versus DX12 performance and NVIDIA versus AMD performance."</post>
   <post id="6054109d-8179-418e-8b6d-43fd1586fe95" section="Video Cards" discussion="Rise of the Tomb Raider DX11 vs. DX12 Review @ [H]">"Rise of the Tomb Raider DX11 vs. DX12 Review - Rise of the Tomb Raider has recently received a new patch which adds DX12 API support, in addition the patch adds NVIDIA VXAO Ambient Occlusion technology, however just under DX11. In this evaluation we will find out if DX12 is beneficial to the gameplay experience currently and how it impacts certain GPUs."</post>

   ...

   <post id="51e73fa7-fe50-4c8b-bdb8-7782dfe22879" section="Video Cards" discussion="Fallout 4 Patch 1.3 New Image Quality Features @ [H]">"Fallout 4 Patch 1.3 New Image Quality Features - A new patch, version 1.3.47 is out for Fallout 4 and with it brings a couple of big image quality improvements. HBAO+ Ambient Occlusion is added as well as hardware driven Particle Weapon Debris. We will compare these new image quality features and see how they help improve the gameplay experience of Fallout 4."</post>
</forum>

```
La prima fase consiste ovviamente nel processamento di questo file per la determinazione delle risorse uri per ogni discussione, il risultato sarà serializzato in un file dati che rappresenta l'intero forum.

Quando si vuole raccomandare una discussione per un nuovo post, si deve ottenere una rappresentazione del post mediante risorse uri e confrontare  le uri del post con quelle di ciascuna discussione memorizzate dentro il file dati riportando le discussioni che ottengono uno "score" (similitudine) più alto.
Riporto di seguito la metrica utilizzata per la determinazione dello "score" delle discussioni rispetto ad un post 

------------------------------------------------
SIMILITUDINE POST-DISCUSSIONE:

similitudine tra post p e discussione d
sim(p,d) = (w) #urispd + (1-w) #categoriespd

* con w compreso tra 0 ed 1 
* con #urispd numero di uri in comune tra il post e la discussione
* con #categoriespd numero di categorie in comune tra il post e la discussione

Nella raccolta di uri e categorie per discussione, le uri come le categorie si possono ripetere, questo per dare importanza ai concetti che si ripropongono nella discussione, infatti le strutture che memorizzano le uri di spotlight e le categorie non sono insiemi ma liste.
Come è facile notare con il parametro w è possibile regolare l'importanza del numero di occorrenze delle uri rispetto alle categorie.

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

1. STRING indexedForum: file dati che conterrà le semantic feature dell'intero forum
2. DOUBLE confidence:   confidenza per spotlight su dbpedia
3. STRING xmlforum:     file di input in formato xml che contiene discussioni del post nel formato specificato

```
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
```

* evaluatePostInForum: determinazione feature semantiche per post e calcolo degli score delle discussioni rispetto ad un dato forum
  
1. STRING indexedForum: file dati che contiene le semantic feature dell'intero forum
2. DOUBLE confidence:   confidenza per spotlight su dbpedia
3. DOUBLE w:            trade-off importanza occorrenza uri/categoria
4. INT n:               numero delle discussioni con score più alto rispetto al post riportate come output
5. STRING postcorpus:   corpus testuale del nuovo post

```
public static Map<String,Double> evaluatePostInForum(String indexedforum, double confidence,double w, int n, String postcorpus) 
throws Exception{
		SemanticFeature post=getSemanticFeatureForPost(postcorpus, confidence);
		Object forum=JSer.readAnIndexedForumOnFile(indexedforum);
		return getClassificationBeta(n,post,(ArrayList<SemanticFeature>) forum,w);
}
```
