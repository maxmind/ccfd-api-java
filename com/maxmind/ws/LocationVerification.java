/**
 * LocationVerification.java
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

public class LocationVerification extends HTTPBase {
  static String[] allowedfields = {"i","city",
  "region", "postal", "country", "license_key"};
  public LocationVerification() {
    url = "app/locvr";
    check_field = "distance";
    allowed_fields = new HashMap();
    for (int i = 0; i < allowedfields.length; i++) {
      allowed_fields.put(allowedfields[i], new Integer(1));
    }
    setIsSecure(true);
  }
  public LocationVerification(boolean s) {
    url = "app/locvr";
    check_field = "distance";
    allowed_fields = new HashMap();
    for (int i = 0; i < allowedfields.length; i++) {
      allowed_fields.put(allowedfields[i],new Integer(1));
    }
    setIsSecure(s);
  }
}
