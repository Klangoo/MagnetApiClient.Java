**This library allows you to easily use the Magnet API via Java.**

# Table of Contents

* [About](#about)
* [Installation](#installation)
* [Usage](#usage)

<a name="about"></a>
# About

Klangoo NLP API is a natural language processing (NLP) service that uses the rule-based paradigm and machine learning to recognize the aboutness of text. The service recognizes the category of the text, extracts key disambiguated topics, places, people, brands, events, and 41 other types of names; analyzes text using tokenization, parts of speech, parsing, word sense disambiguation, named entity recognition; and automatically finds the relatedness score between documents.

[Read More](https://klangoosupport.zendesk.com/hc/en-us/categories/360000812171-Klangoo-Natural-Language-API).

[Signup for a free trail](https://connect.klangoo.com/pub/Signup/)

<a name="installation"></a>
# Installation

## Prerequisites

- An API Key Provided by [Klangoo](https://klangoosupport.zendesk.com/hc/en-us/articles/360015236872-Step-2-Registering-to-Klangoo-NLP-API)
- An API Secret Provided by [Klangoo](https://klangoosupport.zendesk.com/hc/en-us/articles/360015236872-Step-2-Registering-to-Klangoo-NLP-API)


## Install

### Install  with Maven:
Add the following dependency to your pom.xml file:

```xml
<dependency>
  <groupId>com.klangoo</groupId>
  <artifactId>magnethttpclient</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Install  manually:
Download and add to your project the file [MagnetAPIClient.java](https://github.com/Klangoo/MagnetApiClient.Java/blob/master/magnethttpclient/src/main/java/com/klangoo/MagnetAPIClient.java)

<a name="usage"></a>
# Usage

This quick start tutorial will show you how to process a text.

## Initialize the client

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