//package com.lucidworks;
//
//
//import org.apache.lucene.util.TestUtil;
//import org.apache.solr.client.solrj.SolrServerException;
//import org.apache.solr.client.solrj.impl.HttpSolrClient;
//import org.apache.solr.common.SolrInputDocument;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.concurrent.TimeUnit;
//
//public class Ticket7215 {
//
//  private static Random r = new Random();
//
//  public static void main(String[] args) throws IOException, SolrServerException {
//    HttpSolrClient client = new HttpSolrClient("http://localhost:8983/solr");
//    long startTime = System.nanoTime();
//    for (int i=0; i<1000*45; i++) {
//      List<SolrInputDocument> docs = new ArrayList<>(1250);
//      docs.clear();
//      for (int j=0; j<1000; j++) {
//        SolrInputDocument document = new SolrInputDocument();
//        document.addField("id", TestUtil.randomSimpleString(r, 5, 20));
//        docs.add(document);
//      }
//      client.add("collection1", docs);
//    }
//    client.commit("collection1", true, true, true);
//    long endTime = System.nanoTime();
//    System.out.println("Time taken to complete indexing = " + TimeUnit.SECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS) + "s");
//  }
//}
