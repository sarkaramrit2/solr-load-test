package org.apache.solr;


import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.lucene.util.TestUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
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
        final String collection = args[0];
        HttpClient httpClient;
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set(HttpClientUtil.PROP_MAX_CONNECTIONS, 2048);
        params.set(HttpClientUtil.PROP_MAX_CONNECTIONS_PER_HOST, 1024);
        params.set(HttpClientUtil.PROP_FOLLOW_REDIRECTS, false);
        httpClient = HttpClientUtil.createClient(params);
        final CloudSolrClient client = new CloudSolrClient(zkHost, httpClient);
        System.out.println(client.getHttpClient().getParams());
        client.setDefaultCollection(collection);

        final String json_q1 = "{\n" +
                "    models: {\n" +
                "        type: terms,\n" +
                "        field: \"v_model_s\",\n" +
                "        limit: 10,\n" +
                "\t\t\t\trefine: true,\n" +
                "        facet: {\n" +
                "            year_per_model: {\n" +
                "                type: terms,\n" +
                "                field: \"v_year_i\",\n" +
                "                limit: 10,\n" +
                "                facet: {\n" +
                "                    claim_month: {\n" +
                "                        domain: {\n" +
                "                            join: {\n" +
                "                                from: \"vin_s\",\n" +
                "                                to: \"vin_s\"\n" +
                "                            },\n" +
                "                            filter: \"doc_type_s:claim\"\n" +
                "                        },\n" +
                "                        type: terms,\n" +
                "                        field: \"claim_opcode_s\",\n" +
                "                        limit: 10,\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\tfacet: {\n" +
                "\t\t\t\t                    defect_shop: {\n" +
                "\t\t\t\t                        domain: {\n" +
                "\t\t\t\t                            join: {\n" +
                "\t\t\t\t                                from: \"vin_s\",\n" +
                "\t\t\t\t                                to: \"vin_s\"\n" +
                "\t\t\t\t                            },\n" +
                "\t\t\t\t                            filter: \"doc_type_s:defect\"\n" +
                "\t\t\t\t                        },\n" +
                "\t\t\t\t                        type: terms,\n" +
                "\t\t\t\t                        field: \"defect_shop_s\",\n" +
                "\t\t\t\t                        limit: 10\n" +
                "\t\t\t\t                    }\n" +
                "\t\t\t\t                }\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}\n";

        final String json_q2 =
                "{\n" +
                        "    models: {\n" +
                        "        type: terms,\n" +
                        "        field: \"v_model_s\",\n" +
                        "        limit: 10,\n" +
                        "\t\t\t\trefine: true,\n" +
                        "        facet: {\n" +
                        "            year_per_model: {\n" +
                        "                type: terms,\n" +
                        "                field: \"v_year_i\",\n" +
                        "                limit: 10,\n" +
                        "                facet: {\n" +
                        "                    claim_month: {\n" +
                        "                        domain: {\n" +
                        "                            join: {\n" +
                        "                                from: \"vin_s\",\n" +
                        "                                to: \"vin_s\"\n" +
                        "                            },\n" +
                        "                            filter: \"doc_type_s:claim\"\n" +
                        "                        },\n" +
                        "                        type: terms,\n" +
                        "                        field: \"claim_opcode_s\",\n" +
                        "                        limit: 10\n" +
                        "                    }\n" +
                        "                }\n" +
                        "            }\n" +
                        "        }\n" +
                        "    }\n" +
                        "}\n";

        final String json_q3 = "{\n" +
                "    defects: {\n" +
                "        type: terms,\n" +
                "        field: \"defect_shop_s\",\n" +
                "        limit: 10,\n" +
                "\t\t\t\trefine: true,\n" +
                "\t\t\t\tdomain: {\n" +
                "\t\t\t\t\t\tjoin: {\n" +
                "\t\t\t\t\t\t\t\tfrom: \"vin_s\",\n" +
                "\t\t\t\t\t\t\t\tto: \"vin_s\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\tfilter: \"doc_type_s:defect\"\n" +
                "\t\t\t\t}\n" +
                "    }\n" +
                "}\n" +
                "\n";

        List<Thread> threads = new ArrayList<>(50);


        //int i=Integer.parseInt(args[0]);

        final List<Integer> avg = new ArrayList<Integer>();
        avg.add(0);

        //while() {

            //int j=i;
            //System.out.println("simultaneous theads: " + j);
            long start = System.currentTimeMillis();

            for (int k = 0; k < 2; k++) {

                Thread t = new Thread() {
                    @Override
                    public void run() {

                        try {
                            QueryResponse response = client.query(new ModifiableSolrParams().add("q", "doc_type_s:vehicle").
                                    add("json.facet", json_q1)
                            );
                            avg.set(0,avg.get(0) + response.getQTime());
                        } catch (Exception e) {
                            System.err.println(e);
                        }

                    }
                };
                threads.add(t);
                t.start();
            }
        for (int k = 0; k < 2; k++) {

            Thread t = new Thread() {
                @Override
                public void run() {

                    try {
                        QueryResponse response = client.query(new ModifiableSolrParams().add("q", "doc_type_s:vehicle").
                                add("json.facet", json_q2)
                        );
                        avg.set(0,avg.get(0) + response.getQTime());
                    } catch (Exception e) {
                        System.err.println(e);
                    }

                }
            };
            threads.add(t);
            t.start();
        }
        for (int k = 0; k < 12; k++) {

            Thread t = new Thread() {
                @Override
                public void run() {

                    try {
                        QueryResponse response = client.query(new ModifiableSolrParams().add("q", "doc_type_s:vehicle").
                                add("json.facet", json_q3)
                        );
                        avg.set(0,avg.get(0) + response.getQTime());
                    } catch (Exception e) {
                        System.err.println(e);
                    }

                }
            };
            threads.add(t);
            t.start();
        }
            for (Thread thread : threads) thread.join();
            //Thread.sleep(60000);
            long end = System.currentTimeMillis();
            System.out.println("time spent: "+ (end-start));
            System.out.println("avg qtime: "+ (double)avg.get(0)/16);
        //}

    }
}
