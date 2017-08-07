//package com.wk.indexer;
//
//import org.apache.http.NoHttpResponseException;
//import org.apache.http.conn.ConnectTimeoutException;
//import org.apache.solr.client.solrj.SolrClient;
//import org.apache.solr.client.solrj.request.UpdateRequest;
//import org.apache.solr.common.SolrException;
//import org.apache.solr.common.SolrInputDocument;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.xml.stream.XMLInputFactory;
//import javax.xml.stream.XMLStreamConstants;
//import javax.xml.stream.XMLStreamException;
//import javax.xml.stream.XMLStreamReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.net.ConnectException;
//import java.net.SocketException;
//import java.nio.file.Path;
//import java.util.ArrayList;
//import java.util.List;
//
//public class IndexRunner implements Runnable {
//
//    public static final String ADD = "add";
//    public static final String DELETE = "delete";
//
//    private static Logger log = LoggerFactory.getLogger(IndexRunner.class);
//
//    private final SolrClient solrClient;
//    private final XMLInputFactory inputFactory;
//    private final Path file;
//
//
//    public IndexRunner(SolrClient solrClient, XMLInputFactory inputFactory, Path file) {
//        this.solrClient = solrClient;
//        this.inputFactory = inputFactory;
//        this.file = file;
//    }
//
//    protected List<SolrInputDocument> makeBatch(Path file) throws XMLStreamException, IOException {
//        XMLStreamReader parser = inputFactory.createXMLStreamReader(new FileReader(file.toAbsolutePath().toString()));
//        List<SolrInputDocument> docs = null;
//        try {
//            while (true) {
//                int event = parser.next();
//                switch (event) {
//                    case XMLStreamConstants.END_DOCUMENT:
//                        parser.close();
//                        return docs;
//                    case XMLStreamConstants.START_ELEMENT:
//                        String currTag = parser.getLocalName();
//                        if (currTag.equals(ADD)) {
//                            docs = new ArrayList<>();
//                        } else if ("doc".equals(currTag)) {
//                            log.debug("loading doc...");
//                            SolrInputDocument doc = XMLLoader.readDoc(parser);
//                            docs.add(doc);
//                        }
//                        //Get more clarity if deletes are supported
////                    } else if (DELETE.equals(currTag)) {
////                        log.trace("parsing delete");
////                        processDelete(req, processor, parser);
////                    } // end delete
//                        break;
////                    default:
////                        throw new IOException("Unrecognized XML tag");
//                }
//            }
//        } finally {
//            parser.close();
//        }
//    }
//
//    protected int sendBatch(List<SolrInputDocument> docs, int waitBeforeRetry, int maxRetries) {
//        int sent = 0;
//        try {
//            UpdateRequest updateRequest = new UpdateRequest();
//            if (docs.size() == 1) {
//                updateRequest.add(docs.get(0));
//            } else {
//                updateRequest.add(docs);
//            }
//
//            solrClient.request(updateRequest);
//            sent = docs.size();
//        } catch (Exception exc) {
//            Throwable rootCause = SolrException.getRootCause(exc);
//            boolean wasCommError =
//                    (rootCause instanceof ConnectException ||
//                            rootCause instanceof ConnectTimeoutException ||
//                            rootCause instanceof NoHttpResponseException ||
//                            rootCause instanceof SocketException);
//
//            if (wasCommError) {
//                if (--maxRetries > 0) {
//                    log.warn("ERROR: " + rootCause + " ... Sleeping for "
//                            + waitBeforeRetry + " seconds before re-try ...");
//                    try {
//                        Thread.sleep(waitBeforeRetry * 1000L);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    sent = sendBatch(docs, waitBeforeRetry, maxRetries);
//                } else {
//                    throw new RuntimeException("No more retries available!", exc);
//                }
//            } else {
//                throw new RuntimeException(exc);
//            }
//        }
//        return sent;
//    }
//
//    @Override
//    public void run() {
//        try {
//            List<SolrInputDocument> docs = makeBatch(file);
//            int sent = sendBatch(docs, 10, 3);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
