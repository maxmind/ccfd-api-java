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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class CreditCardFraudDetection extends HTTPBase {
    static String[] allowedfields = { "i", "domain", "city", "region",
            "postal", "country", "bin", "binName", "binPhone", "custPhone",
            "license_key", "requested_type", "forwardedIP", "emailMD5",
            "shipAddr", "shipCity", "shipRegion", "shipPostal", "shipCountry",
            "txnID", "sessionID", "usernameMD5", "passwordMD5", "user_agent",
            "accept_language", "avs_result", "cvv_result", "order_amount",
            "order_currency", "shopID", "txn_type" };
    char[] hexchar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
            'b', 'c', 'd', 'e', 'f' };

    public CreditCardFraudDetection() {
        url = "app/ccv2r";
        check_field = "countryMatch";
        allowed_fields = new HashMap<String, Integer>();
        for (final String allowedfield : allowedfields) {
            allowed_fields.put(allowedfield, new Integer(1));
        }
        setIsSecure(true);
    }

    public CreditCardFraudDetection(boolean s) {
        url = "app/ccv2r";
        check_field = "countryMatch";
        allowed_fields = new HashMap<String, Integer>();
        for (final String allowedfield : allowedfields) {
            allowed_fields.put(allowedfield, new Integer(1));
        }
        setIsSecure(s);
    }

    @Override
    protected String filter_field(String key, String value) {
        if (("emailMD5".equals(key) && value.indexOf('@') != -1)
                || (("usernameMD5".equals(key) || "passwordMD5".equals(key)) && value
                        .length() != 32)) {
            try {
                final MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(value.toLowerCase().getBytes());
                final byte[] b = md.digest();
                final StringBuffer md5str = new StringBuffer();
                for (final byte element : b) {
                    md5str.append(hexchar[(element & 0xff) >> 4]);
                    md5str.append(hexchar[element & 15]);
                }
                return md5str.toString();
            } catch (final NoSuchAlgorithmException e) {
                System.out.println("algorithm not support\n");
            }
        }
        return value;
    }
}
