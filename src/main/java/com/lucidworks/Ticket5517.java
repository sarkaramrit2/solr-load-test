//package com.lucidworks;
//
//
//import org.apache.lucene.util.TestUtil;
//import org.apache.solr.client.solrj.SolrClient;
//import org.apache.solr.client.solrj.SolrServerException;
//import org.apache.solr.client.solrj.impl.CloudSolrClient;
//import org.apache.solr.client.solrj.impl.HttpSolrClient;
//import org.apache.solr.common.SolrInputDocument;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.concurrent.TimeUnit;
//
//public class Ticket5517 {
//
//  private static Random r = new Random();
//
//  public static void main(String args[]) throws IOException, SolrServerException {
//    HttpSolrClient client = new HttpSolrClient("http://localhost:8983/solr");
//    //SolrClient client = new CloudSolrClient("localhost:9983");
//
//    long startTime = System.nanoTime();
//    for (int i=0; i<1000; i++) {
//      List<SolrInputDocument> docs = new ArrayList<>(1250);
//      docs.clear();
//      for (int j=0; j<1000; j++) {
//        SolrInputDocument document = new SolrInputDocument();
//        document.addField("id", i*1000+j);
//        document.addField("description", createSentance());
//        docs.add(document);
//      }
//      client.add("techproducts", docs);
//    }
//    //client.commit("gettingstarted", true, true, true);
//    long endTime = System.nanoTime();
//    System.out.println("Time taken to complete indexing = " + TimeUnit.SECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS) + "s");
//  }
//
//  private static String createSentance() {
//    //1k word description with 5-7 letters in each word
//    StringBuilder sb = new StringBuilder(1000*5);
//    for (int i=0; i<1000; i++) {
//      sb.append(TestUtil.randomSimpleString(r, 5, 7) + " ");
//    }
//    return sb.toString();
//  }
//}
