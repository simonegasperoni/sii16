package spotlightclient;

/**
 * Copyright 2011 Pablo Mendes, Max Jakob
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dbpedia.spotlight.exceptions.AnnotationException;
import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.Text;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple web service-based annotation client for DBpedia Spotlight.
 *
 * @author pablomendes, Joachim Daiber
 */

public class DBpediaspotlightClient extends AnnotationClient {

	//private final static String API_URL = "http://jodaiber.dyndns.org:2222/";
    //private final static String API_URL = "http://spotlight.dbpedia.org:2222/";
    private final static String API_URL = "http://spotlight.sztaki.hu:2222/";
	//private final static String API_URL = "http://localhost:2222/";
	private double confidence;
	private static final int SUPPORT = 20;

	
	public DBpediaspotlightClient(double c){
		confidence=c;
	}
	public List<DBpediaResource> extract(Text text) throws AnnotationException {

        LOG.info("Querying API.");
		String spotlightResponse;
		try {
			GetMethod getMethod = new GetMethod(API_URL + "rest/annotate/?" +
					"confidence=" + confidence
					+ "&support=" + SUPPORT
					+ "&text=" + URLEncoder.encode(text.text(), "utf-8"));
			getMethod.addRequestHeader(new Header("Accept", "application/json"));

			spotlightResponse = request(getMethod);
		} catch (UnsupportedEncodingException e) {
			throw new AnnotationException("Could not encode text.", e);
		}

		assert spotlightResponse != null;

		JSONObject resultJSON = null;
		JSONArray entities = null;

		try {
			resultJSON = new JSONObject(spotlightResponse);
			entities = resultJSON.getJSONArray("Resources");
		} catch (JSONException e) {
			throw new AnnotationException("Received invalid response from DBpedia Spotlight API.");
		}

		LinkedList<DBpediaResource> resources = new LinkedList<DBpediaResource>();
		for(int i = 0; i < entities.length(); i++) {
			try {
				JSONObject entity = entities.getJSONObject(i);
				resources.add(
						new DBpediaResource(entity.getString("@URI"),
								Integer.parseInt(entity.getString("@support"))));

			} catch (JSONException e) {
                LOG.error("JSON exception "+e);
            }

		}


		return resources;
	}
        //public static void main(String[] args) throws Exception {
        //DBpediaspotlightClient c = new DBpediaspotlightClient ();
        //ArrayList<String> out=c.evaluate("Apache Hive è un'infrastruttura datawarehouse costruita su Hadoop per fornire riepilogo dei dati, interrogazione e analisi. Mentre all'inizio fu sviluppato da Facebook, Apache Hive è ora usato e sviluppato da altre compagnie come Netflix. Amazon mantiene un fork di Apache Hive che include l'Amazon Elastic MapReduce su Amazon Web Services.");
        //System.out.println(out.toString());
//        SpotlightClient c = new SpotlightClient(api_key);
//        List<DBpediaResource> response = c.extract(new Text(text));
//        PrintWriter out = new PrintWriter(manualEvalDir+"AnnotationText-Spotlight.txt.set");
//        System.out.println(response);

    //}



}
