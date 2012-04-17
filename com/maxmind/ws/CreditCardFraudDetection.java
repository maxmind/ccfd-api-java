/**
 * CreditCardFraudDetection.java
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

import java.util.*;
import java.util.regex.*;
import java.security.*;

public class CreditCardFraudDetection extends HTTPBase {
    static String[] allowedfields = {"i","domain", "city", "region", "postal", "country", "bin", "binName",
				"binPhone", "custPhone", "license_key", "requested_type", "forwardedIP", "emailMD5",
				"shipAddr", "shipCity", "shipRegion", "shipPostal", "shipCountry", "txnID", "sessionID",
				"usernameMD5", "passwordMD5", "user_agent", "accept_language"};
    char[] hexchar = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
  public CreditCardFraudDetection() {
    url = "app/ccv2r";
    check_field = "score";
    allowed_fields = new HashMap();
    for (int i = 0; i < allowedfields.length; i++) {
      allowed_fields.put(allowedfields[i], new Integer(1));
    }
    setIsSecure(true);
  }
  public CreditCardFraudDetection(boolean s) {
    url = "app/ccv2r";
    check_field = "countryMatch";
    allowed_fields = new HashMap();
    for (int i = 0; i < allowedfields.length; i++) {
      allowed_fields.put(allowedfields[i],new Integer(1));
    }
    setIsSecure(s);
  }

  protected String filter_field(String key,String value) {
    if (
	(key.equals("emailMD5") && value.indexOf('@') != -1)
		||
	((key.equals("usernameMD5") || key.equals("passwordMD5")) && value.length() != 32)
      ) {
      try {
	MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(value.toLowerCase().getBytes());
        byte[] b = md.digest();
        StringBuffer md5str = new StringBuffer();
	for (int i = 0;i < b.length;i++) {
	  md5str.append(hexchar[(b[i] & 0xff) >> 4]);
	  md5str.append(hexchar[b[i] & 15]);
	}
        return md5str.toString();
      } catch (NoSuchAlgorithmException e) {
	System.out.println("algorithm not support\n");
      }
    }
    return value;
  }
}
