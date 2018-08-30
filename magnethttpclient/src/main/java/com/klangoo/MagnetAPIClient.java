/**************************************************************************************
 * 
 * file: MagnetAPIClient.java
 * 
 * Copyright 2018, Klangoo Inc.
 * 
 * ************************************************************************************/
package com.klangoo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


/**
 *      Example:
 *      
 *		MagnetAPIClient magnetAPIClient = new MagnetAPIClient(ENDPOINT_URI, CALK, SECRET_KEY);
 *		Map<String, String> request = new HashMap<String, String>();
 *		request.put("text", "Hello World");
 *		String response = magnetAPIClient.CallWebMethod("ProcessDocument", request, "POST");
 */
public class MagnetAPIClient {
	
	private String _endpointUri;
	private String _calk;
	private MagnetSigner _magnetSigner = null;
	
	public MagnetAPIClient(String endpointUri, String calk)
	{
		_endpointUri = endpointUri;
		_calk = calk;
	}
	
	public MagnetAPIClient(String endpointUri, String calk, String secretKey) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException
	{
		_endpointUri = endpointUri;
		_calk = calk;
		
        if (secretKey != null && secretKey.length() > 0 )
        {
            _magnetSigner = new MagnetSigner(_endpointUri, secretKey);
        }
	}

    /// <summary>
    /// Call Web Method (Http request)
    /// sign request if secret key is provided in the constructor
    /// </summary>
    /// <param name="methodName">method name</param>
    /// <param name="request">query string</param>
    /// <param name="requestMethod">GET or POST</param>
    /// <returns>response</returns>
    public String CallWebMethod(String methodName, Map<String, String> request, String requestMethod) throws IOException
    {
        return CallWebMethod(methodName, request, requestMethod, true);
    }
    
	/// <summary>
    /// Call Web Method (Http request)
    /// </summary>
    /// <param name="methodName">method name</param>
    /// <param name="request">query string</param>
    /// <param name="requestMethod">GET or POST</param>
    /// <param name="signRequest">sign request if secret key is provided in the constructor</param>
    /// <returns></returns>
    public String CallWebMethod(String methodName, Map<String, String> request, String requestMethod, boolean signRequest) throws IOException
    {
        if (!HasCalk(request)) { request.put("calk", _calk); }

        String queryString;
        if (signRequest && _magnetSigner != null)
        {
            queryString = _magnetSigner.GetSignedQueryString(methodName, request, requestMethod);
        }
        else
        {
            queryString = ConstructQueryString(request);
        }
        return CallWebMethod__(methodName, queryString, requestMethod);
    }
    
    /// <summary>
    /// Call Web Method (Http request)
    /// sign request if secret key is provided in the constructor
    /// </summary>
    /// <param name="methodName">method name</param>
    /// <param name="request">query string</param>
    /// <param name="requestMethod">GET or POST</param>
    /// <returns>response</returns>
    public String CallWebMethod(String methodName, String queryString, String requestMethod) throws Exception
    {
        return CallWebMethod(methodName, queryString, requestMethod, true);
    }
    
	/// <summary>
    /// Call Web Method (Http request)
    /// </summary>
    /// <param name="methodName">method name</param>
    /// <param name="request">query string</param>
    /// <param name="requestMethod">GET or POST</param>
    /// <param name="signRequest">sign request if secret key is provided in the constructor</param>
    /// <returns></returns>
    public String CallWebMethod(String methodName, String queryString, String requestMethod, boolean signRequest) throws Exception
    {
        if (queryString == "") { queryString = "calk=" + _calk; }
        else if (!HasCalk(queryString)) { queryString = "calk=" + _calk + "&" + queryString; }

        if (signRequest && _magnetSigner != null)
        {
            queryString = _magnetSigner.GetSignedQueryString(methodName, queryString, requestMethod);
        }
        return CallWebMethod__(methodName, queryString, requestMethod);
    }
    
	private String CallWebMethod__(String methodName, Map<String, String> request, String requestMethod) throws Exception
    {
		String queryString = ConstructQueryString(request);
		return CallWebMethod__(methodName, queryString, requestMethod);
    }
	 
	private String CallWebMethod__(String methodName, String queryString, String requestMethod) throws IOException
    {
        if (requestMethod.toUpperCase() == "GET")
        {
        	URL url = new URL(_endpointUri + "/" + methodName + "?" + queryString);
    		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    		InputStream inStr = conn.getInputStream();
    		return InputStreamToString(new InputStreamReader(inStr));
        }
        else // POST
        {
        	URL url = new URL(_endpointUri + "/" + methodName);
    		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    		conn.setDoOutput(true);
    		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
    		wr.write(queryString);
    		wr.flush();
    		InputStream inStr = conn.getInputStream();
    		return InputStreamToString(new InputStreamReader(inStr));
        }
    }
	
	private String InputStreamToString(InputStreamReader reader) throws IOException {
		final char[] buffer = new char[0x10000];
		StringBuilder out = new StringBuilder();
		int read;
		do {
		  read = reader.read(buffer, 0, buffer.length);
		  if (read>0) {
		    out.append(buffer, 0, read);
		  }
		} while (read>=0);
		return out.toString();
	}
	
	private String ConstructQueryString(Map<String, String> request)
    {
        if (request.isEmpty())
        {
            return "";
        }
        
        StringBuffer builder = new StringBuffer();
       
        Iterator<Map.Entry<String, String>> iter = request.entrySet().iterator();
        
        while (iter.hasNext()) {
            Map.Entry<String, String> kvp = iter.next();
            builder.append(percentEncodeRfc3986(kvp.getKey()));
            builder.append("=");
            builder.append(percentEncodeRfc3986((kvp.getValue() != null) ? kvp.getValue() : ""));
            builder.append("&");
        }
        
        String queryString = builder.toString();
        queryString = queryString.substring(0, queryString.length() - 1);
        return queryString;
    }
	
	private String percentEncodeRfc3986(String s) {
        String out;
        try {
            out = URLEncoder.encode(s, "UTF-8")
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            out = s;
        }
        return out;
    }
	
	private boolean HasCalk(Map<String, String> request)
    {
		Iterator<Map.Entry<String, String>> iter = request.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> kvp = iter.next();
            if (kvp.getKey().toLowerCase() == "calk") {
            	return true;
            }   
        }
        return false;
    }
	
	private boolean HasCalk(String queryString)
    {
         String queryStringLower = queryString.toLowerCase();
         
         if (queryStringLower.startsWith("calk=") || queryStringLower.contains("?calk=") || queryStringLower.contains("&calk="))
         {
             return true;
         }
         return false;
    }
	 
	class MagnetSigner {
		
		private String _endpointUri;
        private byte[] _secretKey;
        private Mac _hmac;
        private SecretKeySpec _secretKeySpec = null;
        
        public MagnetSigner(String endpointUri, String secretKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException
        {
            _endpointUri = endpointUri;
            _secretKey = secretKey.getBytes("UTF-8");
            _secretKeySpec = new SecretKeySpec(_secretKey, "HmacSHA256");
            _hmac = Mac.getInstance("HmacSHA256");
            _hmac.init(_secretKeySpec);
        }
        
        public String GetSignedQueryString(String methodName, String queryString, String requestMethod) throws UnsupportedEncodingException
        {
            Map<String, String> request = this.CreateParameterMap(queryString);
            return GetSignedQueryString(methodName, request, requestMethod);
        }
        
        public String GetSignedQueryString(String methodName, Map<String, String> request, String requestMethod) throws UnsupportedEncodingException
        {
        	// Add Timestamp to the requests.
            request.put("timestamp", this.GetTimestamp());
            
            // Use a SortedDictionary to get the parameters in natural byte order
        	SortedMap<String, String> sortedMap = new TreeMap<String, String>(request);

            // Get the canonical query string
            String canonicalQueryString = this.ConstructCanonicalQueryString(sortedMap);

            String signature = GetSignatureUsingCanonicalQueryString(methodName, canonicalQueryString, requestMethod);

            // now construct the complete URL and return to caller.
            StringBuilder qsBuilder = new StringBuilder();
            qsBuilder.append(canonicalQueryString).append("&signature=").append(signature);

            return qsBuilder.toString();
        }
        
        private String GetSignatureUsingCanonicalQueryString(String methodName, String canonicalQueryString, String requestMethod) throws UnsupportedEncodingException
        {
            // Derive the bytes needs to be signed.
            StringBuilder builder = new StringBuilder();
            builder.append(requestMethod.toLowerCase())
                .append("\n")
                .append(_endpointUri.toLowerCase())
                .append("\n")
                .append(methodName.toLowerCase())
                .append("\n")
                .append(canonicalQueryString);

            String stringToSign = builder.toString();
            byte[] toSign = stringToSign.getBytes("UTF-8");

            // Compute the signature and convert to Base64.
            byte[] sigBytes = _hmac.doFinal(toSign);
            
            String signature = new String(Base64.encode(sigBytes));

            return this.PercentEncodeRfc3986(signature);
        }
        
        ///
        /// Construct the canonical query string from the sorted parameter map.
        ///
        private String ConstructCanonicalQueryString(SortedMap<String, String> sortedParamMap)
        {
        	 if (sortedParamMap.isEmpty()) {
                 return "";
             }

             StringBuffer builder = new StringBuffer();
             Iterator<Map.Entry<String, String>> iter = sortedParamMap.entrySet().iterator();

             while (iter.hasNext()) {
                 Map.Entry<String, String> kvp = iter.next();
                 builder.append(percentEncodeRfc3986(kvp.getKey()));
                 builder.append("=");
                 builder.append(percentEncodeRfc3986((kvp.getValue() != null) ? kvp.getValue() : ""));
                 builder.append("&");
             }
             
             String canonicalString = builder.toString();
             canonicalString = canonicalString.substring(0, canonicalString.length() - 1);
             return canonicalString;
        }
        
        ///
        /// Current time in IS0 8601 format
        ///
        private String GetTimestamp() {
            String timestamp = null;
            Calendar cal = Calendar.getInstance();
            DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
            timestamp = dfm.format(cal.getTime());
            return timestamp;
        }
        
        private String PercentEncodeRfc3986(String s) {
            String out;
            try {
                out = URLEncoder.encode(s, "UTF-8")
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
            } catch (UnsupportedEncodingException e) {
                out = s;
            }
            return out;
        }
        
        private Map<String, String> CreateParameterMap(String queryString) {
            Map<String, String> map = new HashMap<String, String>();
            String[] pairs = queryString.split("&");

            for (String pair: pairs) {
                if (pair.length() < 1) {
                    continue;
                }

                String[] tokens = pair.split("=",2);
                for(int j=0; j<tokens.length; j++)
                {
                    try {
                        tokens[j] = URLDecoder.decode(tokens[j], "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                    }
                }
                switch (tokens.length) {
                    case 1: {
                        if (pair.charAt(0) == '=') {
                            map.put("", tokens[0]);
                        } else {
                            map.put(tokens[0], "");
                        }
                        break;
                    }
                    case 2: {
                        map.put(tokens[0], tokens[1]);
                        break;
                    }
                }
            }
            return map;
        }
	}
	
	/**
	 * This class provides encode/decode for RFC 2045 Base64 as
	 * defined by RFC 2045, N. Freed and N. Borenstein.
	 * RFC 2045: Multipurpose Internet Mail Extensions (MIME)
	 * Part One: Format of Internet Message Bodies. Reference
	 * 1996 Available at: http://www.ietf.org/rfc/rfc2045.txt
	 * This class is used by XML Schema binary format validation
	 *
	 * This implementation does not encode/decode streaming
	 * data. You need the data that you will encode/decode
	 * already on a byte arrray.
	 *
	 * @xerces.internal
	 *
	 * @author Jeffrey Rodriguez
	 * @author Sandy Gao
	 */
	static class Base64 {
		
		static private final int  BASELENGTH         = 128;
	    static private final int  LOOKUPLENGTH       = 64;
	    static private final int  TWENTYFOURBITGROUP = 24;
	    static private final int  EIGHTBIT           = 8;
	    static private final int  SIXTEENBIT         = 16;
	    static private final int  SIXBIT             = 6;
	    static private final int  FOURBYTE           = 4;
	    static private final int  SIGN               = -128;
	    static private final char PAD                = '=';
	    static private final boolean fDebug          = false;
	    static final private byte [] base64Alphabet        = new byte[BASELENGTH];
	    static final private char [] lookUpBase64Alphabet  = new char[LOOKUPLENGTH];
	    
	    static {

	        for (int i = 0; i < BASELENGTH; ++i) {
	            base64Alphabet[i] = -1;
	        }
	        for (int i = 'Z'; i >= 'A'; i--) {
	            base64Alphabet[i] = (byte) (i-'A');
	        }
	        for (int i = 'z'; i>= 'a'; i--) {
	            base64Alphabet[i] = (byte) ( i-'a' + 26);
	        }

	        for (int i = '9'; i >= '0'; i--) {
	            base64Alphabet[i] = (byte) (i-'0' + 52);
	        }

	        base64Alphabet['+']  = 62;
	        base64Alphabet['/']  = 63;

	        for (int i = 0; i<=25; i++)
	            lookUpBase64Alphabet[i] = (char)('A'+i);

	        for (int i = 26,  j = 0; i<=51; i++, j++)
	            lookUpBase64Alphabet[i] = (char)('a'+ j);

	        for (int i = 52,  j = 0; i<=61; i++, j++)
	            lookUpBase64Alphabet[i] = (char)('0' + j);
	        lookUpBase64Alphabet[62] = (char)'+';
	        lookUpBase64Alphabet[63] = (char)'/';

	    }
	    
		 /**
	     * Encodes hex octects into Base64
	     *
	     * @param binaryData Array containing binaryData
	     * @return Encoded Base64 array
	     */
	    public static String encode(byte[] binaryData) {

	        if (binaryData == null)
	            return null;

	        int      lengthDataBits    = binaryData.length*EIGHTBIT;
	        if (lengthDataBits == 0) {
	            return "";
	        }

	        int      fewerThan24bits   = lengthDataBits%TWENTYFOURBITGROUP;
	        int      numberTriplets    = lengthDataBits/TWENTYFOURBITGROUP;
	        int      numberQuartet     = fewerThan24bits != 0 ? numberTriplets+1 : numberTriplets;
	        char     encodedData[]     = null;

	        encodedData = new char[numberQuartet*4];

	        byte k=0, l=0, b1=0,b2=0,b3=0;

	        int encodedIndex = 0;
	        int dataIndex   = 0;
	        if (fDebug) {
	            System.out.println("number of triplets = " + numberTriplets );
	        }

	        for (int i=0; i<numberTriplets; i++) {
	            b1 = binaryData[dataIndex++];
	            b2 = binaryData[dataIndex++];
	            b3 = binaryData[dataIndex++];

	            if (fDebug) {
	                System.out.println( "b1= " + b1 +", b2= " + b2 + ", b3= " + b3 );
	            }

	            l  = (byte)(b2 & 0x0f);
	            k  = (byte)(b1 & 0x03);

	            byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);

	            byte val2 = ((b2 & SIGN)==0)?(byte)(b2>>4):(byte)((b2)>>4^0xf0);
	            byte val3 = ((b3 & SIGN)==0)?(byte)(b3>>6):(byte)((b3)>>6^0xfc);

	            if (fDebug) {
	                System.out.println( "val2 = " + val2 );
	                System.out.println( "k4   = " + (k<<4));
	                System.out.println( "vak  = " + (val2 | (k<<4)));
	            }

	            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
	            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val2 | ( k<<4 )];
	            encodedData[encodedIndex++] = lookUpBase64Alphabet[ (l <<2 ) | val3 ];
	            encodedData[encodedIndex++] = lookUpBase64Alphabet[ b3 & 0x3f ];
	        }

	        // form integral number of 6-bit groups
	        if (fewerThan24bits == EIGHTBIT) {
	            b1 = binaryData[dataIndex];
	            k = (byte) ( b1 &0x03 );
	            if (fDebug) {
	                System.out.println("b1=" + b1);
	                System.out.println("b1<<2 = " + (b1>>2) );
	            }
	            byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
	            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
	            encodedData[encodedIndex++] = lookUpBase64Alphabet[ k<<4 ];
	            encodedData[encodedIndex++] = PAD;
	            encodedData[encodedIndex++] = PAD;
	        } else if (fewerThan24bits == SIXTEENBIT) {
	            b1 = binaryData[dataIndex];
	            b2 = binaryData[dataIndex +1 ];
	            l = ( byte ) ( b2 &0x0f );
	            k = ( byte ) ( b1 &0x03 );

	            byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
	            byte val2 = ((b2 & SIGN)==0)?(byte)(b2>>4):(byte)((b2)>>4^0xf0);

	            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
	            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val2 | ( k<<4 )];
	            encodedData[encodedIndex++] = lookUpBase64Alphabet[ l<<2 ];
	            encodedData[encodedIndex++] = PAD;
	        }

	        return new String(encodedData);
	    }
	}
}
