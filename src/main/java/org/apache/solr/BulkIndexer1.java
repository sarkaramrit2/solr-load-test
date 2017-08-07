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

    public class BulkIndexer1 {

    private static Random r = new Random();

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(final String args[]) throws IOException, SolrServerException, Exception {

        String zkHost = args[0]; // 0 - zk-string
        //final String zkHost = "localhost:9983";
        final CloudSolrClient client = new CloudSolrClient(zkHost);
        final String collection = args[1]; // 1- collection-name
        client.setDefaultCollection(collection);

        System.out.println("start :: " +System.currentTimeMillis());

        final String[] inputs_cat = new String[20];
        inputs_cat[0] = "A";
        inputs_cat[1] = "B";
        inputs_cat[2] = "C";
        inputs_cat[3] = "D";
        inputs_cat[4] = "E";
        inputs_cat[5] = "F";
        inputs_cat[6] = "G";
        inputs_cat[7] = "H";
        inputs_cat[8] = "I";
        inputs_cat[9] = "J";
        inputs_cat[10] = "K";
        inputs_cat[11] = "L";
        inputs_cat[12] = "M";
        inputs_cat[13] = "N";
        inputs_cat[14] = "O";
        inputs_cat[15] = "P";
        inputs_cat[16] = "Q";
        inputs_cat[17] = "R";
        inputs_cat[18] = "S";
        inputs_cat[19] = "T";

        final String[] inputs_all = new String[20];
        inputs_all[0] = "A-X";
        inputs_all[1] = "B-X";
        inputs_all[2] = "C-X";
        inputs_all[3] = "D-X";
        inputs_all[4] = "E-X";
        inputs_all[5] = "F-X";
        inputs_all[6] = "G-X";
        inputs_all[7] = "H-X";
        inputs_all[8] = "I-X";
        inputs_all[9] = "J-X";
        inputs_all[10] = "K-X";
        inputs_all[11] = "L-X";
        inputs_all[12] = "M-X";
        inputs_all[13] = "N-X";
        inputs_all[14] = "O-X";
        inputs_all[15] = "P-X";
        inputs_all[16] = "Q-X";
        inputs_all[17] = "R-X";
        inputs_all[18] = "S-X";
        inputs_all[19] = "T-X";

        List<Thread> threads = new ArrayList<>(100);

        UpdateRequest updateRequest;

        for (int k = 0; k < Integer.parseInt(args[2]); k++) { // 2- no of threads
            int index = ThreadLocalRandom.current().nextInt(5);
            Thread t = new Thread() {
                @Override
                public void run() {
                    List<SolrInputDocument> docs = new ArrayList<>();
                    for (int j = 0; j < 100; j++) {
                        for (int i = 0; i < 10000; i++) {
                            SolrInputDocument document = new SolrInputDocument();
                            document.addField("id", ThreadLocalRandom.current().nextLong());
                            for (int x = 0 ; x < Integer.parseInt(args[3]); x ++ ) {
                                document.addField("cat"+x+"_s", createSentance(ThreadLocalRandom.current().nextInt(50)));
                            }
                            //document.addField("add_s", inputs_all[ThreadLocalRandom.current().nextInt(20)]);
                            docs.add(document);
                        }
                        UpdateRequest updateRequest = new UpdateRequest();
                        //updateRequest.setParam("update.chain","regular-update-chain");
                        updateRequest.add(docs);
                        try {
                            client.request(updateRequest, collection);
                            //updateRequest.commit(client, collection);
                        } catch (Exception e) {

                        }
                    }
                }
            };
            threads.add(t);
            t.start();
        }
        updateRequest = new UpdateRequest();
        updateRequest.commit(client, collection);
        for (Thread thread: threads) thread.join();
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
            sb.append(TestUtil.randomSimpleString(r, 3, 7) + " ");
        }
        return sb.toString();
    }

}
