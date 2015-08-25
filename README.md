# Maxmind minFraud Java API

## Installation ##

We recommend installing this package with [Maven](http://maven.apache.org/).
To do this, add the dependency to your pom.xml:

```xml
    <dependency>
        <groupId>com.maxmind.ws</groupId>
        <artifactId>minfraud-api</artifactId>
        <version>1.70</version>
    </dependency>
```

If you install by hand, you will need [Apache
HttpComponents](http://hc.apache.org/).

## Example programs

* sample/Example.java - how to use the API with minFraud service.

## API Documentation for minFraud service

```java
ccfs = new CreditCardFraudDetection(boolean s);
```

This creates a new CreditCardFraudDetection object and set isSecure to s if
isSecure is false then it uses regular HTTP. if isSecure is true then it uses
secure HTTPS.

```java
ccfb = new CreditCardFraudDetection.Builder()
```

Additionally you can use the Builder object to create a `CreditCardFraudDetection`
instance that either uses
the [JVM settings for proxies](https://docs.oracle.com/javase/8/docs/technotes/guides/net/proxies.html)
(`useSystemProxies()`) or a custom proxy (specified by hostname and port, e.g. `useProxy("host", 3128)`).

Here is a full example:

```java
ccfs =  new CreditCardFraudDetection.Builder().
                useProxy("proxy-host", 6568).
                build();
```


```java
ccfs.input(HashMap h);
```

Takes a Hashmap and uses it as input for the server See
http://dev.maxmind.com/minfraud for details on input fields.

```java
ccfs.query();
```

Queries the server with the fields passed to the input method and stores the
output.

```java
HashMap h = ccfs.output();
```

Returns the HashMap containing the output from the server. See
http://dev.maxmind.com/minfraud for details on output fields.

## Copyright and License

Copyright (c) 2007, MaxMind LLC

All rights reserved.  This package is licensed under the GNU Lesser General
Public License version 2.1 or later.  For details see the LICENSE file.
