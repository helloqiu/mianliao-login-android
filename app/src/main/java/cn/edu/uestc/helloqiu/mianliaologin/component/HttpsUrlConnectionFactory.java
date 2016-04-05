package cn.edu.uestc.helloqiu.mianliaologin.component;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by helloqiu on 16/4/5.
 */
public class HttpsUrlConnectionFactory {
    private URL url;
    private SSLContext sslContext;

    HttpsUrlConnectionFactory(URL url, SSLContext sslContext) {
        this.url = url;
        this.sslContext = sslContext;
    }

    public HttpsURLConnection getNewConnection(String method) {
        try {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            httpsURLConnection.setRequestMethod(method);
            httpsURLConnection.setRequestProperty("Connection", "keep-alive");
            httpsURLConnection.setRequestProperty("Cache-Control", "max-age=0");
            httpsURLConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            httpsURLConnection.setRequestProperty("Origin", "https://wifi.52mianliao.com");
            httpsURLConnection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            httpsURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
            httpsURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpsURLConnection.setRequestProperty("Referer", "https://wifi.52mianliao.com/");
            httpsURLConnection.setRequestProperty("Accept-Encoding", "identity");
            httpsURLConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
            return httpsURLConnection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
