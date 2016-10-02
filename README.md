#Tecniche di filtraggio dei contenuti più rilevanti nel Web 2.0
####PROGETTO DEL CORSO DI SISTEMI INTELLIGENTI PER INTERNET, AA 15 16, Simone Gasperoni, Antonio D'Innocente

Task assegnato: Tecniche di filtraggio dei contenuti più rilevanti nel Web 2.0 (per 2 persone). Descrizione: Classificazione basata su features estratte dal testo per comprendere se i commenti inviati dagli utenti sui siti web (es. Youtube, forum) sono di particolare rilevanza per la comunità di utenti, ovvero possono essere considerati spam.

Ci siamo posti l'obiettivo di sperimentare un sistema di classificazione di post utente mediante features estratte da Dbpedia. La classificazione ha lo scopo di assegnare ad ogni post la sezione del forum più pertinente in base al corpo del testo. Abbiamo scaricato diversi archivi di post, divisi per sezione, dal dump pubblico di StackExchange ed abbiamo selezionato le cinque sezioni: anime, beer, economics, fitness e robotics per la sperimentazione. Da questi post abbiamo quindi estratto delle feature mediante Dbpedia Spotlight ed
interrogazioni SPARQL.

Abbiamo seguito due approcci: il primo si basa sul confronto - semplice - delle features estratte da dbpedia, il secondo - più sofisticato - sfrutta tecniche di machine learning e l'individuazione ad hoc di feature semantiche rappresentative per gli argomenti d'interesse. 

##approccio1
Raccomandazione automatica della sezione e delle discussioni per nuovi post mediante l'uso di feature ontologiche, il framework indicizza le sezioni e le discussioni di un forum tramite feature ontologiche estratte con spotlight su dbpedia. Le feature vengono utilizzate da un modulo sparql che estrae le categorie di apparteneza delle uri di spotlight. 
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

###similitudine post-discussione

similitudine tra post p e discussione d
sim(p,d) = (w) #urispd + (1-w) #categoriespd

* con w compreso tra 0 ed 1 
* con #urispd numero di uri in comune tra il post e la discussione
* con #categoriespd numero di categorie in comune tra il post e la discussione

Nella raccolta di uri e categorie per discussione, le uri come le categorie si possono ripetere, questo per dare importanza ai concetti che si ripropongono nella discussione, infatti le strutture che memorizzano le uri di spotlight e le categorie non sono insiemi ma liste.
Come è facile notare con il parametro w è possibile regolare l'importanza del numero di occorrenze delle uri rispetto alle categorie.

###package

* serialization: contiene metodi statici utili x la serializzazione delle semantic feature del forum
* sparqlclient: esegue interrogazioni sparql su dbpedia per trovare le categorie
* spotlightclient: spotlight, cerca le uri su dbpedia per tutti i corpi testuali grazie alle funzioni rest
* xpathquey: modulo responsabile delle interrogazioni xpath sul file xml di input

###semantic feature

è stato indispensabile definire un tipo astratto di dato SemanticFeature: 
* String: nome discussione o corpus del post
* ArrayList<String>: uri spotlight
* ArrayList<String>: uri sparql (categorie)

Il forum viene rappresentato da un lista di SemanticFeature, vediamo i metodi importanti di alto livello:

* serializeIndexedForum: determinazione delle feature semantiche del forum (discussione per discussione) e serializzazione su file dati

1. STRING indexedForum: file dati che conterrà le semantic feature dell'intero forum
2. DOUBLE confidence:   confidenza per spotlight su dbpedia
3. STRING xmlforum:     file di input in formato xml che contiene discussioni del post nel formato specificato
4. BOOLEAN update:      se 'true' aggiorna un file bin già esistente, altrimenti lo crea o lo sovrascrive

```
public static void serializeIndexedForum(String indexedforum, double confidence, String xmlforum, boolean update) 
throws Exception{
		ArrayList<SemanticFeature> ris=getSemanticFeatureForForum(xmlforum, confidence);
		Iterator<SemanticFeature> i=ris.iterator();
		while(i.hasNext()){
			SemanticFeature s=i.next();
			System.out.println(s.toString());
		}
		if(update) JSer.addSemanticFeature(indexedforum, ris);
		else JSer.writeOnFile(indexedforum,ris);
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

##approccio2
Obiettivo: ottenere più insiemi di post rilevanti per una determinata comunità di utenti (forum). Selezionare tematiche abbastanza diverse tra loro, in modo da sfruttare le caratteristiche di Dbpedia. Inizialmente era stato creato un parser che si connetteva ad un forum, scaricando automaticamente i post per ogni sezione (progetto forumExtractor), tuttavia le sezioni del forum trattavano di diversi tipi di hardware per computer, argomenti troppo collegati tra loro per essere usati in questo progetto.
Per condurre le sperimentazioni e creare il dataset è stato necessario disporre di un ampio numero di post afferenti a topic (sezioni) abbastanza diversi tra loro. I dati sono stati presi dal data dump degli archivi di Stack Exchange (https://archive.org/details/
stackexchange). Ciascun archivio raggruppa tutti i primi post riguardanti un determinato argomento. Nel file Post.xml sono contenute le informazioni sui post presenti nella sezione.

![Alt text](img/data.png?raw=true "dataset")

L'elemento row identifica il post e nell'attributo Body è presente il testo inserito dall'utente. Sono presenti anche altri dati, come titolo del post e conteggio di risposte, che non vengono utilizzati per la classificazione.
Le cinque sezioni (forum) scelti sono stati: anime, beer, economics, fitness, robotics. La classificazione dovrà prendere in input un post e decidere in quali di queste cinque sezioni il post deve essere inserito.

È necessario scandire i file xml e, per ogni post, generare la relativa riga di features in formato .arff. Ci avvaliamo di spotlight, estraendo le uri, quindi ricaviamo le feature da un insieme di relazioni rilevanti e da cinque insiemi di parole chiave, ciascuno contenente parole (stem) attinenti ad uno specifico topic. Dato un post, ed un insieme di parole chiave, sintetizziamo il procedimento nei seguenti passaggi.

1. Estrazione delle uri.
2. Generazione delle feature ricavate dalle relazioni.
3. Generazione delle feature ricavate dalle parole chiave.
4. Generazione di altre feature e scrittura della linea nel file arff.

###introduzione a Dbpedia e query SPARQL
Dbpedia può essere vista come un grafo orientato. Ogni entità dbpedia è collegata ad un valore (che può essere un valore numerico, una stringa o un'altra entità) da una relazione (arco orientato). Tramite SPARQL è possibile interrogare dbpedia fornendo la tripla:
nodo1 – relazione – nodo2. È sufficiente omettere uno qualsiasi (o più) degli elementi che si vuole ricercare, SPARQL si occuperà di estrarre tutte le triple che possono “matchare” con la query descritta. La posizione di nodo1 e nodo2 nella query stabilisce la direzione dell'arco (relazione), in questo caso un arco uscente da nodo1 ed entrante in nodo2. Invertire l'ordine dei nodi equivale a invertire il verso della relazione, in generale i risultati saranno diversi.

###estrazione delle uri
Prima di qualsiasi elaborazione ci siamo posti il problema di estrarre le URI (dbpedia) da ciascun post (corpo testuale), abbiamo utilizzato le funzionalità delle API REST messe a disposizione da Dbpedia Spotlight (https://github.com/dbpedia-spotlight). Dbpedia
Spotlight offre un servizio web che permette di determinare le URI Dbpedia relative ad un testo con un certo livello di confidenza.
Il programma client java che richiede l'elaborazione dei testi per restituire le URI è stato realizzato da Pablo Mendes e Max Jakob, abbiamo trovato questo programma all'interno della repositoy git di Dbpedia Spotlight, abbiamo modificato il metodo evaluate in modo
che prendesse in input il corpo testuale del post e restituisse un HashMap di stringhe contenente la collezione di URI ottenute.

```
public HashSet<String> evaluate(String inputFile) throws Exception { 
   return evaluateManual(inputFile, 0); 
}
```
Una volta ottenuta questa collezione di URI abbiamo proceduto ad ulteriori elaborazioni per la determinazione delle feature.

###features dalle relazioni
Nel contesto del nostro lavoro abbiamo individuato delle relazioni rilevanti per determinati topic. Dati dei post attinenti a determinati topic, le entità dbpedia estratte tendono ad avere alcune relazioni significative, che possono esse stesse aiutare nella classificazione. Ad esempio da molti post della sezione anime si estraggono uri che contengono le relazioni dbp:episodesList e dbp:writer, mentre da molti post della sezione fitness si ricavano analogamente molte occorrenze della relazione is dbp:sport of. Raggruppiamo quindi queste relazioni rilevanti individuate in 3 macro-gruppi: relazioni che compaiono frequentemente in fitness, relazioni che compaiono frequentemente in
beer e relazioni che compaiono frequentemente in anime. Quando generiamo le feature per il nostro post, per ogni uri estratta con spotlight
eseguiamo delle query SPARQL per contare il numero di occorrenze di relazioni relative ad un macro-gruppo. Utilizziamo quindi questo conteggio (normalizzato in seguito) come feature per aiutarci a classificare correttamente il post.
Per i topic economics e robotics non sono state individuate relazioni particolarmente significative, pertanto non creiamo nessun gruppo di relazioni relativo a queste due classi.

###features dalle parole chiave
Dato uno qualsiasi dei topic scelti, abbiamo individuato un gruppo di relazioni “comuni”, come dct:subject e is dbo:wikiPageRedirects of, che puntano a risorse le cui uri spesso contengono molte occorrenze dello stesso set di parole. Per ciascuno dei cinque topic scelti abbiamo individuato un insieme di parole chiave (stem) che occorrono molto spesso all'interno delle uri delle risorse puntate dalle relazioni “comuni”. Usiamo quindi questi cinque gruppi di keywords come cinque features, contando le occorrenze delle parole chiave attinenti a ciascuno dei determinati topic. Per ogni uri estratta da un determinato post procediamo come segue:
* Eseguiamo delle query sparql per ritrovare tutte le uri puntate da queste relazioni comuni.
* Prendiamo solamente la parte descrittiva dell'uri e le aggiungiamo ad una lista “blob di stringhe”.

Una volta ottenuto l'intero “blob” per un deterinato post, confrontiamo ogni parola chiave con ciascuna delle stringhe nel blob e, ogni volta che c'è un matching (la parola stem è contenuta in una delle uri) aumentiamo il conteggio per il gruppo di parole chiave
a cui apparteneva la parola confrontata. Le nostre cinque feature saranno quindi il numero di occorrenze (normalizzato) delle
keywords relative a ciascuno dei cinque argomenti.

###altre features, scrittura del file .arff e normalizzazione
Le altre due features che abbiamo utilizzato sono state lunghezza del post e numero di entità dbpedia rilevato (uri estratte).
Una volta che il set di feature è stato correttamente istanziato, la scrittura della linea del file .arff è banale, si scorre l'elenco ordinato di features (implementato come dizionario collegato) trascrivendo i valori sul file, quindi si inserisce la classe.
Poichè le interrogazioni Dbpedia e SPARQL possono richiedere molto tempo (circa 4 ore per la creazione di un dataset di soli 1000 post), il programma genera in formato .arff solo un conteggio non normalizzato di tutte le feature che abbiamo estratto, la normalizzazione è stata  effettuata separatamente con un piccolo programma che fa un parsing che riscrive una versione normalizzata delle feature. Questo approccio ci ha garantito una maggiore flessibilità, nel caso avessimo avuto bisogno di provare diversi metodi di normalizzazione.
I valori delle feature relative al conteggio delle relazioni rilevanti per topic sono stati normalizzati in [0,1], così come i valori relativi al conteggio delle parole chiavi attinenti a ciascun topic.

###sperimentazioni su weka
Per quanto riguarda la classificazione abbiamo eseguito alcuni k-fold-cross-validation-test (k=10) su diverse tipologie di modelli ottenendo una precisione a cavallo tra il 75% e l'84%. Abbiamo fatto i test con il training set ed un altro training set ottenuto con i valori delle feature normalizzati tra 0 e 1. Per valutare la precisione abbiamo utilizzato Weka 3.6.0, caricando i file arff ed eseguendo gli addestramenti mediante Workbench.

![Alt text](img/prova.png?raw=true "weka")

Il training set che abbiamo utilizzato ha 990 esempi 198 per ogni classe di esempi (per ogni sezione del forum abbiamo creato 198 esempi). Come si può facilmente notare dal grafico, tutti i modelli di apprendimento che abbiamo usato traggono beneficio dalla normalizzazione dei valori delle feature. Alleghiamo alla relazione i log dei test effettuati su weka (nella cartella logs weka).

###conclusioni
Il classificatore migliore (naive bayes) classifica con una precisione del 83.43% mediante la tecnica 10-fold cross-validation.
Le feature estratte, ad esclusione della lunghezza del post, provengono tutte da dbpedia. Per alcuni post ci risulta difficile ottenere una classificazione accurata con le tecniche utilizzate, per esempio alcuni post in robotics che descrivono soccer robot vengono erroneamente classificati in fitness, come pure alcuni post in anime che descrivono robot vengono erroneamente classificati in robotics.
