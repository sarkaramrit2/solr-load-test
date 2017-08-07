//package com.lucidworks;
//
//
//import org.apache.solr.client.solrj.SolrServerException;
//import org.apache.solr.client.solrj.impl.CloudSolrClient;
//import org.apache.solr.client.solrj.impl.HttpSolrClient;
//import org.apache.solr.client.solrj.request.QueryRequest;
//import org.apache.solr.common.params.ModifiableSolrParams;
//import org.apache.solr.common.util.NamedList;
//import org.apache.zookeeper.KeeperException;
//
//import java.io.IOException;
//
//public class Ticket7502 {
//
//  public static void main(String args[]) throws IOException, SolrServerException, KeeperException, InterruptedException {
//    CloudSolrClient client = new CloudSolrClient("localhost:9983");
//    client.connect();
//    String leaderUrl = client.getZkStateReader().getLeaderUrl("dih3", "shard1", 30000);
//
//    HttpSolrClient httpSolrClient = new HttpSolrClient(leaderUrl);
//    ModifiableSolrParams modifiableSolrParams = new ModifiableSolrParams();
//    modifiableSolrParams.add("command", "status");
//    QueryRequest queryRequest = new QueryRequest(modifiableSolrParams);
//    queryRequest.setPath("/dataimport");
//    NamedList<Object> response = httpSolrClient.request(queryRequest);
//    while (response.get("status").equals("busy")) {
//      Thread.sleep(1000);
//    }
//    int x = 10;
//  }
//}
