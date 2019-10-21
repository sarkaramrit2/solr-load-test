package org.apache.solr;


import org.apache.http.NoHttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.lucene.util.TestUtil;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DBQ_Reordering {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static Random r = new Random();
    private final CloudSolrClient client;
    //private final HttpSolrClient httpSolrClient;
    //private final HttpSolrClient httpSolrClient2;s
    private final String collection;

    public DBQ_Reordering(String zkHost, String collection) {
        client = new CloudSolrClient.Builder().withZkHost(zkHost).build();
        this.collection = collection;
        client.setDefaultCollection(collection);
        //httpSolrClient = new HttpSolrClient("http://127.0.0.1:8983/solr/");
        //httpSolrClient2 = new HttpSolrClient("http://192.168.0.43:8985/solr/");
    }

    public static void main(String args[]) throws Exception {
        int numIndexers = 1;
        String collection = "collection1";
        String zkHost = "54.202.166.159:9983";
        int batchSize = 10;

        DBQ_Reordering bulkIndexer = new DBQ_Reordering(zkHost, collection);
        bulkIndexer.doDBQReordering();
        //bulkIndexer.startIndexing(numIndexers, batchSize);
    }

    private static String createSentance(int numWords) {
        //Sentence with numWords and 3-7 letters in each word
        StringBuilder sb = new StringBuilder(numWords * 5);
        for (int i = 0; i < numWords; i++) {
            sb.append(TestUtil.randomSimpleString(r, 3, 7) + " ");
        }
        return sb.toString();
    }

    public void doDBQReordering() throws Exception {
        final String[] randoms = new String[10];
        for (int i = 0; i < randoms.length; i++) {
            randoms[i] = createSentance(2);
        }

        final List<Thread> threads = new ArrayList<>(100);

        for (int i = 0; i < 1; i++) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    while (true) {

                        List<SolrInputDocument> docs = new ArrayList<>();
                    /*for (int i=0 ;i <50; i++) {
                        SolrInputDocument parent = new SolrInputDocument();
                        parent.addField("id", "parent_" + new Random().nextInt(1000));
                        parent.addField("cat_s", new Random().nextInt(10));
                        docs.add(parent);
                    }*/

                        docs = new ArrayList<>();
                        for (int i = 0; i < 5; i++) {
                            SolrInputDocument parent = new SolrInputDocument();
                            parent.addField("id", "parent_" + new Random().nextInt(1000));
                            parent.addField("cat_s", randoms[new Random().nextInt(5)]);
                            docs.add(parent);
                            //httpSolrClient2.deleteByQuery(collection, dbq2);
                        }

                        try{
                            client.add(docs);
                        } catch (Exception e) {
                        }
                        //httpSolrClient.add(collection, docs);

                        //String dbq1 = "cat_s:" + randoms[new Random().nextInt(10)];
                        String dbq2 = "cat_s:" + randoms[new Random().nextInt(10)];
                        try {
                            System.out.println(client.deleteByQuery(dbq2).getResponse());
                            System.out.println(client.query(new ModifiableSolrParams().add("q", "cat_s:" + randoms[new Random().nextInt(10)])).getResponse());
                            //httpSolrClient2.deleteByQuery(collection, dbq2);
                        } catch (Exception e) {
                        }

                        //httpSolrClient.deleteByQuery(collection, dbq);
                        //httpSolrClient2.deleteByQuery(collection, dbq2);
                        docs = new ArrayList<>();
                        for (int i = 0; i < 5; i++) {
                            SolrInputDocument parent = new SolrInputDocument();
                            parent.addField("id", "parent_" + new Random().nextInt(1000));
                            parent.addField("cat_s", randoms[new Random().nextInt(5)]);
                            docs.add(parent);
                            //httpSolrClient2.deleteByQuery(collection, dbq2);
                        }
                        //List<SolrInputDocument> childs = new ArrayList<>();
      /*for (int i=0; i<10; i++) {
        SolrInputDocument child = new SolrInputDocument();
        child.addField("id","child_" + new Random().nextInt(1000));
        child.addField("cat_s",parent.getFieldValue("cat_s"));
        childs.add(child);
      }*/
                        try{
                            client.add(docs);
                            client.commit("collection1");
                        } catch (Exception e) {
                        }

                        //parent.addChildDocuments(childs);
                    /*try {
                        client.add(docs);
                        //httpSolrClient2.add(collection, parent);
                    //httpSolrClient2.deleteByQuery(collection, dbq2);
                        //httpSolrClient2.add(collection, docs);
                    } catch (Exception e) {
                    }*/
                        //httpSolrClient.add(collection, parent);
                        //httpSolrClient2.add(collection, parent);
                        //client.commit();
                        //httpSolrClient.commit(collection);
                    }
                }
            };
            threads.add(t);
            t.start();
        }
    }

    public void startIndexing(int numIndexers, int batchSize) {
        List<Runnable> indexers = new ArrayList<>(numIndexers);
        for (int i = 0; i < numIndexers; i++) {
            indexers.add(new IndexThread(batchSize));
        }
        for (Runnable indexer : indexers) {
            indexer.run();
        }
    }

    /**
     * Sends a batch of N docs. 2 long description fields.
     * Pause for 1s
     */
    private class IndexThread implements Runnable {

        private final int batchSize;

        public IndexThread(int batchSize) {
            this.batchSize = batchSize;
        }

        @Override
        public void run() {
            List<SolrInputDocument> docs = new ArrayList<>(batchSize);
            while (true) {
                docs.clear();
                for (int j = 0; j < batchSize; j++) {
                    SolrInputDocument document = new SolrInputDocument();
                    document.addField("id", UUID.randomUUID());
//          document.addField("description_txt", createSentance(500));
//          document.addField("long_description_txt", createSentance(1000));
                    docs.add(document);
                }
                sendBatch(docs, 1, 10);
                break;
            }
        }

        protected int sendBatch(List<SolrInputDocument> reuse, int waitBeforeRetry, int maxRetries) {
            int sent;
            try {
                UpdateRequest updateRequest = new UpdateRequest();
                if (reuse.size() == 1) {
                    updateRequest.add(reuse.get(0));
                } else {
                    updateRequest.add(reuse);
                }
                client.request(updateRequest, collection);
                sent = reuse.size();
            } catch (Exception exc) {
                Throwable rootCause = SolrException.getRootCause(exc);
                boolean wasCommError =
                        (rootCause instanceof ConnectException ||
                                rootCause instanceof ConnectTimeoutException ||
                                rootCause instanceof NoHttpResponseException ||
                                rootCause instanceof SocketException);

                if (wasCommError) {
                    if (--maxRetries > 0) {
                        log.warn("ERROR: " + rootCause + " ... Sleeping for "
                                + waitBeforeRetry + " seconds before re-try ...");
                        try {
                            Thread.sleep(waitBeforeRetry * 1000L);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        sent = sendBatch(reuse, waitBeforeRetry, maxRetries);
                    } else {
                        throw new RuntimeException("No more retries available!", exc);
                    }
                } else {
                    throw new RuntimeException(exc);
                }
            }
            return sent;
        }
    }

}
