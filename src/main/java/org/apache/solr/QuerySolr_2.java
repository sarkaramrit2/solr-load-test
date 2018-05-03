package org.apache.solr;


import org.apache.http.client.HttpClient;
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

public class QuerySolr_2 {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String args[]) throws IOException, SolrServerException, Exception {

        final String zkHost = "toyota:9983";
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
                //"\t\t\t\trefine: true,\n" +
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
                "\t\t\t\t                                to: \"vin_s\",\n" +
                "\t\t\t\t                                method: \"dv\"\n" +
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

        final String json_q1_a = "{\n" +
                "    models: {\n" +
                "        type: terms,\n" +
                "        field: \"v_model_s\",\n" +
                "        limit: 3,\n" +
                //"\t\t\t\trefine: true,\n" +
                "        facet: {\n" +
                "            year_per_model: {\n" +
                "                type: terms,\n" +
                "                field: \"v_year_i\",\n" +
                "                limit: 3,\n" +
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
                "                        limit: 3,\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\tfacet: {\n" +
                "\t\t\t\t                    defect_shop: {\n" +
                "\t\t\t\t                        domain: {\n" +
                "\t\t\t\t                            join: {\n" +
                "\t\t\t\t                                from: \"vin_s\",\n" +
                "\t\t\t\t                                to: \"vin_s\",\n" +
                "\t\t\t\t                                method: \"dv\"\n" +
                "\t\t\t\t                            },\n" +
                "\t\t\t\t                            filter: \"doc_type_s:defect\"\n" +
                "\t\t\t\t                        },\n" +
                "\t\t\t\t                        type: terms,\n" +
                "\t\t\t\t                        field: \"defect_shop_s\",\n" +
                "\t\t\t\t                        limit: 3\n" +
                "\t\t\t\t                    }\n" +
                "\t\t\t\t                }\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}\n";


        final String json_q1_b = "{  \n" +
                "   defect_shops:{  \n" +
                "      type:terms,\n" +
                "      field:\"defect_shop_s\",\n" +
                "      limit:3,\n" +
                "      refine:true,\n" +
                "      facet:{  \n" +
                "         models:{  \n" +
                "            type:terms,\n" +
                "            field:\"v_model_s\",\n" +
                "            limit:3,\n" +
                "            domain:{  \n" +
                "               join:{  \n" +
                "                  from:\"vin_s\",\n" +
                "                  to:\"vin_s\",\n" +
                "                  method:dv\n" +
                "               },\n" +
                "               filter:\"doc_type_s:vehicle\"\n" +
                "            },\n" +
                "            facet:{\n" +
                "               claim_opcode:{\n" +
                "                  domain:{\n" +
                "                     join:{  \n" +
                "                        from:\"vin_s\",\n" +
                "                        to:\"vin_s\",\n" +
                "                        method:dv\n" +
                "                     },\n" +
                "                     filter:\"doc_type_s:claim\"\n" +
                "                  },\n" +
                "                  type:terms,\n" +
                "                  field:\"claim_opcode_s\",\n" +
                "                  limit:3\n" +
                "               }\n" +
                "            }\n" +
                "         }\n" +
                "      }\n" +
                "   }}\n";

        final String json_q2 =
                "{\n" +
                        "    models: {\n" +
                        "        type: terms,\n" +
                        "        field: \"v_model_s\",\n" +
                        "        limit: 10,\n" +
                        //"\t\t\t\trefine: true,\n" +
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


        final String json_q2_2 =
                "{\n" +
                        "    models: {\n" +
                        "        type: terms,\n" +
                        "        field: \"v_model_s\",\n" +
                        "        limit: 10,\n" +
                        //"\t\t\t\trefine: true,\n" +
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
                        "                                to: \"vin_s\",\n" +
                        "                                method: \"dv\"\n" +
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
                //"\t\t\t\trefine: true,\n" +
                "\t\t\t\tdomain: {\n" +
                "\t\t\t\t\t\tjoin: {\n" +
                "\t\t\t\t\t\t\t\tfrom: \"vin_s\",\n" +
                "\t\t\t\t\t\t\t\tto: \"vin_s\",\n" +
                "\t\t\t\t\t\t\t\tmethod: \"dv\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\tfilter: \"doc_type_s:defect\"\n" +
                "\t\t\t\t}\n" +
                "    }\n" +
                "}\n";

        final String json_q4 = "{\n" +
                "    models: {\n" +
                "        type: terms,\n" +
                "        field: \"v_model_s\",\n" +
                "        limit: 10,\n" +
                "        refine: true,\n" +
                "        facet: {\n" +
                "            year_per_model: {\n" +
                "                type: terms,\n" +
                "                field: \"v_year_i\",\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}\n";

        final String json_q4_a = "{\n" +
                "    models: {\n" +
                "        type: terms,\n" +
                "        field: \"v_model_s\",\n" +
                "        limit: 10,\n" +
                "        refine: true,\n" +
                "        facet: {\n" +
                "            year_per_model: {\n" +
                "                type: terms,\n" +
                "                field: \"v_year_i\",\n" +
                "            }\n" +
                "        }\n" +
                "    },\n" +
                "    \"avg_horsepower\" : \"avg(v_horsepower_i)\",\n" +
                "    \"unique_years\" : \"unique(v_year_i)\",\n" +
                "    plants: {\n" +
                "        type: terms,\n" +
                "        field: \"v_plant_s\",\n" +
                "        limit: 10,\n" +
                "        refine: true\n" +
                "    }\n" +
                "}\n";

        List<Thread> threads = new ArrayList<>(50);

        int j_1 = Integer.parseInt(args[2]);
        int j_2 = Integer.parseInt(args[3]);
        int j_3 = Integer.parseInt(args[4]);
        int j_4 = Integer.parseInt(args[5]);
        int j_5 = Integer.parseInt(args[6]);
        int j_6 = Integer.parseInt(args[2]);
        int j_7 = Integer.parseInt(args[3]);
        int j_8 = Integer.parseInt(args[4]);

        final List<Integer> avg = new ArrayList<Integer>();
        avg.add(0);

        System.out.println("simultaneous theads: " + j_1 + " : " + j_2 + " : " + j_3);
        long start = System.currentTimeMillis();

        if (args[1].equals("3") || args[1].equals("10") || args[1].equals("11")) {

            for (int k = 0; k < (int) j_3; k++) {

                Thread t = new Thread() {
                    @Override
                    public void run() {

                        try {
                            QueryResponse response = client.query(new ModifiableSolrParams().add("q", "doc_type_s:vehicle AND v_year_i:2007").
                                    add("json.facet", json_q3)
                            );
                            avg.set(0, avg.get(0) + response.getQTime());
                        } catch (Exception e) {
                            System.err.println(e);
                        }

                    }
                };
                threads.add(t);
                t.start();
            }

        }

        if (args[1].equals("2") || args[1].equals("10")) {

            for (int k = 0; k < j_2; k++) {

                Thread t = new Thread() {
                    @Override
                    public void run() {

                        try {
                            QueryResponse response = client.query(new ModifiableSolrParams().add("q", "doc_type_s:vehicle").
                                    add("json.facet", json_q2)
                            );
                            avg.set(0, avg.get(0) + response.getQTime());
                        } catch (Exception e) {
                            System.err.println(e);
                        }

                    }
                };
                threads.add(t);
                t.start();
            }

        }

        if (args[1].equals("2_2") || args[1].equals("11")) {

            for (int k = 0; k < j_2; k++) {

                Thread t = new Thread() {
                    @Override
                    public void run() {

                        try {
                            QueryResponse response = client.query(new ModifiableSolrParams().add("q", "doc_type_s:vehicle").
                                    add("json.facet", json_q2_2)
                            );

                            avg.set(0, avg.get(0) + response.getQTime());
                        } catch (Exception e) {
                            System.err.println(e);
                        }

                    }
                };
                threads.add(t);
                t.start();
            }

        }

        if (args[1].equals("1") || args[1].equals("10") || args[1].equals("11")) {

            for (int k = 0; k < j_1; k++) {

                Thread t = new Thread() {
                    @Override
                    public void run() {

                        try {
                            QueryResponse response = client.query(new ModifiableSolrParams().add("q", "doc_type_s:vehicle").
                                    add("json.facet", json_q1)
                            );
                            avg.set(0, avg.get(0) + response.getQTime());
                        } catch (Exception e) {
                            System.err.println(e);
                        }

                    }
                };
                threads.add(t);
                t.start();
            }
        }

        if (args[1].equals("1_a") || args[1].equals("10") || args[1].equals("11")) {

            for (int k = 0; k < j_1; k++) {

                Thread t = new Thread() {
                    @Override
                    public void run() {

                        try {
                            QueryResponse response = client.query(new ModifiableSolrParams().add("q", "doc_type_s:vehicle").
                                    add("json.facet", json_q1_a)
                            );
                            avg.set(0, avg.get(0) + response.getQTime());
                        } catch (Exception e) {
                            System.err.println(e);
                        }

                    }
                };
                threads.add(t);
                t.start();
            }
        }

        if (args[1].equals("1_b") || args[1].equals("10") || args[1].equals("11")) {

            for (int k = 0; k < j_1; k++) {

                Thread t = new Thread() {
                    @Override
                    public void run() {

                        try {
                            QueryResponse response = client.query(new ModifiableSolrParams().add("q", "doc_type_s:defect").
                                    add("json.facet", json_q1_b)
                            );
                            avg.set(0, avg.get(0) + response.getQTime());
                        } catch (Exception e) {
                            System.err.println(e);
                        }

                    }
                };
                threads.add(t);
                t.start();
            }
        }

        if (args[1].equals("4") || args[1].equals("10") || args[1].equals("11")) {

            for (int k = 0; k < j_4; k++) {

                Thread t = new Thread() {
                    @Override
                    public void run() {

                        try {
                            QueryResponse response = client.query(new ModifiableSolrParams()
                                    .add("rows", "0")
                                    .add("q", "doc_type_s:vehicle AND v_year_i:[1995 TO *]")
                                    .add("fq", "{!join from=vin_s to=vin_s v=$defect_q}")
                                    .add("fq", "{!join from=vin_s to=vin_s v=$claim_q}")
                                    .add("claim_q", "doc_type_s:claim AND claim_milage_i:[* TO 10000]")
                                    .add("defect_q", "doc_type_s:defect AND defect_shop_s:(d_shop_01 d_shop_06 d_shop_07 d_shop_09)")
                                    .add("json.facet", json_q4)
                            );
                            avg.set(0, avg.get(0) + response.getQTime());
                        } catch (Exception e) {
                            System.err.println(e);
                        }

                    }
                };
                threads.add(t);
                t.start();
            }
        }

        if (args[1].equals("4_a") || args[1].equals("10") || args[1].equals("11")) {

            for (int k = 0; k < j_4; k++) {

                Thread t = new Thread() {
                    @Override
                    public void run() {

                        try {
                            QueryResponse response = client.query(new ModifiableSolrParams()
                                    .add("rows", "0")
                                    .add("q", "doc_type_s:vehicle AND v_year_i:[1995 TO *]")
                                    .add("fq", "{!join from=vin_s to=vin_s v=$defect_q}")
                                    .add("fq", "{!join from=vin_s to=vin_s v=$claim_q}")
                                    .add("claim_q", "doc_type_s:claim AND claim_milage_i:[* TO 10000]")
                                    .add("defect_q", "doc_type_s:defect AND defect_shop_s:(d_shop_01 d_shop_06 d_shop_07 d_shop_09)")
                                    .add("json.facet", json_q4_a)
                            );
                            avg.set(0, avg.get(0) + response.getQTime());
                        } catch (Exception e) {
                            System.err.println(e);
                        }

                    }
                };
                threads.add(t);
                t.start();
            }
        }

        if (args[1].equals("5") || args[1].equals("10") || args[1].equals("11")) {

            for (int k = 0; k < j_5; k++) {

                Thread t = new Thread() {
                    @Override
                    public void run() {

                        try {
                            QueryResponse response = client.query(new ModifiableSolrParams()
                                    .add("rows", "0")
                                    .add("q", "doc_type_s:vehicle AND v_year_i:(1995 1996 1997 1998 1999 2000 2001 2002 2003 2004)")
                                    .add("fq", "{!join from=vin_s to=vin_s v=$defect_q}")
                                    .add("defect_q", "doc_type_s:defect AND defect_shop_s:(d_shop_01 d_shop_06 d_shop_07 d_shop_09)")
                                    .add("json.facet", json_q4)
                            );
                            avg.set(0, avg.get(0) + response.getQTime());
                        } catch (Exception e) {
                            System.err.println(e);
                        }

                    }
                };
                threads.add(t);
                t.start();
            }
        }

        if (args[1].equals("6") || args[1].equals("10") || args[1].equals("11")) {

            for (int k = 0; k < j_6; k++) {

                Thread t = new Thread() {
                    @Override
                    public void run() {

                        try {
                            QueryResponse response = client.query(new ModifiableSolrParams()
                                    .add("rows", "0")
                                    .add("claim_q", "doc_type_s:claim AND claim_opcode_s:(c_op_0072 c_op_0097 c_op_0041 c_op_0084 c_op_0026)")
                                    .add("q", "doc_type_s:vehicle AND v_year_i:(1995 1996 1997 1998 1999 2000 2001 2002 2003 2004)")
                                    .add("fq", "{!join from=vin_s to=vin_s v=$defect_q}")
                                    .add("fq", "{!join from=vin_s to=vin_s v=claim_q}")
                                    .add("defect_q", "doc_type_s:defect AND defect_shop_s:(d_shop_01 d_shop_06 d_shop_07 d_shop_09)")
                                    .add("json.facet", json_q4)
                            );
                            avg.set(0, avg.get(0) + response.getQTime());
                        } catch (Exception e) {
                            System.err.println(e);
                        }

                    }
                };
                threads.add(t);
                t.start();
            }
        }

        if (args[1].equals("7") || args[1].equals("10") || args[1].equals("11")) {

            for (int k = 0; k < j_7; k++) {

                Thread t = new Thread() {
                    @Override
                    public void run() {

                        try {
                            QueryResponse response = client.query(new ModifiableSolrParams()
                                    .add("claim_q", "doc_type_s:claim AND claim_opcode_s:c_op_0072")
                                    .add("rows", "0")
                                    .add("q", "doc_type_s:vehicle AND v_year_i:(1995 1996 1997 1998 1999 2000 2001 2002 2003 2004)")
                                    .add("fq", "{!join from=vin_s to=vin_s v=$defect_q}")
                                    .add("fq", "{!join from=vin_s to=vin_s v=claim_q}")
                                    .add("defect_q", "doc_type_s:defect AND defect_shop_s:d_shop_01")
                                    .add("json.facet", json_q4)
                            );
                            avg.set(0, avg.get(0) + response.getQTime());
                        } catch (Exception e) {
                            System.err.println(e);
                        }

                    }
                };
                threads.add(t);
                t.start();
            }
        }


        if (args[1].equals("8") || args[1].equals("10") || args[1].equals("11")) {

            for (int k = 0; k < j_8; k++) {

                Thread t = new Thread() {
                    @Override
                    public void run() {

                        try {
                            QueryResponse response = client.query(new ModifiableSolrParams()
                                    .add("claim_q", "doc_type_s:claim AND claim_opcode_s:c_op_0072")
                                    .add("rows", "0")
                                    .add("q", "doc_type_s:vehicle AND v_year_i:1995")
                                    .add("fq", "{!join from=vin_s to=vin_s v=$defect_q}")
                                    .add("fq", "{!join from=vin_s to=vin_s v=claim_q}")
                                    .add("defect_q", "doc_type_s:defect AND defect_shop_s:(d_shop_01 d_shop_06 d_shop_07 d_shop_09)")
                                    .add("json.facet", json_q4)
                            );
                            avg.set(0, avg.get(0) + response.getQTime());
                        } catch (Exception e) {
                            System.err.println(e);
                        }

                    }
                };
                threads.add(t);
                t.start();
            }
        }

        for (Thread thread : threads) thread.join();

        long end = System.currentTimeMillis();

        System.out.println("time spent: " + (end - start));
        System.out.println("avg qtime: " + (double) avg.get(0) / (j_1 + j_2 + j_3 + j_4 + j_5));

    }
}