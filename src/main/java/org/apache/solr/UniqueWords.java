package org.apache.solr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class UniqueWords {

    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader("/Users/apple/Work/IndexLoader/brand.txt"));
        Set<String> brandsSet = new HashSet();
        String line;
        while ((line = in.readLine()) != null) {
            brandsSet.add(line.toLowerCase().trim());
        }
        in.close();

        FileWriter fileWriter = new FileWriter("/Users/apple/Work/IndexLoader/brands.txt");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for (String brand :  brandsSet) {
            printWriter.println(brand);
        }
        printWriter.close();
    }
}
