package org.apache.solr;


import org.apache.lucene.util.TestUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuerySolr_2 {

    ModifiableSolrParams queryDefaults = null;

    private static Random r = new Random();

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String args[]) throws IOException, SolrServerException, Exception {

        //String zkHost = "virginia:9983";
        final String zkHost = "34.210.73.96:9983";
        final String collection = "vehicles";
        final CloudSolrClient client = new CloudSolrClient(zkHost);
        client.setDefaultCollection(collection);

        final String json_query =
                "{" +
                        "models: {" +
                        "type: terms," +
                        "field: \"v_model_s\"," +
                        "facet: {" +
                        "year_per_model: {" +
                        "type: terms," +
                        "field: \"v_year_i\"," +
                        "limit: 10," +
                        "facet: {" +
                        "claim_month: {" +
                        "domain: {" +
                        "join: {" +
                        "from: \"vin_s\"," +
                        "to: \"vin_s\"" +
                        "}," +
                        "filter: \"doc_type_s:claim\"" +
                        "}," +
                        "type: terms," +
                        "field: \"claim_opcode_s\"," +
                        "limit: 1" +
                        "}" +
                        "}" +
                        "}" +
                        "}" +
                        "}" +
                        "}";

        List<Thread> threads = new ArrayList<>(1000000);


        int i=Integer.parseInt(args[0]);

        while(true) {

            int j=++i*100;
            System.out.println("simultaneous theads: " + j);

            for (int k = 0; k < (int)j; k++) {

                Thread t = new Thread() {
                    @Override
                    public void run() {

                        try {
                            QueryResponse response = client.query(new ModifiableSolrParams().add("q", "doc_type_s:vehicle").
                                    add("json.facet", json_query).
                                    add("indent", "true")
                            );
                        } catch (Exception e) {
                            System.err.println(e);
                        }

                    }
                };
                threads.add(t);
                t.start();
            }
            for (Thread thread : threads) thread.join();
        }

    }

    private static String createSentance(int numWords) {
        //Sentence with numWords and 3-7 letters in each word
        StringBuilder sb = new StringBuilder(numWords * 5);
        for (int i = 0; i < numWords; i++) {
            sb.append(TestUtil.randomSimpleString(r, 3, 7) + " ");
        }
        return sb.toString();
    }

    public ModifiableSolrParams queryDefaults() {
        if (queryDefaults == null) {
            queryDefaults = new ModifiableSolrParams();
        }
        return queryDefaults;
    }
}
