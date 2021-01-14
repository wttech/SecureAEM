package com.cognifide.secureaem;

import com.cognifide.secureaem.cli.Main;
import com.cognifide.secureaem.json.Severity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;

public class TestConfiguration {
    private static final String PATH = "/test_suite.json";

    private boolean enabled;

    private String description;

    private String name;

    private Severity severity;

    private String url;

    private String urlDescription;

    private String[] extensions;

    private String[] users;

    public TestConfiguration(String testName) {
        try {
            InputStream configStream = Main.class.getResourceAsStream(PATH);
            BufferedReader configReader = new BufferedReader(new InputStreamReader(configStream, "UTF-8"));
            JsonArray testsJson = (JsonArray) new JsonParser().parse(configReader);

            for(int i = 0; i < testsJson.size(); i++) {
                if(testsJson.get(i).getAsJsonObject().get("name").getAsString().equals(testName)){
                    JsonObject jsonObject = (testsJson.get(i)).getAsJsonObject();
                    this.name = jsonObject.get("name").getAsString();
                    if(jsonObject.get("description") != null) {
                        this.description = jsonObject.get("description").getAsString();
                    }
                    if(jsonObject.get("severity") != null) {
                        this.severity = Severity.valueOf(jsonObject.get("severity").getAsString());
                    }
                    if(jsonObject.get("enabled") != null) {
                        this.enabled = jsonObject.get("enabled").getAsBoolean();
                    }
                    if(jsonObject.get("urlDescription") != null) {
                        this.urlDescription = jsonObject.get("urlDescription").getAsString();
                    }
                    if(jsonObject.get("url") != null) {
                        this.url = jsonObject.get("url").getAsString();
                    }
                    if(jsonObject.get("extensions") != null) {
                        JsonArray jsonExtensions = jsonObject.get("extensions").getAsJsonArray();
                        String[] extensions = new String[jsonExtensions.size()];
                        for (int j = 0; j < jsonExtensions.size(); j++) {
                            extensions[j] = jsonExtensions.get(j).getAsString();
                        }
                        this.extensions = extensions;
                    }
                    if(jsonObject.get("users") != null) {
                        JsonArray jsonUsers = jsonObject.get("users").getAsJsonArray();
                        String[] users = new String[jsonUsers.size()];
                        for (int j = 0; j < jsonUsers.size(); j++) {
                            users[j] = jsonUsers.get(j).getAsString();
                        }
                        this.users = users;
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlDescription() {
        return urlDescription;
    }

    public void setUrlDescription(String urlDescription) {
        this.urlDescription = urlDescription;
    }


    public String[] getExtensions() {
        return extensions;
    }

    public void setExtensions(String[] extensions) {
        this.extensions = extensions;
    }

    public String[] getUsers() {
        return users;
    }

    public void setUsers(String[] users) {
        this.users = users;
    }
}
