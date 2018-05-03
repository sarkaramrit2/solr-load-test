/*package org.apache.solr;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Test {

    private static final String SIGNATURE = "&s=";

    public static void main(String[] args) throws Exception {

        String tokenStr_1 = "u=id094579&p=id094579@BGC.NET&t=kerberos&e=1512673792463" +
        "&s=OGl5By8LdkJIXC6637QBheCjaYE=";

        String tokenStr_2 = "u=solr&p=solr/el4579.bc@BGC.NET&t=kerberos&e=1512671528284" +
                "&s=lK59bQUUv2Fv+zgrPVvJf6Ju8qk=";

        verifyAndExtract(tokenStr_1);
        //verifyAndExtract(tokenStr_2);

    }

    public static String verifyAndExtract(String signedStr) throws Exception {
        int index = signedStr.lastIndexOf(SIGNATURE);
        if (index == -1) {
            throw new Exception("Invalid signed text: " + signedStr);
        }
        String originalSignature = signedStr.substring(index + SIGNATURE.length());
        String rawValue = signedStr.substring(0, index);
        checkSignatures(rawValue, originalSignature);
        return rawValue;
    }

    protected static void checkSignatures(String rawValue, String originalSignature)
            throws Exception {
        boolean isValid = false;
        byte[][] secrets = null;//secretProvider.getAllSecrets();
        for (int i = 0; i < secrets.length; i++) {
            byte[] secret = secrets[i];
            if (secret != null) {
                String currentSignature = computeSignature(secret, rawValue);
                if (originalSignature.equals(currentSignature)) {
                    isValid = true;
                    break;
                }
            }
        }
        if (!isValid) {
            throw new Exception("Invalid signature");
        }
    }

    protected static String computeSignature(byte[] secret, String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(str.getBytes(Charset.forName("UTF-8")));
            md.update(secret);
            byte[] digest = md.digest();
            return new Base64(0).encodeToString(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("It should not happen, " + ex.getMessage(), ex);
        }
    }

    public static void createAuthCookie(HttpServletResponse resp, String token,
                                        String domain, String path, long expires,
                                        boolean isSecure) {
        StringBuilder sb = new StringBuilder(AuthenticatedURL.AUTH_COOKIE)
                .append("=");
        if (token != null && token.length() > 0) {
            sb.append("\"").append(token).append("\"");
        }

        if (path != null) {
            sb.append("; Path=").append(path);
        }

        if (domain != null) {
            sb.append("; Domain=").append(domain);
        }

        if (expires >= 0) {
            Date date = new Date(expires);
            SimpleDateFormat df = new SimpleDateFormat("EEE, " +
                    "dd-MMM-yyyy HH:mm:ss zzz");
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            sb.append("; Expires=").append(df.format(date));
        }

        if (isSecure) {
            sb.append("; Secure");
        }

        sb.append("; HttpOnly");
        resp.addHeader("Set-Cookie", sb.toString());
    }
}*/