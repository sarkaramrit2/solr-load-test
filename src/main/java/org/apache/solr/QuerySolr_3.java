package org.apache.solr;


import org.apache.lucene.util.TestUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuerySolr_3 {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static Random r = new Random();

    public static void main(String args[]) throws IOException, SolrServerException, InterruptedException {

        CloudSolrClient client = new CloudSolrClient.Builder().withZkHost("localhost:9983").build();
        client.setDefaultCollection("test4");

        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");

        SolrRequest request = new QueryRequest(query);

        List<String> productTypeFacets = new ArrayList<>();

        QueryResponse response = new QueryResponse(client.request(request), client);
        System.out.println("response: " + response);

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
