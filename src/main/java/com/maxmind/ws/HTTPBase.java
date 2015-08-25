/**
 * HTTPBase.java
 * 
 * Copyright (C) 2005 MaxMind LLC. All Rights Reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package com.maxmind.ws;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class HTTPBase {
    private final String clientApi = "Java/1.60";

    int numservers = 3;
    static String[] server = { "minfraud.maxmind.com",
            "minfraud-us-east.maxmind.com", "minfraud-us-west.maxmind.com" };
    String url;
    public Map<String, String> queries;
    public Map<String, Integer> allowed_fields;
    public HashMap<String, String> outputstr;
    public boolean isSecure = true;
    public float timeout = 10; // default timeout is 10 seconds
    public boolean debug = false;
    public String check_field = "countryMatch";

    // We keep the variables useDNS, wsIpaddrRefreshTimeout and
    // wsIpaddrCacheFile
    // for backward compatibility, __but we do not use them__
    public boolean useDNS = true;
    public long wsIpaddrRefreshTimeout = 18000;
    public String wsIpaddrCacheFile = "/tmp/maxmind.ws.cache";

    boolean useSystemProxies = false;

    String proxyHost = null;
    int proxyPort = -1;

    HTTPBase() {
        queries = new HashMap<String, String>();
        allowed_fields = new HashMap<String, Integer>();
        outputstr = new HashMap<String, String>();
        isSecure = true;
    }

    HTTPBase(boolean s) {
        queries = new HashMap<String, String>();
        allowed_fields = new HashMap<String, Integer>();
        outputstr = new HashMap<String, String>();
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

        // query every server using its domain name
        for (int i = 0; i < numservers; i++) {
            final boolean result = this.querySingleServer(server[i]);
            if (debug) {
                System.out.println("queried server = " + server[i]
                        + ", result = " + result);
            }
            if (result) {
                return result;
            }
        }
        return false;
    }

    // takes a input hash and stores it in the hash named queries
    public void input(Map<?, ?> h) {
        queries = new HashMap<String, String>();
        for (final Object name : h.keySet()) {
            final String key = (String) name;
            // check if key is a allowed field
            if (allowed_fields.containsKey(key)) {
                final String value = (String) h.get(key);
                queries.put(key, filter_field(key, value));
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
    public HashMap<String, String> output() {
        return outputstr;
    }

    // queries a single server
    boolean querySingleServer(String hostname) {
        String scheme, url2;

        // check if we using the Secure HTTPS protocol
        scheme = isSecure ? "https://" : "http://";

        final ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
        for (final Map.Entry<String, String> entry : queries.entrySet()) {
            parameters.add(new BasicNameValuePair(entry.getKey(), entry
                    .getValue()));
        }
        parameters.add(new BasicNameValuePair("clientAPI", clientApi));

        // scheme already has the name of the protocol
        // append the domain name of the server, url of the web service
        // and the query string to the string named url2
        url2 = scheme + hostname + "/" + url;
        if (debug) {
            System.out.println("url2 = " + url2);
        }
        CloseableHttpClient client = null;
        try {
            RequestConfig timeoutConfig = RequestConfig.custom().
                    setConnectionRequestTimeout((int) timeout * 1000).
                    setSocketTimeout((int) timeout * 1000).build();

            HttpRoutePlanner proxyPlaner = null;
            if (useSystemProxies) {
                proxyPlaner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
            }
            if (proxyHost != null && proxyPort > -1) {
                proxyPlaner = new DefaultProxyRoutePlanner(new HttpHost(proxyHost, proxyPort));
            }
            client = HttpClients.custom().
                    setDefaultRequestConfig(timeoutConfig).
                    setRoutePlanner(proxyPlaner).build();

            // connect the server
            final HttpPost method = new HttpPost(url2);
            method.setEntity(new UrlEncodedFormEntity(parameters));

            final HttpResponse response = client.execute(method);

            final String content = EntityUtils.toString(response.getEntity());
            if (response.getStatusLine().getStatusCode() == 200) {

                if (debug) {
                    System.out.println("content = " + content);
                }

                // get the keys and values from
                // the string content and store them
                // the hash named outputstr

                // split content into pairs containing both
                // the key and the value
                final StringTokenizer st = new StringTokenizer(content, ";");

                // for each pair store key and value into the
                // hash named outputstr
                while (st.hasMoreTokens()) {
                    final String keyvaluepair = st.nextToken();

                    // split the pair into a key and a value
                    final StringTokenizer st2 = new StringTokenizer(
                            keyvaluepair, "=");
                    String key;
                    String value;
                    key = st2.nextToken();
                    value = st2.hasMoreTokens() ? st2.nextToken() : "";
                    // store the key and the value into the
                    // hash named outputstr
                    outputstr.put(key, value);
                    if (debug) {
                        System.out.println("key = " + key + ", value = "
                                + value);
                    }
                }
                if (!outputstr.containsKey(check_field)) {
                    // if the output does not have the field it is checking for
                    // then return false
                    return false;
                }
                method.releaseConnection();
                return true;
            }
            method.releaseConnection();
            return false;
        } catch (final java.io.IOException e) {
            if (e instanceof InterruptedIOException) {
                System.out.println("web service timeout");
            }
            System.out.println("error = " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                //ignore
            }
        }
        return false;
    }
}
