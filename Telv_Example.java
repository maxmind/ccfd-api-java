import java.util.*;
import com.maxmind.ws.*;

class Telv_Example {
  public static void main(String args[]) {
    TelephoneVerification tv;
    HashMap h = new HashMap();
    tv = new TelephoneVerification(true);

//  uncomment to turn debugging on
//  ccfs.debug = true;

//  Set timeout to thirty seconds
    tv.setTimeout(30);

//  Enter your license key here
    h.put("l","YOUR_LICENSE_KEY_HERE");

//  Enter your phone number here
    h.put("phone","PHONE_NUMBER_HERE");

// the verify code goes here (optional)
    //h.put("verify_code","2536");

    tv.input(h);
    tv.query();
    h = tv.output();
    for (Iterator i = h.keySet().iterator();i.hasNext();){
      String key = (String)i.next();
      String value = (String)h.get(key);
      System.out.println(key + " = " + value + " \n");
    }
  }
}
