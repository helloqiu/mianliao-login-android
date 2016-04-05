package cn.edu.uestc.helloqiu.mianliaologin.component;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import cn.edu.uestc.helloqiu.mianliaologin.R;

/**
 * Created by helloqiu on 16/4/5.
 */
public class LoginHttpsWorker extends HttpsWorker {
    public final String SUCCESS = "success";
    public final String SERVERERROR = "server error";
    public final String FAIL = "fail";

    public LoginHttpsWorker(Context context) {
        super(context);
    }

    private class ReturnValue {
        String returnValue;

        public ReturnValue() {
            this.returnValue = null;
        }

        public void setVaule(String value) {
            this.returnValue = value;
        }

        public String getValue() {
            return this.returnValue;
        }
    }

    public String login(final String username, final String password) {
        final ReturnValue returnValue = new ReturnValue();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Load CAs from an InputStream
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    InputStream caInput = new BufferedInputStream(
                            context.getResources().openRawResource(R.raw.server_self_crt));
                    Certificate ca;
                    try {
                        ca = cf.generateCertificate(caInput);
                        Log.e("HttpWorker", "ca=" + ((X509Certificate) ca).getSubjectDN());
                    } finally {
                        caInput.close();
                    }

                    // Create a KeyStore containing our trusted CAs
                    String keyStoreType = KeyStore.getDefaultType();
                    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                    keyStore.load(null, null);
                    keyStore.setCertificateEntry("ca", ca);

                    // Create a TrustManager that trusts the CAs in our KeyStore
                    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                    tmf.init(keyStore);

                    // Create an SSLContext that uses our TrustManager
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, tmf.getTrustManagers(), null);

                    // Connect to server
                    final String address = "https://wifi.52mianliao.com";
                    final String auth = "username=" + username + "&password=" + password + "&action=login";
                    URL url = new URL(address);
                    HttpsUrlConnectionFactory httpsUrlConnectionFactory = new HttpsUrlConnectionFactory(url, sslContext);
                    HttpsURLConnection get = httpsUrlConnectionFactory.getNewConnection("GET");
                    get.connect();
                    if (get.getResponseCode() != 200) {
                        returnValue.setVaule(SERVERERROR);
                        return;
                    }
                    // Get the cookie
                    final String key = "Set-Cookie";
                    Map<String, List<String>> map = get.getHeaderFields();
                    List<String> list = map.get(key);
                    StringBuilder builder = new StringBuilder();
                    for (String str : list) {
                        builder.append(str).toString();
                    }
                    final String cookie = builder.toString();
                    get.disconnect();
                    // Send the auth info to server
                    HttpsURLConnection firstPost = httpsUrlConnectionFactory.getNewConnection("POST");
                    firstPost.setRequestProperty("Cookie", cookie);
                    firstPost.connect();
                    DataOutputStream dataOutputStream = new DataOutputStream(firstPost.getOutputStream());
                    dataOutputStream.write(auth.getBytes());
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    BufferedReader bufferedReaderFirst = new BufferedReader(new InputStreamReader(firstPost.getInputStream()));
                    String resultFirst = "";
                    String readLineFirst = null;
                    while ((readLineFirst = bufferedReaderFirst.readLine()) != null) {
                        resultFirst += readLineFirst;
                    }
                    Log.e("DEBUG",resultFirst);
                    firstPost.disconnect();
                    // Send client info to server
                    HttpsURLConnection secondPost = httpsUrlConnectionFactory.getNewConnection("POST");
                    secondPost.setRequestProperty("Cookie", cookie);
                    final String clientInfo = "ua=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36&sw=1280&sh=720&ww=1280&wh=720";
                    secondPost.connect();
                    DataOutputStream dataOutputStreamAgain = new DataOutputStream(secondPost.getOutputStream());
                    dataOutputStreamAgain.write(clientInfo.getBytes());
                    dataOutputStreamAgain.flush();
                    dataOutputStreamAgain.close();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(secondPost.getInputStream()));
                    String result = "";
                    String readLine = null;
                    while ((readLine = bufferedReader.readLine()) != null) {
                        result += readLine;
                    }
                    secondPost.disconnect();
                    Log.e("Debug",result);
                    if (result.contains("登陆用户")) {
                        returnValue.setVaule(SUCCESS);
                        return;
                    } else {
                        returnValue.setVaule(FAIL);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (true) {
            if (returnValue.getValue() == null) {
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                return returnValue.getValue();
            }
        }
    }
}
