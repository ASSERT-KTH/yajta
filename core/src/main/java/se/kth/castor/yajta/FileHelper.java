package se.kth.castor.yajta;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

/**
 * Created by nharrand on 12/07/17.
 */
public class FileHelper {

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

    public static JSONArray readArrayFromFile(File f) {
        JSONArray jsonArray = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }

            jsonArray = new JSONArray(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return jsonArray;
    }

    public static void printResults(String str, File out) {

        try {
            PrintWriter w = new PrintWriter(out);
            w.print(str);
            w.close();
        } catch (Exception ex) {
            System.err.println("Problem writing log");
            ex.printStackTrace();
        }
    }
}
