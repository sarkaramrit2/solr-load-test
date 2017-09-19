package org.apache.solr;


import org.apache.commons.collections.map.UnmodifiableMap;
import org.apache.lucene.util.TestUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Map.Entry;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BulkIndexer2 {

    private static Random r = new Random();

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String args[]) throws IOException, SolrServerException, Exception {

        //final String zkHost = "oregon-ms:9983";
        final String zkHost = "localhost:9983";
        final CloudSolrClient client = new CloudSolrClient(zkHost);
        final String collection = "collection1";
        client.setDefaultCollection(collection);

        System.out.println("start :: " +System.currentTimeMillis());

        List<Thread> threads = new ArrayList<>(100);

        final UpdateRequest updateRequest = new UpdateRequest();

        for (int k = 0; k < 2; k++) {
            int index = ThreadLocalRandom.current().nextInt(5);
            Thread t = new Thread() {
                @Override
                public void run() {
                    for (int j = 0; j < 1000; j++) {
                        List<SolrInputDocument> docs = new ArrayList<>();
                        for (int i = 0; i < 1000; i++) {
                            SolrInputDocument document = new SolrInputDocument();
                            document.addField("id", ThreadLocalRandom.current().nextLong());
                            document.addField("cat1_s", createSentance(20));
                            document.addField("cat2_s", createSentance(20));
                            document.addField("cat3_s", createSentance(20));
                            document.addField("cat4_s", createSentance(20));
                            document.addField("cat5_s", createSentance(20));
                            //document.addField("cat1_str", createSentance(20));
                            //document.addField("cat2_str", createSentance(20));
                            //document.addField("cat3_str", createSentance(20));
                            //document.addField("cat4_str", createSentance(20));
                            //document.addField("cat5_str", createSentance(20));
                            //document.addField("add_s", inputs_all[ThreadLocalRandom.current().nextInt(20)]);
                            docs.add(document);
                        }
                        UpdateRequest updateRequest = new UpdateRequest();
                        //updateRequest.setParam("update.chain","regular-update-chain");
                        updateRequest.add(docs);
                        try {
                            client.request(updateRequest, collection);
                        } catch (Exception e) {

                        }
                        docs.clear();
                    }
                    /*try{
                        updateRequest.commit(client, collection);
                    } catch (Exception e) {

                    }*/
                }
            };
            threads.add(t);
            t.start();
        }
        for (Thread thread: threads) thread.join();
        //updateRequest.commit(client, collection);
        System.out.println("end :: " +System.currentTimeMillis());
        System.exit(0);
        /*while (true) {
            client.query(new ModifiableSolrParams().add("q","*:*").add("sort","external_version_field_s desc"));
            client.query(new ModifiableSolrParams().add("q","*:*").add("sort","external_version_field_s asc"));
            client.query(new ModifiableSolrParams().add("q",createSentance(3)).add("sort","external_version_field_s desc"));
        }*/
    }

    private static String createSentance(int numWords) {
        //Sentence with numWords and 3-7 letters in each word
        StringBuilder sb = new StringBuilder(numWords * 5);
        for (int i = 0; i < numWords; i++) {
            sb.append("abcd" + " ");
        }
        return sb.toString();
    }

}
