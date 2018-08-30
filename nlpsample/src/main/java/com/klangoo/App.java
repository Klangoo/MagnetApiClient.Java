package com.klangoo;

import java.util.HashMap;
import java.util.Map;

/**
 * Sample NLP Code
 *
 */
public class App 
{
    static final String ENDPOINT = "https://nlp.klangoo.com/Service.svc";
    static final String CALK = "enter your calk here";
    static final String SECRET_KEY = "enter your secret key here";

    public static void main(String[] args) throws Exception {
        processDocument();

        getSummary();

        getEntities();

        getCategories();

        getKeyTopics();
    }

    private static void processDocument() throws Exception {
        System.out.println("\nprocess document()");

        MagnetAPIClient client = new MagnetAPIClient(ENDPOINT, CALK, SECRET_KEY);

        Map<String, String> request = new HashMap<String, String>();
        request.put("text", "The United States of America (USA), commonly known as the United States (U.S.) or America, is a federal republic composed of 50 states, a federal district, five major self-governing territories, and various possessions.");
        request.put("lang", "en");
        request.put("format", "json");

        String json = client.CallWebMethod("ProcessDocument", request, "POST");
        
        System.out.println(json);
    }

    private static void getSummary() throws Exception {
        System.out.println("\nget summary()");

        MagnetAPIClient client = new MagnetAPIClient(ENDPOINT, CALK, SECRET_KEY);

        Map<String, String> request = new HashMap<String, String>();
        request.put("text", "The United States of America (USA), commonly known as the United States (U.S.) or America, is a federal republic composed of 50 states, a federal district, five major self-governing territories, and various possessions.");
        request.put("lang", "en");
        request.put("format", "json");

        String json = client.CallWebMethod("GetSummary", request, "POST");
        
        System.out.println(json);
    }

    private static void getEntities() throws Exception {
        System.out.println("\nget entities()");

        MagnetAPIClient client = new MagnetAPIClient(ENDPOINT, CALK, SECRET_KEY);

        Map<String, String> request = new HashMap<String, String>();
        request.put("text", "The United States of America (USA), commonly known as the United States (U.S.) or America, is a federal republic composed of 50 states, a federal district, five major self-governing territories, and various possessions.");
        request.put("lang", "en");
        request.put("format", "json");

        String json = client.CallWebMethod("GetEntities", request, "POST");
        
        System.out.println(json);
    }

    private static void getCategories() throws Exception {
        System.out.println("\nget categories()");

        MagnetAPIClient client = new MagnetAPIClient(ENDPOINT, CALK, SECRET_KEY);

        Map<String, String> request = new HashMap<String, String>();
        request.put("text", "The United States of America (USA), commonly known as the United States (U.S.) or America, is a federal republic composed of 50 states, a federal district, five major self-governing territories, and various possessions.");
        request.put("lang", "en");
        request.put("format", "json");

        String json = client.CallWebMethod("GetCategories", request, "POST");
        
        System.out.println(json);
    }

    private static void getKeyTopics() throws Exception {
        System.out.println("\nget key topics()");

        MagnetAPIClient client = new MagnetAPIClient(ENDPOINT, CALK, SECRET_KEY);

        Map<String, String> request = new HashMap<String, String>();
        request.put("text", "The United States of America (USA), commonly known as the United States (U.S.) or America, is a federal republic composed of 50 states, a federal district, five major self-governing territories, and various possessions.");
        request.put("lang", "en");
        request.put("format", "json");

        String json = client.CallWebMethod("GetKeyTopics", request, "POST");
        
        System.out.println(json);
    }
}
