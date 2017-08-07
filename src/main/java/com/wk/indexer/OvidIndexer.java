//package com.wk.indexer;
//
//
//import org.apache.solr.client.solrj.SolrClient;
//import org.apache.solr.client.solrj.impl.CloudSolrClient;
//
//import javax.xml.stream.XMLInputFactory;
//import java.io.IOException;
//import java.nio.file.DirectoryStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class OvidIndexer {
//
//    int numThreads = 10;
//
//    private final ExecutorService indexerPool;
//    private final SolrClient solrClient;
//    private final XMLInputFactory inputFactory;
//
//    public OvidIndexer() {
//        indexerPool = Executors.newFixedThreadPool(numThreads);
//        solrClient = new CloudSolrClient("localhost:9983");
//        inputFactory = XMLInputFactory.newInstance();
//    }
//
//    public void startIndexing(String dir , String collection) throws IOException {
//        ((CloudSolrClient)solrClient).setDefaultCollection(collection);
//        try (DirectoryStream<Path> files = Files.newDirectoryStream(Paths.get(dir))) {
//            for (Path file : files) {
//                if (!file.toString().endsWith(".xml")) {
//                    continue;
//                }
//                IndexRunner runner = new IndexRunner(solrClient, inputFactory, file);
//                indexerPool.execute(runner);
//            }
//        }
//    }
//
//    public static void main(String args[]) throws IOException {
//        OvidIndexer indexer = new OvidIndexer();
//        indexer.startIndexing("/Users/varun/solr-5.3.1/example/exampledocs", "gettingstarted");
//    }
//}
