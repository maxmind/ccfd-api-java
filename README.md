# Maxmind minFraud Java API

## Example programs

* sample/Example.java - how to use the API with minFraud service.
* sample/Telv_Example.java - using the API with the Telephone Verification
  service.

## API Documentation for minFraud service

```java
ccfs = new CreditCardFraudDetection(boolean s);
```

This creates a new CreditCardFraudDetection object and set isSecure to s if
isSecure is false then it uses regular HTTP. if isSecure is true then it uses
secure HTTPS.

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

## Notes

The Java API requires the Commons HttpClient package and Commons Logging
package. For more details, see http://jakarta.apache.org/commons/httpclient/

## Copyright and License

Copyright (c) 2007, MaxMind LLC

All rights reserved.  This package is licensed under the GNU Lesser General
Public License version 2.1 or later.  For details see the LICENSE file.
