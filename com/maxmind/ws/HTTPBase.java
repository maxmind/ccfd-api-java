/**
 * HTTPBase.java
 *
 * Copyright (C) 2005 MaxMind LLC.  All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.maxmind.ws;

import java.util.regex.*;
import java.util.*;
import java.net.*;
import java.io.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;


//import HttpTimeoutHandler;

public class HTTPBase{
    int numservers = 3;
    static String[] server = {"minfraud.maxmind.com", "minfraud-us-east.maxmind.com", "minfraud-us-west.maxmind.com"};
    String url;
    public HashMap queries;
    public HashMap allowed_fields;
    public HashMap outputstr;
    public boolean isSecure = true;
    public float timeout = 10; // default timeout is 10 seconds
    public boolean debug = false;
    public String check_field = "countryMatch";
    public boolean useDNS = false;
    public long wsIpaddrRefreshTimeout = 18000;
    private static Pattern ippattern =  Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3};)*\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    public String wsIpaddrCacheFile = "/tmp/maxmind.ws.cache";
    HTTPBase() {
        queries = new HashMap();
        allowed_fields = new HashMap();
        outputstr = new HashMap();
        isSecure = true;
    }

    HTTPBase(boolean s) {
        queries = new HashMap();
        allowed_fields = new HashMap();
        outputstr = new HashMap();
        isSecure = s;
    }

    public boolean getIsSecure() {
	return isSecure;
    }
    public void setIsSecure(boolean isSecure) {
	this.isSecure = isSecure;
    }
    public float getTimeout() {
	return timeout;
    }
    public void setTimeout(float t) {
	timeout = t;
    }
    // queries the servers
    public boolean query() {
	if (debug) {
	    System.out.println("number of servers = " + numservers); 
	}
	
	if (this.useDNS == false) {
            // query every server using its ip address
            // if there was success reading the ip addresses
            // from the web or the cache file
	    String ipstr = readIpAddressFromCache();
	    if (debug) {
	        System.out.println("using ip addresses, ipstr is " + ipstr);
	    }
	    StringTokenizer st = new StringTokenizer(ipstr, ";");
	    while (st.hasMoreTokens()) {
		String ipstr2 = st.nextToken();
	        boolean result = this.querySingleServer(ipstr2);
	        if (debug) {
		    System.out.println("ip address " + ipstr2 + ", result = " + result); 
	        }
	        if (result == true) {
		    return result;
	        }
	    }
	}

        // query every server using its domain name
    	for (int i = 0; i < numservers; i++) {
	    boolean result = this.querySingleServer(server[i]);
	    if (debug) {
		System.out.println("queried server = " + server[i] + ", result = " + result); 
	    }
	    if (result == true) {
		return result;
	    }
	}
	return false;
    }
    
    // takes a input hash and stores it in the hash named queries
    public void input(HashMap h) {
	queries = new HashMap();
	for (Iterator i = h.keySet().iterator(); i.hasNext(); ) {
	    String key = (String) i.next();
	    // check if key is a allowed field
	    if (allowed_fields.containsKey(key)) {
		String value = (String) h.get(key);
		queries.put(key, filter_field(key,value));
	    } else {
		System.out.println("key " + key + " is not a allowed field ");
	    }
	}
    }

    // sub-class should override this if it needs to filter inputs
    protected String filter_field(String key, String value) {
      return value;
    }

    // returns the output from the server
    public HashMap output() {
	return outputstr;
    }

    // write the IP addresses and the timestamp to
    // the cache file
    void writeIpAddressToCache(String filename,String ipstr) {
        try {
	    BufferedWriter f = new BufferedWriter(new FileWriter(filename,false));
	    f.write(ipstr);
	    long datetime = System.currentTimeMillis() / 1000;
	    f.write(Long.toString(datetime));
            f.newLine();
	    f.close();
	    if (debug) {
	        System.out.println("writing ip address to cache\n");
	        System.out.println("ip str: " + ipstr);
	        System.out.println("date time: " + datetime);
	    }
	} catch (Exception e) {
	    if (debug) {
	        System.out.println(e.getMessage());
	        e.printStackTrace();
	    }
        }
       
    }

    String readIpAddressFromCache(){
        String ipstr = null;
        long datetime = 0;
        long time = 0;
        // if the cache file exists (it throws a exception if it does not exist) then
        // read the ip addresses and the time
        // they were cache
	try {
	    BufferedReader f = new BufferedReader(new FileReader(this.wsIpaddrCacheFile));
            ipstr = f.readLine();
            datetime = Long.valueOf(f.readLine()).longValue();
            time = System.currentTimeMillis() / 1000;
            f.close();
	} catch (Exception e) {
	    if (debug) {
	        System.out.println(e.getMessage());
	        e.printStackTrace();
	    }
	}
	// if the ip addresses expired or don't exist then
	// get them from the web and write 
	// them to the cache file
	if (((time - datetime) > this.wsIpaddrRefreshTimeout) | (ipstr == null)) {
	    String tryIpstr = readIpAddressFromWeb();
	    if (tryIpstr.length() > 0) {
	        ipstr = tryIpstr;
	    } else {
		if (debug) {
		    System.out.println("Warning, unable to get ws_ipaddr from www.maxmind.com\n");
		}
	    }
	    // we write to cache whether or not we were able to get tryIpStr, since
	    // in case DNS goes down, we don't want to check app/ws_ipaddr over and over
	    writeIpAddressToCache(this.wsIpaddrCacheFile,ipstr);
        }
        if (debug) {
	    System.out.println("reading ip address from cache\n");
	    System.out.println("ip str: " + ipstr);
	    System.out.println("date time: " + datetime);
	    System.out.println("time: " + time);
	}

	// return the ip addresses
        ipstr = ipstr.trim();
	Matcher m = ippattern.matcher(ipstr);
        boolean b = m.matches();

	if (b == true) {
	    return ipstr;
        }
	return null;
    }

    // get the ip addresses from app/ws_ipaddr
    String readIpAddressFromWeb() {
	String url2 = "http://www.maxmind.com/app/ws_ipaddr";
	try {
	    org.apache.commons.httpclient.HttpClient client = new HttpClient();
	    client.setConnectionTimeout((int)timeout*1000);
	    client.setTimeout((int)timeout*1000);

	    // connect to the server
	    org.apache.commons.httpclient.HttpMethod method = new GetMethod(url2);
	    int r = client.executeMethod(method);
	    String content = method.getResponseBodyAsString();

	    if (method.getStatusCode() == 200) {
		if (debug) {
		    System.out.println("content = " + content);
		}
                return content;
	    }
	} catch (Exception e) {
	    if (debug) {
	        System.out.println(e.getMessage());
	        e.printStackTrace();
	    }
	}
	return null;
    }

    // queries a single server
    boolean querySingleServer(String server) {
	String scheme, url2;
	
	NameValuePair[] query_data = new NameValuePair[queries.size()+1]; 
	// check if we using the Secure HTTPS proctol
	if (isSecure == true) {
	    scheme = "https://";    // Secure HTTPS proctol
	} else {
	    scheme = "http://";     // Regular HTTP proctol
	}
	
	// build a query string from the hash called queries
	int n = 0;
	for (Iterator i = queries.keySet().iterator(); i.hasNext(); ) {
	    // for each element in the hash called queries
	    // append the key and the value of the element to the query string
	    String key = (String)i.next();
	    String value = (String)queries.get(key);
	    if (debug) {
		System.out.println("query key " + key + " = " + value); 
	    }
	    query_data[n] = new NameValuePair(key,value);
	    n++;
	}
	query_data[n] = new NameValuePair("clientAPI","Java/1.51");
	// scheme already has the name of the proctol
	// append the domain name of the server, url of the web service
	// and the query string to the string named url2
	url2 = scheme + server + "/" + url;
	if (debug) {
	    System.out.println("url2 = " + url2);
	}
	try {
	    org.apache.commons.httpclient.HttpClient client = new HttpClient();
	    client.setConnectionTimeout((int)timeout*1000);
	    client.setTimeout((int)timeout*1000);
	    
	    // connect the server
	    org.apache.commons.httpclient.methods.PostMethod method = new PostMethod(url2);
	    method.setRequestBody(query_data);
	    int r = client.executeMethod(method);
            BufferedInputStream in = new BufferedInputStream(method.getResponseBodyAsStream());
	    StringBuffer temp = new StringBuffer();
	    int i = in.read();
	    while (i > -1){
	      temp.append((char) i);
	      i = in.read();
	    }
	    String content = temp.toString();
	    if (method.getStatusCode() == 200) {
		if (debug) {
		    System.out.println("content = " + content);
		}

		// get the keys and values from
		// the string content and store them
		// the hash named outputstr

		// split content into pairs containing both
		// the key and the value
		StringTokenizer st = new StringTokenizer(content, ";");

		// for each pair store key and value into the
		// hash named outputstr
		while (st.hasMoreTokens()) {
		    String keyvaluepair = st.nextToken();

		    // split the pair into a key and a value
		    StringTokenizer st2 = new StringTokenizer(keyvaluepair,"=");
		    String key;
		    String value;
		    key = st2.nextToken();
		    if (st2.hasMoreTokens()) {
			value = st2.nextToken();
		    } else {
			value = "";
		    }
		    // store the key and the value into the
		    // hash named outputstr
		    outputstr.put(key,value);
		    if (debug) {
			System.out.println("key = " + key + ", value = " + value);
		    }
		}
		if (outputstr.containsKey(check_field) == false) {
		    // if the output does not have the field it is checking for then return false
		    return false;
		}
		method.releaseConnection();
		return true;
	    } else {
		method.releaseConnection();
		return false;
	    }
	} catch(java.io.IOException e) {
	    if (e instanceof InterruptedIOException) {
		System.out.println("web service timeout");
	    }
	    System.out.println("error = " + e.getMessage());
	    e.printStackTrace();     
	}
	return false;
    }
}
