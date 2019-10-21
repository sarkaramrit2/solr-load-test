/*
package org.apache.solr;


import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class BulkIndexer1 {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static Random r = new Random();

    public static void main(final String args[]) throws IOException, SolrServerException, Exception {

        final String zkHost = args[0]; // 0 - zk-string
        //final String zkHost = "localhost:9983";
        final String collection = args[1]; // 1- collection-name

        log.info("start :: " + System.currentTimeMillis());
        System.out.println("start :: " + System.currentTimeMillis());

        List<Thread> threads = new ArrayList<>(100);

        UpdateRequest updateRequest;

        for (int k = 0; k < Integer.parseInt(args[2]); k++) { // 2- no of threads
            int index = ThreadLocalRandom.current().nextInt(5);
            Thread t = new Thread() {
                @Override
                public void run() {
                    CloudSolrClient client = new CloudSolrClient(zkHost);
                    client.setDefaultCollection(collection);
                    for (int j = 0; j < Integer.parseInt(args[4]); j++) {
                        List<SolrInputDocument> docs = new ArrayList<>();
                        for (int i = 0; i < 1; i++) {
                            SolrInputDocument document = new SolrInputDocument();
                            document.addField("id", UUID.randomUUID().toString());
                            String input = createSentance(20);
                            for (int x = 0; x < Integer.parseInt(args[3]); x++) { // 3 - no of string fields / index=true / stored=true
                                document.addField("cat" + x + "_s", input);
                            }
                            for (int x = 0; x < Integer.parseInt(args[3]); x++) { // 3 - no of string fields / docValues = true
                                document.addField("cat" + x + "_str", input);
                            }
                            //document.addField("add_s", inputs_all[ThreadLocalRandom.current().nextInt(20)]);
                            docs.add(document);
                        }
                        UpdateRequest updateRequest = new UpdateRequest();
                        //updateRequest.setParam("update.chain","regular-update-chain");
                        updateRequest.add(docs);
                        try {
                            NamedList resp = client.request(updateRequest, collection);
                            //updateRequest.commit(client, collection);
                        } catch (Exception e) {

                        }
                        docs.clear();

                        */
/*if (j%10 == 4) {
                            try{
                                UpdateRequest updateRequest2 = new UpdateRequest();
                                updateRequest2.commit(client, collection);
                            } catch (Exception e) {

                            }
                        }*//*

                    }
                }
            };
            threads.add(t);
            t.start();
        }
        CloudSolrClient client = new CloudSolrClient(zkHost);
        for (Thread thread : threads) thread.join();
        updateRequest = new UpdateRequest();
        updateRequest.commit(client, collection);
        log.info("end :: " + System.currentTimeMillis());
        System.out.println("end :: " + System.currentTimeMillis());
        System.exit(0);
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
*/
