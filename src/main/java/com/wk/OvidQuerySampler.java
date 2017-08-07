//package com.wk;
//
//import com.codahale.metrics.ConsoleReporter;
//import com.codahale.metrics.MetricRegistry;
//import org.apache.jmeter.config.Arguments;
//import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
//import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
//import org.apache.jmeter.samplers.SampleResult;
//import org.apache.solr.client.solrj.SolrClient;
//import org.apache.solr.client.solrj.SolrQuery;
//import org.apache.solr.client.solrj.impl.CloudSolrClient;
//import org.apache.solr.client.solrj.impl.HttpSolrClient;
//import org.apache.solr.client.solrj.impl.XMLResponseParser;
//import org.apache.solr.client.solrj.response.QueryResponse;
//import org.apache.solr.client.solrj.response.TermsResponse;
//import org.apache.solr.common.cloud.Replica;
//import org.apache.solr.common.cloud.Slice;
//import org.apache.solr.common.params.ModifiableSolrParams;
//
//import java.io.Serializable;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//
///* These:
// * ./bin/solr start -e cloud -noprompt;./bin/post -c gettingstarted example/exampledocs/*.xml
// */
//public class OvidQuerySampler extends AbstractJavaSamplerClient implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    // keeps track of how many tests are running this sampler and when there are
//    // none, a final hard commit is sent.
//    private static AtomicInteger refCounter = new AtomicInteger(0);
//    private static final MetricRegistry metrics = new MetricRegistry();
//    private static final com.codahale.metrics.Timer queryTimer = metrics.timer("query");
//    private static final com.codahale.metrics.Counter noResultsCounter = metrics.counter("noresults");
//    private static final com.codahale.metrics.Counter excCounter = metrics.counter("exceptions");
//
//    private static ConsoleReporter reporter = null;
//    private static CloudSolrClient solrClient = null;
//
//    private static Random random;
//    private static String[] collections;
//
//    //Load up all unique ids across all collections
//    private static Map<String, List<String>> ids = new HashMap<>();
//    private static String collectionList;
//    private static String searchField;
//
//    @Override
//    public SampleResult runTest(JavaSamplerContext context) {
//        SampleResult result = new SampleResult();
//        result.sampleStart();
//
//        int collectionPos = random.nextInt(collections.length);
//        String collection = collections[collectionPos];
//
//        int idPos = random.nextInt(ids.get(collection).size());
//        String id = ids.get(collection).get(idPos);
//
//        ModifiableSolrParams solrParams = new ModifiableSolrParams();
//        solrParams.add("q", searchField + ":" + id);
//        solrParams.add("collection", collectionList);
//        solrParams.add("wt", "json");
//        final com.codahale.metrics.Timer.Context queryTimerCtxt = queryTimer.time();
//        try {
//            QueryResponse qr = solrClient.query(collection, solrParams);
//            if (qr.getResults().getNumFound() == 0) {
//                noResultsCounter.inc();
//            }
//            result.setResponseOK();
//        } catch (Exception solrExc) {
//            excCounter.inc();
//        } finally {
//            queryTimerCtxt.stop();
//        }
//
//        result.sampleEnd();
//        return result;
//    }
//
//    @Override
//    public Arguments getDefaultParameters() {
//        Arguments defaultParameters = new Arguments();
//        defaultParameters.addArgument("COLLECTION", "gettingstarted");
//        defaultParameters.addArgument("RANDOM_SEED", "5150");
//        defaultParameters.addArgument("ZK_HOST", "localhost:9983");
//        defaultParameters.addArgument("searchField", "mp");
//        return defaultParameters;
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
//        collections = params.get("COLLECTION").split(",");
//        collectionList = params.get("COLLECTION");
//        String zkHost = params.get("ZK_HOST");
//        searchField = params.get("searchField");
//
//        synchronized (OvidQuerySampler.class) {
//            if (solrClient == null) {
//                try {
//                    solrClient = new CloudSolrClient(zkHost);
//                    solrClient.setParser(new XMLResponseParser());
//                    solrClient.connect();
//                } catch (Exception e) {
//                    throw new RuntimeException("Could not create solr client against zkHost=" + zkHost, e);
//                }
//
//                for (String collection: collections) {
//                    getLogger().info(solrClient.getZkStateReader().toString());
//                    List<String> perCollectionIDS = new ArrayList<>();
//                    Collection<Slice> slices = solrClient.getZkStateReader().getClusterState().getCollection(collection).getActiveSlices();
//                    for (Slice slice : slices) {
//                        Replica replica = slice.getLeader();
//                        String url = (String) replica.get("base_url");
//                        try (SolrClient client = new HttpSolrClient(url)) {
//                            buildTermsDictionary((String) replica.get("core"), client, perCollectionIDS);
//                        } catch (Exception e) {
//                            throw new RuntimeException("Could not load up unique ids", e);
//                        }
//                    }
//                    ids.put(collection, perCollectionIDS);
//                }
//
//                getLogger().info("Connected to Solr");
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
//    protected void buildTermsDictionary(String collection, SolrClient solrClient, List<String> perCollectionIDS) throws Exception {
//        SolrQuery termsQ = new SolrQuery();
//        termsQ.setParam("qt", "/terms");
//        termsQ.add("terms.fl", searchField);
//        termsQ.setParam("terms.limit", "100000");
//        QueryResponse resp = solrClient.query(collection, termsQ);
//        List<TermsResponse.Term> terms = resp.getTermsResponse().getTermMap().get(searchField);
//        for (TermsResponse.Term term : terms) {
//            perCollectionIDS.add(term.getTerm());
//        }
//    }
//
//    @Override
//    public void teardownTest(JavaSamplerContext context) {
//        if (solrClient != null) {
//            int refs = refCounter.decrementAndGet();
//            if (refs == 0) {
//
//                if (reporter != null) {
//                    reporter.report();
//                    reporter.stop();
//                }
//
//                try {
//                    solrClient.close();
//                } catch (Exception ignore) {}
//                solrClient = null;
//                getLogger().info("Shutdown olrClient.");
//            }
//        }
//
//        super.teardownTest(context);
//    }
//
//}
