package org.apache.solr;


import org.apache.lucene.util.TestUtil;
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

public class BulkIndexer3 {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static String[] Strings = new String[3];
    private static Random r = new Random();

    public static void main(String args[]) throws Exception {

        final String zkHost = "localhost:9983";
        final CloudSolrClient client = new CloudSolrClient(zkHost);
        final String collection = "test";
        client.setDefaultCollection(collection);

        for (int i = 0; i < 3; i++) {
            Strings[i] = createSentance1(1);
        }

        System.out.println("start :: " + System.currentTimeMillis());

        List<Thread> threads = new ArrayList<>(100);

        final UpdateRequest updateRequest = new UpdateRequest();

        for (int k = 0; k < 1; k++) {
            int index = ThreadLocalRandom.current().nextInt(2);
            Thread t = new Thread() {
                @Override
                public void run() {
                    List<SolrInputDocument> docs = new ArrayList<>();
                    for (int j = 0; j < 10000; j++) {
                    //while (true) {
                        for (int i = 0; i < 1000; i++) {
                            SolrInputDocument document = new SolrInputDocument();
                            document.addField("id", UUID.randomUUID().toString());
                            document.addField("member_id_i", new Random().nextInt(3) % 3);
                            document.addField("subtotal_i", 1000 + new Random().nextInt(3) % 3);
                            document.addField("quantity_l", Math.abs(new Random().nextLong() % 3));
                            document.addField("order_no_s", Strings[new Random().nextInt(3) % 3]);
                            document.addField("ship_addr1_s", Strings[new Random().nextInt(3) % 3]);
                            document.addField("ship_addr2_s", Strings[new Random().nextInt(3) % 3]);
                            document.addField("ship_addr3_s", Strings[new Random().nextInt(3) % 3]);
                            document.addField("ship_addr4_s", Strings[new Random().nextInt(3) % 3]);
                            document.addField("ship_addr5_s", Strings[new Random().nextInt(3) % 3]);
                            for (int z=0; z< 12; z++) {
                                document.addField("ship_addr" + z + "_t", Strings[new Random().nextInt(3) % 3]);
                            }
                            for (int z=0; z< 12; z++) {
                                document.addField("ship_addr" + z + "_l", Math.abs(new Random().nextLong() % 3));
                            }
                            docs.add(document);
                        }
                        UpdateRequest updateRequest = new UpdateRequest();
                        updateRequest.add(docs);
                        try {
                            System.out.println("updateRequest: " + updateRequest);
                            //updateRequest.process(client);
                            NamedList resp = client.request(updateRequest, collection);
                            //
                            log.info("stop here");
                            updateRequest.commit(client, collection);
                        } catch (Exception e) {

                        }
                        docs.clear();
                    }
                }
            };

            threads.add(t);
            t.start();
        }
        for (Thread thread : threads) thread.join();
        updateRequest.commit(client, collection);
        System.out.println("end :: " + System.currentTimeMillis());
        System.exit(0);
    }

    private static String createSentance(int numWords) {
        //Sentence with numWords and 3-7 letters in each word
        StringBuilder sb = new StringBuilder(numWords * 5);
        for (int i = 0; i < numWords; i++) {
            sb.append("abcd");
        }
        return sb.toString();
    }

    private static String createSentance1(int numWords) {
        //Sentence with numWords and 3-7 letters in each word
        StringBuilder sb = new StringBuilder(numWords * 2);
        for (int i = 0; i < numWords; i++) {
            sb.append(TestUtil.randomSimpleString(r, 1, 1));
        }
        return sb.toString();
    }


}
