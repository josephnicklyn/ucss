/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.models.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author John
 */
public class APIUtil {
    
    public static final String TERMS_API = "https://api.svsu.edu/terms";
    public static final String PREFIX_API = "https://api.svsu.edu/prefixes";
    public static final String COURSE_API = "https://api.svsu.edu/courses?term=%s&prefix=%s";

    private static ObservableList<SimpleListItem>  termsList;
    private static ObservableList<SimpleListItem>  prefixList;
    
    public static String getRemoteFile(String remoteFile) throws Exception, IOException {
        URL  apiURL = new URL(remoteFile);
        BufferedReader in = new BufferedReader(new InputStreamReader(apiURL.openStream()));

        String inputLine;
        StringBuilder b = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            b.append(inputLine);
        in.close();
        return b.toString();
    }
    
    public static String loadTerms() throws Exception {
        return getRemoteFile(TERMS_API);
    }
    
    public static ObservableList<SimpleListItem> getTermsList(CompleteAction actionComplete) throws Exception {
        if (termsList == null) {
            termsList = FXCollections.observableArrayList();
            
            new Thread(new Runnable() { 
            public void run(){   
                JSONObject json;
                try {
                    json = new JSONObject(getRemoteFile(TERMS_API));
                    JSONArray arr = JSONAssistant.getArray(json, "terms");
                    if (arr != null) {
                        for(int i = 0; i < arr.length(); i++) {
                            JSONObject obj = JSONAssistant.getArrayObject(arr, i);
                            String code = JSONAssistant.getString(obj, "code");
                            String text = JSONAssistant.getString(obj, "text");
                            termsList.add(new SimpleListItem(code, text));
                        }
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            actionComplete.actionComplete();  
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            actionComplete.actionFailed();  
                        }
                    });
                }
                
            }
            }).start();
        }
        return termsList;
    }
  
    public static ObservableList<SimpleListItem> getPrefixList(CompleteAction actionComplete) throws Exception {
        if (prefixList == null) {
            prefixList = FXCollections.observableArrayList();
            
            new Thread(new Runnable() { 
            public void run(){   
                JSONObject json;
                try {
                    json = new JSONObject(getRemoteFile(PREFIX_API));
                    JSONArray arr = JSONAssistant.getArray(json, "prefixes");
                    if (arr != null) {
                        for(int i = 0; i < arr.length(); i++) {
                            JSONObject obj = JSONAssistant.getArrayObject(arr, i);
                            String prefix = JSONAssistant.getString(obj, "prefix");
                            String description = JSONAssistant.getString(obj, "description");
                            prefixList.add(new SimpleListItem(prefix, description));
                        }
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            actionComplete.actionComplete();  
                        }
                    });     
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            actionComplete.actionFailed();  
                        }
                    });
                }
            }
            }).start();      
        }   
        return prefixList;
    }
    
    
    
    public static void getAPIData(CompleteAction actionComplete,
            String term) {
        if (prefixList == null) {
            return;
        }
        new Thread(new Runnable() { 
            public void run(){   
                JSONObject json;
                try {
                    for(SimpleListItem si: prefixList) {
                        String url = String.format(COURSE_API, term, si.getKey());
                        String a = (getRemoteFile(url));
                        actionComplete.onGotJSON(a);  
                    }
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            actionComplete.actionComplete();
                        }
                    });
                } catch (Exception ex) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            actionComplete.actionFailed();  
                        }
                    });
                }
                
            }
            }).start();
    }
    
    public static interface CompleteAction {
        public void actionComplete();
        public void actionFailed();
        public void onGotJSON(String json);
    }
    
    public static class SimpleListItem {
        private final String key, value;
        public SimpleListItem(
                String key, 
                String value
        ) {
            this.key = key;
            this.value = value;
        }
        
        public String getKey() {
            return key;
        }
        
        public String getValue() {
            return value;
        }
        
        @Override public String toString() {
            return key + ": " + value;
        }
    }
    
}
