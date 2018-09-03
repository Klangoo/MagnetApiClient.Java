# Magnet API Client for Java

# Getting Started

## Install

### Install  manually:
Download and add to your project the file [MagnetAPIClient.java](https://github.com/Klangoo/MagnetApiClient.Java/blob/master/magnethttpclient/src/main/java/com/klangoo/MagnetAPIClient.java)

## Quick Start

This quick start tutorial will show you how to process a text

### Initialize the client

To begin, you will need to initialize the client. In order to do this you will need your API Key **CALK** and **Secret Key**.
You can find both on [your Klangoo account](https://connect.klangoo.com/).

```java
import com.klangoo.MagnetAPIClient;
import java.util.HashMap;
import java.util.Map;

public class Sample {

    static final String ENDPOINT = "https://nlp.klangoo.com/Service.svc";
    static final String CALK = "enter your calk here";
    static final String SECRET_KEY = "enter your secret key here";

    public static void main(String[] args) throws Exception {
        MagnetAPIClient client = new MagnetAPIClient(ENDPOINT, CALK, SECRET_KEY);

        Map<String, String> request = new HashMap<String, String>();
        request.put("text", "Real Madrid transfer news");
        request.put("lang", "en");
        request.put("format", "json");

        String json = client.CallWebMethod("ProcessDocument", request, "POST");
    }
}
```