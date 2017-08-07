package org.apache.solr;

import jodd.datetime.TimeUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MultipleFieldsTest {
    public static void main(String[] args) throws Exception {

//        long startTime = System.nanoTime();
//        try (Directory dir = FSDirectory.open(Paths.get("/tmp/index_a_a_a"))) {
//            buildIndex(dir, "aaa", "aaa", "aaa");
//        }
//        System.out.println("index_a_a_a took" + TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS) + " seconds to build" );


        long startTime = System.nanoTime();
        try (Directory dir = FSDirectory.open(Paths.get("index_a_a_b"))) {
            buildIndex(dir, "aaa", "aaa", "bbb");
        }
        System.out.println("index_a_a_b took" + TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS) + " seconds to build" );


//        System.out.println("Warump @ " + System.nanoTime());
//        try (Directory dir = FSDirectory.open(Paths.get("warmup"))) {
//            buildIndex(dir, "aaa", "bbb", "ccc");
//        }
//        System.out.println("Start 3 @ " + System.nanoTime());
//        try (Directory dir = FSDirectory.open(Paths.get("index3a"))) {
//            buildIndex(dir, "aaa", "bbb", "ccc");
//        }
//        System.out.println("Start 2 @ " + System.nanoTime());
//        try (Directory dir = FSDirectory.open(Paths.get("index2a"))) {
//            buildIndex(dir, "aaa", "aaa", "ccc");
//        }
//        System.out.println("Start 1 @ " + System.nanoTime());
//        try (Directory dir = FSDirectory.open(Paths.get("index1a"))) {
//            buildIndex(dir, "aaa", "aaa", "aaa");
//        }
//        System.out.println("Done A @ " + System.nanoTime());
//
//
//        System.out.println("Start 1 @ " + System.nanoTime());
//        try (Directory dir = FSDirectory.open(Paths.get("index1b"))) {
//            buildIndex(dir, "aaa", "aaa", "aaa");
//        }
//        System.out.println("Start 2 @ " + System.nanoTime());
//        try (Directory dir = FSDirectory.open(Paths.get("index2b"))) {
//            buildIndex(dir, "aaa", "aaa", "ccc");
//        }
//        System.out.println("Start 3 @ " + System.nanoTime());
//        try (Directory dir = FSDirectory.open(Paths.get("index3b"))) {
//            buildIndex(dir, "aaa", "bbb", "ccc");
//        }
//        System.out.println("Done B @ " + System.nanoTime());

    }

    public static void buildIndex(Directory dir, String stored, String indexed, String dv) throws Exception {
        Random rand = new Random(42);
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(OpenMode.CREATE);

        try (IndexWriter writer = new IndexWriter(dir, iwc)) {
            List<Document> docs = new ArrayList<>(1000);
            for (int docNum = 0; docNum < 1000*100; docNum++) {
                if (0 == docNum % 1000) System.out.println(docNum);
                Document doc = new Document();
                for (int fieldNum = 0; fieldNum < 500; fieldNum++) {
                    final StringBuilder fieldValue = new StringBuilder();
                    final int numValues = 1 + rand.nextInt(10);
                    for (int i = 0; i < numValues; i++) {
                        fieldValue.append(rand.nextInt(10000));
                        fieldValue.append(" ");
                    }
                    doc.add(new StoredField(stored + "_" + fieldNum, fieldValue.toString()));
                    doc.add(new TextField(indexed + "_" + fieldNum, fieldValue.toString(), Field.Store.NO));
                    doc.add(new SortedDocValuesField(dv + "_" + fieldNum, new BytesRef(fieldValue.toString())));
                }
                docs.add(doc);
                if (docNum % 1000 ==0) {
                    writer.addDocuments(docs);
                    writer.commit();
                    docs.clear();
                }
            }
        }
//        System.out.println();
    }
}