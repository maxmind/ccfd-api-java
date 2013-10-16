import java.util.HashMap;

import com.maxmind.ws.CreditCardFraudDetection;

class Example {
    public static void main(String args[]) {
        CreditCardFraudDetection ccfs;
        HashMap<String, String> h = new HashMap<String, String>();
        final boolean isSecure = true;
        ccfs = new CreditCardFraudDetection(isSecure);

        // uncomment to turn debugging on
        ccfs.debug = true;

        // Set timeout to ten seconds
        ccfs.setTimeout(10);

        // Set inputs and store them in a hash
        // See http://www.maxmind.com/app/ccv for more details on the input
        // fields

        // Enter your license key here
        h.put("license_key", "YOUR_LICENSE_KEY_HERE");

        // Required Fields
        h.put("i", "24.24.24.24");
        h.put("city", "New York");
        h.put("region", "NY");
        h.put("postal", "11434");
        h.put("country", "US");

        // Recommended Fields
        h.put("domain", "yahoo.com");
        h.put("bin", "549099");

        // Optional fields
        h.put("custPhone", "212-242");
        h.put("binName", "MBNA America Bank");
        h.put("binPhone", "800-421-2110");
        h.put("requested_type", "premium");

        // CreditCardFraudDetection.java will take MD5 hash of e-mail address
        // passed to emailMD5 if it detects '@' in the string
        h.put("emailMD5", "Adeeb@Hackstyle.com");

        // CreditCardFraudDetection.java will take the MD5 hash of the
        // username/password
        // if the length of the string is not 32
        h.put("usernameMD5", "test_carder_username");
        h.put("passwordMD5", "test_carder_password");

        h.put("shipAddr", "145-50 157TH STREET");
        h.put("shipCity", "Jamaica");
        h.put("shipRegion", "NY");
        h.put("shipPostal", "11434");
        h.put("shipCountry", "US");
        h.put("txnID", "1234");
        h.put("sessionID", "abcd9876");

        h.put("user_agent", "Mozilla/4.0");
        h.put("accept_language", "de-de");

        ccfs.input(h);
        ccfs.query();
        h = ccfs.output();
        for (final String string : h.keySet()) {
            final String key = string;
            final String value = h.get(key);
            System.out.println(key + " = " + value);
        }
    }
}
