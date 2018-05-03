package org.apache.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.io.SolrClientCache;
import org.apache.solr.client.solrj.io.Tuple;
import org.apache.solr.client.solrj.io.eval.*;
import org.apache.solr.client.solrj.io.ops.*;
import org.apache.solr.client.solrj.io.stream.*;
import org.apache.solr.client.solrj.io.stream.expr.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StreamExpressionExample {

    /* target stream:

    select(search(collection1,q="*:*",fl="id,member_id_i,quantity_i,order_no_s,ship_addr_s",sort="id asc",zkHost="localhost:9983",qt="/export"),
    order_no_s as order_no_s,ship_addr_s as ship_addr_s,
    concat(fields="order_no_s,ship_addr_s",delim=--,as=concat_field),
    add(member_id_i,quantity_i) as add_member)

    */

    private static final String AS = " as ";

    public static void main(String[] args) throws IOException {
        System.out.println("start" + new Date());

        loadStreamFactory();

        // create the search stream expression
        StreamExpression searchExp = new StreamExpression("search");
        searchExp.addParameter("collection1");
        searchExp.addParameter(new StreamExpressionNamedParameter("q", "*:*"));
        searchExp.addParameter(new StreamExpressionNamedParameter("fl", "id,member_id_i,quantity_i,order_no_s,ship_addr_s"));
        searchExp.addParameter(new StreamExpressionNamedParameter("sort", "id asc"));
        searchExp.addParameter(new StreamExpressionNamedParameter("zkHost", "localhost:9983"));
        searchExp.addParameter(new StreamExpressionNamedParameter("qt", "/export"));

        //create select stream expression
        StreamExpression selectExpression = new StreamExpression("select");
        // infuse search expression into select
        selectExpression.addParameter(searchExp);

        selectExpression.addParameter(new StreamExpressionValue("order_no_s") + AS + new StreamExpressionValue("order_no_s")); //one way of writing
        selectExpression.addParameter("ship_addr_s" + AS + "ship_addr_s"); //more simplar but string representation
        selectExpression.addParameter("member_id_i" + AS + "member_id_i");
        selectExpression.addParameter("quantity_i" + AS + "quantity_i");

        // create concat operation expression
        StreamExpression concatOperation = new StreamExpression("concat");
        concatOperation.addParameter(new StreamExpressionNamedParameter("fields","order_no_s,ship_addr_s")); // can also be passed as StreamExpressionValues
        concatOperation.addParameter(new StreamExpressionNamedParameter("delim","--"));
        concatOperation.addParameter(new StreamExpressionNamedParameter("as","concat_field")); //can also be passed as StreamExpressionValue
        selectExpression.addParameter(concatOperation);

        // create add evaluator expression
        StreamExpression addEval = new StreamExpression("add");
        addEval.addParameter(new StreamExpressionValue("member_id_i"));
        addEval.addParameter(new StreamExpressionValue("quantity_i"));
        selectExpression.addParameter(addEval + AS + new StreamExpressionValue("add_member"));

        // final SelectStream
        SelectStream selectStream = new SelectStream(selectExpression, streamFactory);

        SolrClientCache cache = new SolrClientCache();
        attachStreamFactory(selectStream, cache);
        getTuples(selectStream);

        cache.close();

        System.out.println("end" + new Date());
    }

    private static List<Tuple> getTuples(TupleStream tupleStream) throws IOException {
        System.out.println("getTuples start" + new Date());
        tupleStream.open();
        List<Tuple> tuples = new ArrayList();
        int count = 0;
        for(;;) {
            Tuple t = tupleStream.read();
            if(t.EOF) {
                break;
            } else {
                System.out.println("tuple: " + t.getMap());
                tuples.add(t);
            }
        }
        tupleStream.close();
        System.out.println("getTuples end" + new Date());
        return tuples;
    }

    public static StreamFactory streamFactory = new StreamFactory();

    private static void attachStreamFactory(TupleStream tupleStream, SolrClientCache cache) {
        StreamContext context = new StreamContext();
        context.setSolrClientCache(cache);
        context.setStreamFactory(streamFactory);
        tupleStream.setStreamContext(context);
    }
    public static void loadStreamFactory() {

        streamFactory.withCollectionZkHost("collection1", "localhost:9983");
        streamFactory.withDefaultZkHost("localhost:9983");

        streamFactory
                .withFunctionName("search", CloudSolrStream.class)
                .withFunctionName("select", SelectStream.class)
                .withFunctionName("concat", ConcatOperation.class)
                .withFunctionName("add", AddEvaluator.class);
    }

}