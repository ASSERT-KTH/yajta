package fr.inria.tie;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import fr.inria.yajta.FileHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;

/**
 * Created by nharrand on 12/07/17.
 */
public class Report {

    @Parameter(names = {"--help", "-h"}, help = true, description = "Display this message.")
    private boolean help;
    @Parameter(names = {"--input-dir", "-i"}, description = "Directory containing test reports")
    private String inputFileName;
    @Parameter(names = {"--output-file", "-o"}, description = "File containing the dictionary. Default: tie-report.json")
    private String outputFileName = "tie-report.json";

    boolean printTests = true;

    public static void printUsage(JCommander jcom) {
        jcom.usage();
    }

    public static void main( String ... args ) {
        Report report = new Report();
        JCommander jcom = new JCommander(report,args);

        if(report.help || report.inputFileName == null) {
            printUsage(jcom);
        } else {
            File in, out;
            in = new File(report.inputFileName);
            if(!in.exists() || !in.isDirectory()){
                System.out.println("Incorrect parameter -i / --input-dir, expect a valid directory, found \"" + report.inputFileName + "\".");
                printUsage(jcom);
                return;
            }
            out = new File(report.outputFileName);
            report.gatherReport(in,out);
        }
    }

    //Map<String, Set<String>> testMethods = new HashMap<>();
    Map<String, Set<String>> methodTests = new HashMap<>();

    public void gatherReport(File in, File out) {
        loadTestReports(in.listFiles());
        export(out);
    }

    public void loadTestReports(File[] testReports) {
        for(File tr : testReports) {
            try {
                JSONObject jsonReport = FileHelper.readFromFile(tr);
                JSONArray threads = jsonReport.getJSONArray("report");
                for(int i = 0; i < threads.length(); i++) {
                    JSONObject t = threads.getJSONObject(i);
                    JSONArray methods = methods = t.getJSONArray("methods");
                    for(int j = 0; j < methods.length(); j++) {
                        String m = methods.getString(j);
                        Set<String> tests;
                        if(methodTests.containsKey(m)) tests =  methodTests.get(m);
                        else tests = new HashSet<>();

                        tests.add(tr.getName());
                        methodTests.put(m,tests);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void export(File out) {
        if(out == null) out = new File("tie-report.json");
        String buf = "{\"methodList\":[";
        boolean isFirst = true;
        for(Map.Entry<String, Set<String>> e: methodTests.entrySet()) {
            if(isFirst) isFirst = false;
            else buf += ",";
            buf += "{\"method\": \"" + e.getKey() + "\", \"called-in\":[";
            boolean f = true;
            for(String t: e.getValue()) {
                if(f) f = false;
                else buf += ",";
                buf += "\"" + t + "\"";
            }
            buf += "]}";
        }
        buf += "]}";

        FileHelper.printResults(buf, out);
    }
    //List testreport
    //Load
    //reverse
    //print
}
