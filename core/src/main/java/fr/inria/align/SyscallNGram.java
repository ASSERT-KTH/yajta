package fr.inria.align;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SyscallNGram {
    //Map<String,Integer> ngramsFreqencies = new HashMap<>();
    public Map<String,Double> ngramsFreqencies = new HashMap<>();
    int total = 0;

    public SyscallNGram(File src) throws JSONException {
        this(readFromFile(src), null);
    }

    public SyscallNGram(JSONObject src) throws JSONException {
        this(src, null);
    }

    public SyscallNGram(File src, String containerName) throws JSONException {
        this(readFromFile(src), containerName);
    }

    public SyscallNGram(JSONObject src, String containerName) throws JSONException {
        String name;
        if(containerName != null) name = containerName;
        else name = (String) src.keys().next();
        JSONObject ngrams = src.getJSONObject(name);

        Map<String,Integer> tmp = new HashMap<>();
        Iterator iterator = ngrams.keys();
        while(iterator.hasNext()) {
            String ngram = (String) iterator.next();
            int freq = ngrams.getInt(ngram);
            total += freq;
            tmp.put(ngram,freq);
        }
        for(String ngram : tmp.keySet()) {
            ngramsFreqencies.put(ngram,(double) tmp.get(ngram) / (double) total);
        }
    }

    public double distance(SyscallNGram other) {
        double res = 0;
        Set<String> keys = new HashSet<>();
        keys.addAll(ngramsFreqencies.keySet());
        keys.addAll(other.ngramsFreqencies.keySet());
        for(String ngram : keys) {
            double d1 = 0;
            double d2 = 0;
            if(ngramsFreqencies.containsKey(ngram)) {
                d1 = ngramsFreqencies.get(ngram);
            }
            if(other.ngramsFreqencies.containsKey(ngram)) {
                d2 = other.ngramsFreqencies.get(ngram);
            }
            res += Math.abs(d1 - d2);
        }
        return res / 2.0D;
    }

    public double distance2(SyscallNGram other) {
        double res = 0;
        Set<String> keys = new HashSet<>();
        keys.addAll(ngramsFreqencies.keySet());
        keys.addAll(other.ngramsFreqencies.keySet());
        for(String ngram : keys) {
            boolean d1;
            boolean d2;
            d1 = ngramsFreqencies.containsKey(ngram);
            d2 = other.ngramsFreqencies.containsKey(ngram);
            res += (d1==d2) ? 0 : 1;
        }
        return res / ((double) total + other.total);
    }

    public double distance3(SyscallNGram other) {
        return (this.distance(other) + this.distance2(other)) / 2.0D;
    }

    public static JSONObject readFromFile(File f) {
        JSONObject jsonObject = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }

            jsonObject = new JSONObject(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return jsonObject;
    }
}
