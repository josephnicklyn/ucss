/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.models.database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author John
 */
public class JSONAssistant {
     public static JSONObject getObject(JSONObject parent, String value) {
        try {
            return parent.getJSONObject(value);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONArray getArray(JSONObject parent, String value) {
        try {
            return parent.getJSONArray(value);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONArray getArray(JSONArray json, int index) {
        try {
            return json.getJSONArray(index);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject getArrayObject(JSONArray json, int index) {
        try {
            return json.getJSONObject(index);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject getArrayObject(JSONObject json, String name, int index) {
        try {
            JSONArray obj = getArray(json, name);
            return obj.getJSONObject(index);
        } catch (JSONException e) {
            return null;
        }
    }

    public static String getString(JSONObject json, String value, String defaultValue) {
        String result = defaultValue;
        try {
            result = json.getString(value);
        } catch (JSONException e) {

        }
        return result;
    }

    public static String getString(JSONObject json, String value) {
        return getString(json, value, "");
    }

    public static String getString(JSONArray json, String value, int row) {
        String result = "";
        JSONObject obj = getArrayObject(json, row);
        if (obj != null)
            result = getString(obj, value, "");
        return result;
    }

    public static int getInt(JSONObject json, String value, int defaultValue) {
        int result = defaultValue;
        try {
            result = json.getInt(value);
        } catch (JSONException e) {

        }
        return result;
    }

    public static int getInt(JSONObject json, String value) {
        return getInt(json, value, 0);
    }

    public static JSONObject create(String source) {
        try {
            return new JSONObject(source);
        } catch (JSONException e) {
            return null;
        }
    }

    public static boolean isArray(JSONArray json){
        return json.length() != 0;
    }
}
