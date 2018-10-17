# minFraud Java API Change Log

## 1.80 (2018-10-17)

* Support for using a proxy server. PR by Markus Perndorfer. GitHub #2.
* Upgrade Apache HttpClient from 4.3 to 4.5.6 as 4.3 has several security
  vulnerabilities.

## 1.70 (2015-07-21)

* Support for the Telephone Verification Service was removed as this service
  is no longer offered.
* Minor code cleanup.

## 1.60 (2013-10-23)

* Updated code to use generics (Java 1.5+ required).
* Updated code to use to Apache HttpComponents 4.x.
* Cleaned up the source tree and code.
* First Maven release.

## 1.53 (2013-08-27)

* Remove custom DNS cache ( Boris Zentner )

##1.52 (2013-08-09)

* Check for the countryMatch field (not score) to see if a request returned
  valid data. The score field is deprecated. (Boris Zentner)
* Allow all input fields provided by minFraud 1.4 (Boris Zentner)

## 1.51 (2012-04-17)

* Replaced the minfraud server list with api-us-east.maximind.com and api-us-
  west.maxmind.com ( Boris Zentner )

## 1.50 (2012-03-13)

* Check countryMatch instead of score for successful results. Score is only
  avail for minfraud_version < 1.3 ( Boris Zentner )

## 1.49 (2009-02-19)

* Add minfraud3.maxmind.com to the serverlist

## 1.48 (2008-10-03)

* Add new fields user_agent and accept_language for CCFD ( Boris Zentner )

## 1.46 (2007-10-04)

* Replaced www.maxmind.com and www2.maxmind.com with minfraud1.maxmind.com and
  minfraud2.maxmind.com

## 1.45 (2007-07-23)

* Changed license from GPL to LGPL, to allow commercial programs to include
  minFraud API

## 1.44 (2006-10-09)

* Fixed problem with encoding input fields, since now use POST instead of GET

## 1.43 (2006-10-03)

* Added support for new input fields, usernameMD5 and passwordMD5

## 1.4 (2005-08-08)

* Added support for Telephone Verification
* Use POST method instead of GET method, fixes bug where query string was
  truncated
* Added support for bypassing DNS using IP addresses
* Added shipCity shipRegion shipPostal shipCountry to list of input fields

## 1.3 (2005-02-09)

* Added requested_type, forwardedIP, emailMD5, shipAddr, txnID, sessionID to
  list of input fields
* Added LocationVerification.java

## 1.2 (2004-07-02)

* Added binName, binPhone, custPhone to list of input fields

## 1.1 (2004-06-14)

* use org.apache.commons.httpclient instead of Java's built in HTTP client
  since Apache HTTP client has better support for timeouts
* Replaced h1 and h2 servers with www and www2 (all ending with maxmind.com)
* Added debug and timeout options
* Failover if score field not set

## 1.0 (2004-05-05)

* original version
