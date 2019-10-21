package org.apache.solr;

import java.util.Random;
import java.util.UUID;
import java.io.*;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.distribution.ZipfDistribution;

public class CreateIndex {

    public static void main(String args[]) throws Exception {

        FileWriter fileWriter = new FileWriter("/Users/apple/Work/LoadTesting/Draft1.tsv");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("id" + "\t" + "num_s" + "\t" + "test_s" + "\t" + "id_i" + "\t" + "id_ii" + "\t" + "abstract_t" + "\t" + "color_s" + "\t" + "stock_s" + "\t" + "a.stock_s" + "\t" + "price_i" + "\t" + "day_i" + "\t" + "load_d" + "\t" + "response_d" + "\t" + "eresponse_d" + "\t" + "filesize_d" + "\t" + "filesize_td" + "\t" + "service_d" + "\t" + "service_s" + "\t" + "var1_d" + "\t" + "var2_d" + "\t" + "price_f" + "\t" + "tdate_dt" + "\t" + "stock_priceA_d" + "\t" + "stock_priceB_d" + "\t" + "componentA_d" + "\t" + "componentB_d" + "\t" + "prod_ss");
        // words
        BufferedReader in = new BufferedReader(new FileReader("/Users/apple/Work/IndexLoader/loader/words.txt"));
        String[] words = new String[3001];
        for (int w = 0; w < words.length; w++) {
            words[w] = in.readLine();
        }
        in.close();
        // colors
        in = new BufferedReader(new FileReader("/Users/apple/Work/IndexLoader/colors.txt"));
        String[] colors = new String[865];
        for (int w = 0; w < words.length; w++) {
            colors[w] = in.readLine();
        }
        in.close();

        long num = Integer.parseInt(args[0]);

        long i = 0;
        String[] dates = {"2012-01-20T17:33:18Z",
                "2012-02-20T17:33:18Z",
                "2012-03-20T17:33:18Z",
                "2012-04-20T17:33:18Z",
                "2012-05-20T17:33:18Z",
                "2012-06-20T17:33:18Z",
                "2012-07-20T17:33:18Z",
                "2012-08-20T17:33:18Z",
                "2012-09-20T17:33:18Z",
                "2012-10-20T17:33:18Z",
                "2012-11-20T17:33:18Z",
                "2012-12-20T17:33:18Z",
                "2013-01-20T17:33:18Z",
                "2013-02-20T17:33:18Z",
                "2013-03-20T17:33:18Z",
                "2013-04-20T17:33:18Z",
                "2013-05-20T17:33:18Z",
                "2013-06-20T17:33:18Z",
                "2013-07-20T17:33:18Z",
                "2013-08-20T17:33:18Z",
                "2013-09-20T17:33:18Z",
                "2013-10-20T17:33:18Z",
        };


        Random rand = new Random();
        NormalDistribution normal = new NormalDistribution(40000, 2000);
        NormalDistribution noise = new NormalDistribution(100, 15);
        NormalDistribution noise1 = new NormalDistribution(0, 15);
        NormalDistribution noise2 = new NormalDistribution(0, 7);
        NormalDistribution noise3 = new NormalDistribution(0, 2);
        NormalDistribution loadD = new NormalDistribution(500, 100);
        UniformIntegerDistribution serviceLevel = new UniformIntegerDistribution(1, 4);
        NormalDistribution componentA = new NormalDistribution(500, 50);
        NormalDistribution componentB = new NormalDistribution(500, 25);
        int[] prods = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        double[] probs = {1, 2, 3, 4, 5, 6, 7, 25, 9, 10, 37, 12, 13, 14, 15};
        EnumeratedIntegerDistribution enumd = new EnumeratedIntegerDistribution(prods, probs);
        double lat = 40.7128;
        double lon = -74.0060;

        UniformIntegerDistribution hour = new UniformIntegerDistribution(1, 17);
        UniformIntegerDistribution minute = new UniformIntegerDistribution(1, 56);
        UniformIntegerDistribution second = new UniformIntegerDistribution(1, 56);


        ZipfDistribution abstractZipF = new ZipfDistribution(3000, 1);
        ZipfDistribution colorsZipF = new ZipfDistribution(865, 1);

        for (i = 0; i < num; i++) {
            String output = "";
            //System.out.println("Adding doc:"+i);
            UUID id = UUID.randomUUID();
            StringBuilder paragraph = new StringBuilder();
            for (int w = 0; w < 70; w++) {
                int word = abstractZipF.sample();
                if (word > 60) {
                    paragraph.append(words[word]);
                    paragraph.append(" ");
                }
            }

            double moveLat = lat + componentA.sample();
            double moveLong = lon + componentB.sample();
            String ho = Integer.toString(hour.sample());
            String mi = Integer.toString(minute.sample());
            String se = Integer.toString(second.sample());

            String s = rand.nextInt(5000) + "helloworld";
            output = id.toString();
            output = appendKeyValue(output, i); // num_s
            output = appendKeyValue(output, s); // test_s
            output = appendKeyValue(output, i); // id_i
            output = appendKeyValue(output, "2"); // id_ii
            output = appendKeyValue(output, "3", ","); // id_ii
            int t = rand.nextInt(20);

            output = appendKeyValue(output, paragraph.toString()); // abstract_t
            output = appendKeyValue(output, colors[colorsZipF.sample()]); // colors_s

            int year = rand.nextInt(50);
            int month = rand.nextInt(12);
            int day = rand.nextInt(30);
            double load = loadD.sample();
            double d = normal.sample();
            double d1 = serviceLevel.sample();

            double var1 = normal.sample();
            double var2 = (var1 + 100000) + noise.sample();
            float f = rand.nextFloat();
            double cA = componentA.sample();
            double cB = componentB.sample();
            int factor = 0;
            int monthIndex = rand.nextInt(11);
            if (monthIndex % 2 == 0) {
                factor = 10;
            } else {
                factor = -10;
            }


            int dayIndex = rand.nextInt(21);
            double stockPrice = noise2.sample();
            double baseA = 15;
            double baseB = 12;
            output = appendKeyValue(output, "stock_" + Integer.toString(year)); // stock_s
            output = appendKeyValue(output, Integer.toString(year)); // a.stock_s
            output = appendKeyValue(output, Integer.toString((year * 10) + factor)); // price_i
            output = appendKeyValue(output, Integer.toString(day)); // day_i
            output = appendKeyValue(output, Double.toString(load)); // load_d
            output = appendKeyValue(output, Double.toString((d / 50D) + (load * .11) + noise1.sample())); // response_d
            output = appendKeyValue(output, Double.toString((d / 2000D) + (normal.cumulativeProbability(d) * 100) + noise2.sample())); // eresponse_d
            output = appendKeyValue(output, Double.toString(d)); // filesize_d
            output = appendKeyValue(output, Double.toString(d)); // filesize_td
            output = appendKeyValue(output, Double.toString(d1)); // service_d
            output = appendKeyValue(output, "level" + new Double(d1).intValue()); // service_s
            output = appendKeyValue(output, Double.toString(var1)); // var1_d
            output = appendKeyValue(output, Double.toString(var2)); // var2_d
            output = appendKeyValue(output, Float.toString(f)); // price_f
            output = appendKeyValue(output, dates[dayIndex].replace("17", ho).replace("33", mi).replace("18", se)); // tdate_dt
            // output = appendKeyValue("loc_p", moveLat+","+moveLong);
            output = appendKeyValue(output, baseA + stockPrice); // stock_priceA_d
            output = appendKeyValue(output, baseB + stockPrice + ((double) dayIndex)); // stock_priceB_d
            output = appendKeyValue(output, cA); // componentA_d
            output = appendKeyValue(output, cB); // componentB_d
            for (int l = 0; l < 10; l++) { // prod_ss
                String ps = "product" + enumd.sample();
                if (l == 0) {
                    output = appendKeyValue(output, ps);
                }
                output = appendKeyValue(output, ps, ",");
            }

            System.out.println(output);
            printWriter.println(output);

        }
        printWriter.close();
    }
    public static String appendKeyValue(String string, Object input, String sep) {
        return string.concat(sep).concat(input.toString());
    }
    public static String appendKeyValue(String string, Object input) {
        return appendKeyValue(string, input, "\t");
    }
}
