package fr.inria.yajta.processor.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

public class Dico {

    private MyMap<Integer, String> dico;

    public Dico() {
        dico = new MyMap();
    }

    public Dico(JSONArray array) throws JSONException {
        for(int i = 0; i < array.length(); i++) {
            JSONObject pair = array.getJSONObject(i);
            int key = pair.getInt("key");
            dico.put(key, pair.getString("value"));
        }
    }

    public void add(String el) {
        dico.put(el.hashCode(), el);
    }

    public int getCode(String element) {
        return element.hashCode();
    }

    public String get(int code) {
        return dico.get(code);
    }

    public JSONArray toJSON() throws JSONException {
        JSONArray array = new JSONArray();
        for(int key: dico.keyList()) {
            JSONObject pair = new JSONObject();
            pair.put("key", key);
            pair.put("value", dico.get(key));
            array.put(pair);
        }
        return array;
    }
}
