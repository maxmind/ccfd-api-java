import java.util.*;
import com.maxmind.ws.*;

class Locv_Example {
  public static void main(String args[]) {
    LocationVerification locv;
    HashMap h = new HashMap();
    locv = new LocationVerification(false);

//  uncomment to turn debugging on
//  locv.debug = true;

//  Set timeout to ten seconds
    locv.setTimeout(10);

//  Enter your license key here
//  h.put("license_key","YOUR_LICENSE_KEY_HERE");

//  Required Fields
    h.put("i","24.24.24.24");
    h.put("city","New York");
    h.put("region","NY");
    h.put("postal","10011");
    h.put("country","US");

    locv.input(h);
    locv.query();
    h = locv.output();
    for (Iterator i = h.keySet().iterator();i.hasNext();){
      String key = (String)i.next();
      String value = (String)h.get(key);
      System.out.println(key + " = " + value + " \n");
    }
  }
}
