package org.apache.solr;

import org.apache.lucene.util.TestUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TestJSONFacetAPI {


    public static void main(String args[]) throws IOException, SolrServerException, InterruptedException {
        TestJSONFacetAPI test = new TestJSONFacetAPI();
        test.index();
    }

    public void index() throws IOException, SolrServerException {
        Random r = new Random();
        CloudSolrClient client = new CloudSolrClient("localhost:9983");
        client.deleteByQuery("gettingstarted", "*:*");

        List<SolrInputDocument> docs = new ArrayList<>(10000);

        for (int i = 0; i < 10000000; i++) {
            SolrInputDocument document = new SolrInputDocument();

            document.addField("id", i);
            document.addField("top_facet_s", i % 1000);
            document.addField("sub_facet_unique_s", TestUtil.randomSimpleString(r, 3, 10) + " " + TestUtil.randomSimpleString(r, 3, 10));
            document.addField("sub_facet_unique_td", i);

            document.addField("sub_facet_limited_s", i % 5);
            document.addField("sub_facet_limited_td", i % 5);


            docs.add(document);
            if (i % 10000 == 0) {
                client.add("gettingstarted", docs);
                client.commit("gettingstarted");
                docs.clear();
                System.out.println(i);
            }
        }
        client.add("gettingstarted", docs);
        client.commit("gettingstarted");
    }

    public void testLotsOfDeleteByQueries() throws IOException, SolrServerException, InterruptedException {
        CloudSolrClient client = new CloudSolrClient("localhost:9983");
        //assume we have 10M documents with id's starting from 0 sequentially

        //delete 50k docs . 1 batch has 500 deletes
        long startTime = System.nanoTime();
        for (int i = 100; i < 200; i++) {
            StringBuilder deleteQuery = new StringBuilder("id:(");
            for (int j = 0; j < 500; j++) {
                deleteQuery.append(Integer.toString(i * 500 + j));
                if (i != 199 && j != 499) {
                    deleteQuery.append(" OR ");
                }
            }
            deleteQuery.append(")");
            client.deleteByQuery("gettingstarted", deleteQuery.toString());
            Thread.sleep(500);

            SolrParams params = new ModifiableSolrParams().add("q", "*:*")
                    .add("json.facet", "{.....}");
            QueryResponse response = client.query(params);

        }
        System.out.println(TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));
    }


    public void testLotsOfDeleteByID() throws IOException, SolrServerException, InterruptedException {
        CloudSolrClient client = new CloudSolrClient("localhost:9983");
        //assume we have 10M documents with id's starting from 0 sequentially

        //delete 50k docs . 1 batch has 500 deletes
        long startTime = System.nanoTime();
        for (int i = 300; i < 400; i++) {
            List<String> ids = new ArrayList<>(500);
            for (int j = 0; j < 500; j++) {
                ids.add(Integer.toString(i * 500 + j));
            }
            client.deleteById("gettingstarted", ids);
            Thread.sleep(500);
        }
        System.out.println(TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));
    }

}