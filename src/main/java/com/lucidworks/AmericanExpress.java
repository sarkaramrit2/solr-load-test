//package com.lucidworks;
//
//
//import org.apache.solr.client.solrj.SolrQuery;
//import org.apache.solr.client.solrj.SolrServerException;
//import org.apache.solr.client.solrj.impl.CloudSolrServer;
//import org.apache.solr.client.solrj.impl.HttpSolrServer;
//import org.apache.solr.client.solrj.request.QueryRequest;
//
//import java.net.MalformedURLException;
//import java.util.concurrent.Future;
//
//public class AmericanExpress {
//
//  CloudSolrServer client;
//
//  public static void main(String args[]) throws SolrServerException {
//    AmericanExpress amex = new AmericanExpress();
//    amex.createClient("localhost:9983");
//    amex.query();
//  }
//
//  private void query() throws SolrServerException {
//    try {
//      SolrQuery query = new SolrQuery("id:1 OR id:2 OR id:3 OR id:4 OR id:5 or id:6");
//      QueryRequest request = new QueryRequest(query);
//      System.out.println("NumFound:" + request.process(client));
//    } catch (SolrServerException e) {
//      throw e;
//    } catch (HttpSolrServer.RemoteSolrException e) {
//      System.out.println("Max Boolean Clausses!!! ");
//    }
//
//
//  }
//
//  public void createClient(String zkHost) {
//    try {
//      //TODO how to pass in custom http client connection and SO timeouts
//      client = new CloudSolrServer(zkHost);
//      client.setDefaultCollection("gettingstarted");
//      client.getLbServer().setConnectionTimeout(3000);
//      client.getLbServer().setSoTimeout(3000);
//      client.connect();
//      Future<String> future = null;
//      future.cancel(true);
//    } catch (MalformedURLException e) {
//      throw new RuntimeException("Cannot connect to ZK");
//    }
//  }
//}
