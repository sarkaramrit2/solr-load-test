//package org.apache.solr;
//
//import com.codahale.metrics.ConsoleReporter;
//import com.codahale.metrics.MetricRegistry;
//import com.codahale.metrics.Timer;
//import org.apache.http.NoHttpResponseException;
//import org.apache.http.conn.ConnectTimeoutException;
//import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
//import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
//import org.apache.jmeter.samplers.SampleResult;
//import org.apache.solr.client.solrj.SolrClient;
//import org.apache.solr.client.solrj.impl.CloudSolrClient;
//import org.apache.solr.client.solrj.impl.HttpSolrClient;
//import org.apache.solr.client.solrj.request.UpdateRequest;
//import org.apache.solr.common.SolrException;
//import org.apache.solr.common.SolrInputDocument;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.*;
//import java.net.ConnectException;
//import java.net.SocketException;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * Lucene: http://people.apache.org/~mikemccand/lucenebench/indexing.html
// * Download wikipedia dump from http://people.apache.org/~mikemccand/enwiki-20120502-lines-1k.txt.lzma and extract it
// *
// */
//public class IndexWikipedia1K extends AbstractJavaSamplerClient implements Serializable {
//
//    SolrClient solrClient;
//
//    Logger log = LoggerFactory.getLogger(IndexWikipedia1K.class);
//
//    private static final MetricRegistry metrics = new MetricRegistry();
//    private static final Timer sendBatchToSolrTimer = metrics.timer("sendBatchToSolr");
//    private static AtomicInteger refCounter = new AtomicInteger(0);
//    private static Random random;
//    private static ConsoleReporter reporter = null;
//
//    private BufferedReader reader;
//    private int readCount = 0;
//    private final static int BUFFER_SIZE = 1 << 16;     // 64K
//    private final static char SEP = '\t';
//    private int batchSize = 1000;
//
//    /**
//     *
//     * Number of threads is controlled by JMeter.
//     *
//     *
//     */
//    @Override
//    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
//
//        SampleResult result = new SampleResult();
//        result.sampleStart();
//        List<SolrInputDocument> documents = new ArrayList<>(batchSize);
//        while (true) {
//            readDocs(documents);
//            if (documents.size() == 0) {
//                break;
//            }
//            sendBatch(documents, 10, 3);
//        }
//
//        result.sampleEnd();
//        return result;
//    }
//
//    private synchronized void readDocs(List<SolrInputDocument> reuse) {
//        for (int i=0; i<batchSize; i++) {
//            String line;
//            try {
//                line = reader.readLine();
//            } catch (IOException e) {
//                throw new RuntimeException("error reading file");
//            }
//            if (line == null) {
//                return;
//            }
//            int id = readCount++;
//            if (readCount % 10000 == 0) {
//                log.info("Indexed {} docs ", readCount);
//            }
//            int spot = line.indexOf(SEP);
//            if (spot == -1) {
//                throw new RuntimeException("line: [" + line + "] is in an invalid format !");
//            }
//            int spot2 = line.indexOf(SEP, 1 + spot);
//            if (spot2 == -1) {
//                throw new RuntimeException("line: [" + line + "] is in an invalid format !");
//            }
//            int spot3 = line.indexOf(SEP, 1 + spot2);
//            if (spot3 == -1) {
//                spot3 = line.length();
//            }
//            String body = line.substring(1+spot2, spot3);
//            String title = line.substring(0, spot);
//            String dateString = line.substring(1 + spot, spot2);
//            //TODO convert dateString to ISO format
//
//            SolrInputDocument sdoc = new SolrInputDocument();
//            sdoc.addField("id", id);
//            sdoc.addField("body", body);
//            sdoc.addField("title", title);
//            sdoc.addField("date", dateString);
//
//            reuse.add(sdoc);
//        }
//    }
//
//    protected int sendBatch(List<SolrInputDocument> reuse, int waitBeforeRetry, int maxRetries) {
//        int sent = 0;
//        final Timer.Context sendTimerCtxt = sendBatchToSolrTimer.time();
//        try {
//            UpdateRequest updateRequest = new UpdateRequest();
//
//            if (reuse.size() == 1) {
//                updateRequest.add(reuse.get(0));
//            } else {
//                updateRequest.add(reuse);
//            }
//
//            solrClient.request(updateRequest);
//            sent = reuse.size();
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
//                    sent = sendBatch(reuse, waitBeforeRetry, maxRetries);
//                } else {
//                    throw new RuntimeException("No more retries available!", exc);
//                }
//            } else {
//                throw new RuntimeException(exc);
//            }
//        } finally {
//            sendTimerCtxt.stop();
//        }
//        reuse.clear();
//        return sent;
//    }
//
//    @Override
//    public void setupTest(JavaSamplerContext context) {
//        super.setupTest(context);
//
//        refCounter.incrementAndGet(); // keep track of threads using the statics in this class
//
//        Map<String,String> params = new HashMap<>();
//        Iterator<String> paramNames = context.getParameterNamesIterator();
//        while (paramNames.hasNext()) {
//            String paramName = paramNames.next();
//            String param = context.getParameter(paramName);
//            if (param != null)
//                params.put(paramName, param);
//        }
//
//        random = new Random(Long.parseLong(params.get("RANDOM_SEED")));
//
//        String mode = params.get("mode");
//        String path = params.get("path");
//        String batchSizeString = params.get("batchSize");
//        if (batchSizeString != null) {
//            batchSize = Integer.parseInt(batchSizeString);
//        }
//
//        synchronized (IndexWikipedia1K.class) {
//
//            if (reader == null) {
//                try {
//                    InputStream is = new FileInputStream(path);
//                    reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), BUFFER_SIZE);
//                    String firstLine = reader.readLine();
//                    String[] fields = firstLine.split("\t");
//
//                    assert fields.length == 4;
//                    assert fields[1].equals("doctitle");
//                    assert fields[2].equals("docdate");
//                    assert fields[3].equals("body");
//
//                } catch (IOException e) {
//                    new RuntimeException("Could not find/read file");
//                }
//            }
//
//            if (solrClient == null) {
//                if ("solr".equals(mode)) {
//                    String collection = params.get("COLLECTION");
//                    String url = params.get("SOLR_URL");
//                    if (!url.endsWith("/")) {
//                        url = url + "/";
//                    }
//                    solrClient = new HttpSolrClient(url + collection);
//                } else if ("solrcloud".equals(mode)) {
//                    String collection = params.get("COLLECTION");
//                    String zkString = params.get("ZK_HOST");
//                    solrClient = new CloudSolrClient(zkString);
//                    ((CloudSolrClient) solrClient).setDefaultCollection(collection);
//                    ((CloudSolrClient) solrClient).connect();
//                }
//            }
//
//            if (reporter == null) {
//                reporter = ConsoleReporter.forRegistry(metrics)
//                        .convertRatesTo(TimeUnit.SECONDS)
//                        .convertDurationsTo(TimeUnit.MILLISECONDS).build();
//                reporter.start(1, TimeUnit.MINUTES);
//            }
//        }
//    }
//
//    @Override
//    public void teardownTest(JavaSamplerContext context) {
//        if (solrClient != null) {
//            int refs = refCounter.decrementAndGet();
//            if (refs == 0) {
//                getLogger().info("Shutting down solr solrClient");
//                if (reporter != null) {
//                    reporter.report();
//                    reporter.stop();
//                }
//                try {
//                    solrClient.close();
//                } catch (IOException e) {
//                    solrClient = null;
//                }
//            }
//        }
//        super.teardownTest(context);
//    }
//
//}
