package org.apache.solr;


import org.apache.commons.collections.map.UnmodifiableMap;
import org.apache.lucene.util.TestUtil;
import org.apache.poi.util.SystemOutLogger;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.Query;

import static java.util.Map.Entry;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.*;

public class QuerySolr {

    ModifiableSolrParams queryDefaults = null;

    private static Random r = new Random();

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String args[]) throws IOException, SolrServerException {

        //String zkHost = "virginia:9983";
        String zkHost = "localhost:9983";
        CloudSolrClient client = new CloudSolrClient(zkHost);
        String collection = "collection1";
        client.setDefaultCollection(collection);

        String json_query =
                "{random_s:{type:terms,field:random_s,sort:'count asc',limit:-1,overrequest:0,refine:true}}";

        QueryResponse response = client.query(new ModifiableSolrParams().add("q", "*:*").
                        add("json.facet", json_query).
                        add("_facet_", "{refine:{random_s:{_l:[B]}}}}").
                        add("debug", "true").
                        add("wt", "json").
                        add("shards", "" +
                                "http://localhost:8983/solr/collection1_shard1_replica_n1,http://localhost:8983/solr/collection1_shard2_replica_n1" +
                                "").
                        add("isShard", "true")
                //add("shard.url","http://127.0.0.1:8983/solr/collection1_shard2_0_replica_n1/")
        );

        System.out.println(response);
        System.exit(0);

        //client.query(new ModifiableSolrParams().add("q","*:*").add("sort","external_version_field_s desc"));

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

    public ModifiableSolrParams queryDefaults() {
        if (queryDefaults == null) {
            queryDefaults = new ModifiableSolrParams();
        }
        return queryDefaults;
    }
}
