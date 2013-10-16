/**
 * TelephoneVerification.java
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

import java.util.HashMap;

public class TelephoneVerification extends HTTPBase {
    static String[] allowedfields = { "l", "phone", "verify_code" };

    public TelephoneVerification() {
        url = "app/telephone_http";
        check_field = "refid";
        allowed_fields = new HashMap();
        for (final String allowedfield : allowedfields) {
            allowed_fields.put(allowedfield, new Integer(1));
        }
        setIsSecure(true);
    }

    public TelephoneVerification(boolean s) {
        url = "app/telephone_http";
        check_field = "refid";
        allowed_fields = new HashMap();
        for (final String allowedfield : allowedfields) {
            allowed_fields.put(allowedfield, new Integer(1));
        }
        setIsSecure(s);
    }
}
