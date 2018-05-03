package org.apache.solr;


import org.apache.lucene.util.TestUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FacetingPerf {

    private static Random r = new Random();

    private final CloudSolrClient client;
    private final String collection;

    public FacetingPerf(String zkHost, String collection) {
        client = new CloudSolrClient.Builder().withZkHost(zkHost).build();
        this.collection = collection;
    }

    public static void main(String args[]) throws IOException, SolrServerException {
        String collection = "faceting_perf_high_cardinality";
        String zkHost = "ec2-54-86-68-112.compute-1.amazonaws.com:9987";

        FacetingPerf facetingPerf = new FacetingPerf(zkHost, collection);
        facetingPerf.index();
    }

    public void index() throws IOException, SolrServerException {
        List<SolrInputDocument> docs = new ArrayList<>(10000);

        for (int i = 0; i < 2000000; i++) {
            SolrInputDocument document = new SolrInputDocument();

            document.addField("id", i);
            document.addField("top_facet_s", i % 1000);
            document.addField("sub_facet_s", TestUtil.randomSimpleString(r, 3, 10) + " " + TestUtil.randomSimpleString(r, 3, 10));
            document.addField("sub_facet_td", i);


            docs.add(document);
            if (i % 10000 == 0) {
                client.add(collection, docs);
                client.commit(collection);
                docs.clear();
                System.out.println(i);
            }
        }
        client.add(collection, docs);
        client.commit(collection);
    }

}
