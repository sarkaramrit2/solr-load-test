package org.apache.solr;

import org.apache.solr.client.solrj.io.SolrClientCache;
import org.apache.solr.client.solrj.io.Tuple;
import org.apache.solr.client.solrj.io.stream.*;
import org.apache.solr.client.solrj.io.stream.expr.StreamExpression;
import org.apache.solr.client.solrj.io.stream.expr.StreamExpressionParser;
import org.apache.solr.client.solrj.io.stream.expr.StreamFactory;

public class Stream2 {

    public static void main(String[] args) {


        int tupleCounter = 0;
        StreamExpression expression;

        StreamContext streamContext = new StreamContext();
        SolrClientCache solrClientCache = new SolrClientCache();
        streamContext.setSolrClientCache(solrClientCache);

        StreamFactory factory = new StreamFactory().withCollectionZkHost("collection1", "localhost:9983");
        expression = StreamExpressionParser.parse("search(" + "collection1" + ", q=*:*, fl=\"id,order_no_i\", sort=\"id asc\", qt=\"/export\", zkHost=localhost:9983)");

        try (CloudSolrStream stream = new CloudSolrStream(expression, factory)) {

            stream.setStreamContext(streamContext);

            stream.open();

            for (Tuple t = stream.read(); !t.EOF && tupleCounter < 1_000; t = stream.read()) {
                ++tupleCounter;
                if ((tupleCounter % 1_00) == 0) {
                    System.out.println("Just read tuple " + tupleCounter);
                }
            }
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        solrClientCache.close();
        System.out.println("We finally stopped");

    }
}