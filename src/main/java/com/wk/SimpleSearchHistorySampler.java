//package com.wk;
//
//import com.codahale.metrics.ConsoleReporter;
//import com.codahale.metrics.MetricRegistry;
//import org.apache.jmeter.config.Arguments;
//import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
//import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
//import org.apache.jmeter.samplers.SampleResult;
//import org.apache.lucene.util.TestUtil;
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
//import org.noggit.JSONParser;
//
//import java.io.Serializable;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//
///* Steps:
// * ./bin/solr start -c -s example/cloud/node1/solr
// * ( assuming a collection called ovid has already been created there with data)
// * Couchbase server is running
// * The solr running has the search history code deployed as well.
// *
// * Example query:
// * To save to couchbase : http://localhost:8983/solr/ovid/searchhistory?q=heart&queryName=dedupQuery521&wt=json&indent=on
// * To fire search history query : http://localhost:8983/solr/ovid/searchhistory?q=(![dedupQuery521]&wt=json&indent=on
// *
// * The code looks at all the collections, collects at max 100 terms from the ti_w field of each replica and stores them.
// * On query time it picks one term at random , fires a query and saves the results to couchbase. A subsequent
// * search history call with is made to retrieve the same data.
// *
// * So if you specify N in your test run, 2N queries will be fired. So this will test puts and gets to couchbase.
// *
// * Compile package: mvn clean package;cp target/solr-load-test-1.0-SNAPSHOT-jar-with-dependencies.jar ~/apache-jmeter-2.13/lib/ext/
// * Run Test: ./jmeter -n -t ~/lucid-repos/solr-load-test/src/main/resources/query.jmx -l query.log
// *
// * This is good to check couchbase performance.
// */
//public class SimpleSearchHistorySampler extends AbstractJavaSamplerClient implements Serializable {
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
//    private static List<String> terms = new ArrayList();
//    private static String collectionList;
//
//    @Override
//    public SampleResult runTest(JavaSamplerContext context) {
//        SampleResult result = new SampleResult();
//        result.sampleStart();
//
//        int collectionPos = random.nextInt(collections.length);
//        String collection = collections[collectionPos];
//
//        int termPos = random.nextInt(terms.size());
//        String term = terms.get(termPos);
//
//        ModifiableSolrParams solrParams = new ModifiableSolrParams();
//        solrParams.add("q", "ti_w:" + term);
//        solrParams.add("collection", collectionList);
//        solrParams.add("wt", "json");
//        solrParams.add("qt", "/searchhistory");
//        String queryName = TestUtil.randomSimpleString(random);
//        solrParams.add("queryName", queryName);
//
//        ModifiableSolrParams searchHistoryParams = new ModifiableSolrParams();
//        searchHistoryParams.add("q", "(![" + queryName + "]");  // (![dedupQuery521]
//        searchHistoryParams.add("collection", collectionList);
//        searchHistoryParams.add("wt", "json");
//        searchHistoryParams.add("qt", "/searchhistory");
//
//        final com.codahale.metrics.Timer.Context queryTimerCtxt = queryTimer.time();
//        try {
//            QueryResponse qr = solrClient.query(collection, solrParams);
//            QueryResponse shqr = solrClient.query(collection, searchHistoryParams);
//
//            if (qr.getResults().getNumFound() == 0) {
//                noResultsCounter.inc();
//            }
//            if (qr.getResults().getNumFound() != shqr.getResults().getNumFound()) {
//                excCounter.inc();
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
//
//        synchronized (SimpleSearchHistorySampler.class) {
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
//                    Collection<Slice> slices = solrClient.getZkStateReader().getClusterState().getCollection(collection).getActiveSlices();
//                    for (Slice slice : slices) {
//                        Replica replica = slice.getLeader();
//                        String url = (String) replica.get("base_url");
//                        try (SolrClient client = new HttpSolrClient(url)) {
//                            buildTermsDictionary((String) replica.get("core"), client, terms);
//                        } catch (Exception e) {
//                            throw new RuntimeException("Could not load up unique ids", e);
//                        }
//                    }
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
//        termsQ.add("terms.fl", "ti_w");
//        termsQ.setParam("terms.limit", "100");
//        QueryResponse resp = solrClient.query(collection, termsQ);
//        List<TermsResponse.Term> terms = resp.getTermsResponse().getTermMap().get("ti_w");
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
