package org.apache.solr;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
import org.apache.solr.client.solrj.io.SolrClientCache;
import org.apache.solr.client.solrj.io.Tuple;
import org.apache.solr.client.solrj.io.stream.SolrStream;
import org.apache.solr.client.solrj.io.stream.StreamContext;
import org.apache.solr.client.solrj.io.stream.TupleStream;
import org.apache.solr.common.params.ModifiableSolrParams;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RollupTest {

    public static void main(String args[]) throws IOException {
        long startTime = System.nanoTime();

        StreamContext context = new StreamContext();
        ConnectionKeepAliveStrategy keepAliveStrat = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                // we only close connections based on idle time, not ttl expiration
                return -1;
            }
        };

        String expr = "search(default,q=\"*:*\",sort=\"member_id_i desc\",fl=\"id,member_id_i,quantity_i\", qt=\"/export\")";
        ModifiableSolrParams paramsLoc = new ModifiableSolrParams();
        paramsLoc.set("expr", expr);
        paramsLoc.set("qt", "/stream");
        String url = "http://localhost:8983/solr/default/";
        TupleStream solrStream = new SolrStream(url, paramsLoc);

        solrStream.setStreamContext(context);
        int tupleCounter = 0;
        try {
            solrStream.open();
            while (true) {
                final Tuple tuple = solrStream.read();
                if (tuple.EOF)
                    break;
                if (tupleCounter++ % 10 == 0){
                    System.out.println("member_id_i=" + tuple.fields.get("member_id_i"));
                }
            }
            System.out.println("tuple counter final count: " + tupleCounter);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            solrStream.close();
        }
        System.out.println("index took" + TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS) + " seconds to build" );
    }
}
