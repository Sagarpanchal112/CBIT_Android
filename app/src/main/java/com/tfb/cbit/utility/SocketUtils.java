package com.tfb.cbit.utility;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.tfb.cbit.BuildConfig;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.GameViewActivity;
import com.tfb.cbit.activities.HomeActivity;
import com.tfb.cbit.activities.PrivateContestDetailsActivity;
import com.tfb.cbit.event.ContestLiveUpdate;
import com.tfb.cbit.event.GameAlertEvent;
import com.tfb.cbit.event.GameResultEvent;
import com.tfb.cbit.event.GameStartEvent;
import com.tfb.cbit.event.SocketConnectionEvent;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.event.UpdateHistoryEvent;
import com.tfb.cbit.event.UpdateMyContestEvent;
import com.tfb.cbit.event.UpdateSpecialContestEvent;
import com.tfb.cbit.event.UpdateUpcomingContestEvent;
import com.tfb.cbit.event.UpdateWallet;
import com.tfb.cbit.models.wallet_transfer.WalletTransferModel;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.DecimalFormat;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.OkHttpClient;

import static com.tfb.cbit.utility.Utils.SOCKET_URI;

public class SocketUtils {
    /*
     * Socket Connection Class
     * #TheFreeBird
     */
    //private static final String SOCKET_URI = "http://192.168.0.189:3500";

    //private static final String SOCKET_URI = "http://192.168.0.12:3500";
    // private static final String SOCKET_URI = "http://68.183.144.102:3500"; //Live API URL - commented
    //private static final String SOCKET_URI = "http://68.183.144.102:3600"; //Live API URL
    private static final String SOCKET_PATH = "/socket.io";
    //private static final String SOCKET_URI = "https://admin.cbitoriginal.com";
    //private static final String SOCKET_URI = "https://admin.cbitoriginal.com:443";


    private static final String[] TRANSPORTS = {
            "websocket"
    };
    private static Socket mSocket;
    private static Context context;
    public static final String EVENT_UNAUTHORIZED = "unauthorized";
    public static final String EVENT_LOGIN = "login";
    public static final String EVENT_CONTEST_START = "onContestStart";
    public static final String EVENT_CONTEST_END = "onContestEnd";
    public static final String EVENT_PAYMENT_UPDATE = "onPaymentUpdate";
    public static final String EVENT_CONTEST_UPDATE = "onContestUpdate";
    public static final String EVENT_CONTEST_DETAILS = "contestDetails";
    public static final String EVENT_UPDATE_GAME = "updateGame";
    public static final String EVENT_UPDATE_GAMEALL = "updateGameAll";
    public static final String EVENT_ANYUPDATE_GAMEALL = "AnytimeUpdateGameAll";
    public static final String EVENT_ANYUPDATE_GAME = "AnytimeUpdateGameAll";
    public static final String EVENT_HOST_NOTIFY = "hostNotify";
    public static final String EVENT_CONTEST_LIVE_UPDATE = "onContestLiveUpdate";

    public static final String EVENT_CONTEST_ALERT = "onContestAlert";
    public static final String EVENT_APP_VERSION = "VersionUpdate";
    public static final String EVENT_KYC_UPDATE = "onKycUpdate";

    public static final String SERVER_CERT2 = "MIIEADCCAuigAwIBAgIBADANBgkqhkiG9w0BAQUFADBjMQswCQYDVQQGEwJVUzEh\n" +
            "MB8GA1UEChMYVGhlIEdvIERhZGR5IEdyb3VwLCBJbmMuMTEwLwYDVQQLEyhHbyBE\n" +
            "YWRkeSBDbGFzcyAyIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MB4XDTA0MDYyOTE3\n" +
            "MDYyMFoXDTM0MDYyOTE3MDYyMFowYzELMAkGA1UEBhMCVVMxITAfBgNVBAoTGFRo\n" +
            "ZSBHbyBEYWRkeSBHcm91cCwgSW5jLjExMC8GA1UECxMoR28gRGFkZHkgQ2xhc3Mg\n" +
            "MiBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTCCASAwDQYJKoZIhvcNAQEBBQADggEN\n" +
            "ADCCAQgCggEBAN6d1+pXGEmhW+vXX0iG6r7d/+TvZxz0ZWizV3GgXne77ZtJ6XCA\n" +
            "PVYYYwhv2vLM0D9/AlQiVBDYsoHUwHU9S3/Hd8M+eKsaA7Ugay9qK7HFiH7Eux6w\n" +
            "wdhFJ2+qN1j3hybX2C32qRe3H3I2TqYXP2WYktsqbl2i/ojgC95/5Y0V4evLOtXi\n" +
            "EqITLdiOr18SPaAIBQi2XKVlOARFmR6jYGB0xUGlcmIbYsUfb18aQr4CUWWoriMY\n" +
            "avx4A6lNf4DD+qta/KFApMoZFv6yyO9ecw3ud72a9nmYvLEHZ6IVDd2gWMZEewo+\n" +
            "YihfukEHU1jPEX44dMX4/7VpkI+EdOqXG68CAQOjgcAwgb0wHQYDVR0OBBYEFNLE\n" +
            "sNKR1EwRcbNhyz2h/t2oatTjMIGNBgNVHSMEgYUwgYKAFNLEsNKR1EwRcbNhyz2h\n" +
            "/t2oatTjoWekZTBjMQswCQYDVQQGEwJVUzEhMB8GA1UEChMYVGhlIEdvIERhZGR5\n" +
            "IEdyb3VwLCBJbmMuMTEwLwYDVQQLEyhHbyBEYWRkeSBDbGFzcyAyIENlcnRpZmlj\n" +
            "YXRpb24gQXV0aG9yaXR5ggEAMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEFBQAD\n" +
            "ggEBADJL87LKPpH8EsahB4yOd6AzBhRckB4Y9wimPQoZ+YeAEW5p5JYXMP80kWNy\n" +
            "OO7MHAGjHZQopDH2esRU1/blMVgDoszOYtuURXO1v0XJJLXVggKtI3lpjbi2Tc7P\n" +
            "TMozI+gciKqdi0FuFskg5YmezTvacPd+mSYgFFQlq25zheabIZ0KbIIOqPjCDPoQ\n" +
            "HmyW74cNxA9hi63ugyuV+I6ShHI56yDqg+2DzZduCLzrTia2cyvk0/ZM/iZx4mER\n" +
            "dEr/VxqHD3VILs9RaRegAhJhldXRQLIQTO7ErBBDpqWeCtWVYpoNz4iCxTIM5Cuf\n" +
            "ReYNnyicsbkqWletNw+vHX/bvZ8=";

    public static final String SERVER_CERT1 = "MIIEfTCCA2WgAwIBAgIDG+cVMA0GCSqGSIb3DQEBCwUAMGMxCzAJBgNVBAYTAlVT\n" +
            "MSEwHwYDVQQKExhUaGUgR28gRGFkZHkgR3JvdXAsIEluYy4xMTAvBgNVBAsTKEdv\n" +
            "IERhZGR5IENsYXNzIDIgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkwHhcNMTQwMTAx\n" +
            "MDcwMDAwWhcNMzEwNTMwMDcwMDAwWjCBgzELMAkGA1UEBhMCVVMxEDAOBgNVBAgT\n" +
            "B0FyaXpvbmExEzARBgNVBAcTClNjb3R0c2RhbGUxGjAYBgNVBAoTEUdvRGFkZHku\n" +
            "Y29tLCBJbmMuMTEwLwYDVQQDEyhHbyBEYWRkeSBSb290IENlcnRpZmljYXRlIEF1\n" +
            "dGhvcml0eSAtIEcyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv3Fi\n" +
            "CPH6WTT3G8kYo/eASVjpIoMTpsUgQwE7hPHmhUmfJ+r2hBtOoLTbcJjHMgGxBT4H\n" +
            "Tu70+k8vWTAi56sZVmvigAf88xZ1gDlRe+X5NbZ0TqmNghPktj+pA4P6or6KFWp/\n" +
            "3gvDthkUBcrqw6gElDtGfDIN8wBmIsiNaW02jBEYt9OyHGC0OPoCjM7T3UYH3go+\n" +
            "6118yHz7sCtTpJJiaVElBWEaRIGMLKlDliPfrDqBmg4pxRyp6V0etp6eMAo5zvGI\n" +
            "gPtLXcwy7IViQyU0AlYnAZG0O3AqP26x6JyIAX2f1PnbU21gnb8s51iruF9G/M7E\n" +
            "GwM8CetJMVxpRrPgRwIDAQABo4IBFzCCARMwDwYDVR0TAQH/BAUwAwEB/zAOBgNV\n" +
            "HQ8BAf8EBAMCAQYwHQYDVR0OBBYEFDqahQcQZyi27/a9BUFuIMGU2g/eMB8GA1Ud\n" +
            "IwQYMBaAFNLEsNKR1EwRcbNhyz2h/t2oatTjMDQGCCsGAQUFBwEBBCgwJjAkBggr\n" +
            "BgEFBQcwAYYYaHR0cDovL29jc3AuZ29kYWRkeS5jb20vMDIGA1UdHwQrMCkwJ6Al\n" +
            "oCOGIWh0dHA6Ly9jcmwuZ29kYWRkeS5jb20vZ2Ryb290LmNybDBGBgNVHSAEPzA9\n" +
            "MDsGBFUdIAAwMzAxBggrBgEFBQcCARYlaHR0cHM6Ly9jZXJ0cy5nb2RhZGR5LmNv\n" +
            "bS9yZXBvc2l0b3J5LzANBgkqhkiG9w0BAQsFAAOCAQEAWQtTvZKGEacke+1bMc8d\n" +
            "H2xwxbhuvk679r6XUOEwf7ooXGKUwuN+M/f7QnaF25UcjCJYdQkMiGVnOQoWCcWg\n" +
            "OJekxSOTP7QYpgEGRJHjp2kntFolfzq3Ms3dhP8qOCkzpN1nsoX+oYggHFCJyNwq\n" +
            "9kIDN0zmiN/VryTyscPfzLXs4Jlet0lUIDyUGAzHHFIYSaRt4bNYC8nY7NmuHDKO\n" +
            "KHAN4v6mF56ED71XcLNa6R+ghlO773z/aQvgSMO3kwvIClTErF0UZzdsyqUvMQg3\n" +
            "qm5vjLyb4lddJIGvl5echK1srDdMZvNhkREg5L4wn3qkKQmw4TRfZHcYQFHfjDCm\n" +
            "rw==";

    public static final String SERVER_CERT = "MIIE0DCCA7igAwIBAgIBBzANBgkqhkiG9w0BAQsFADCBgzELMAkGA1UEBhMCVVMx\n" +
            "EDAOBgNVBAgTB0FyaXpvbmExEzARBgNVBAcTClNjb3R0c2RhbGUxGjAYBgNVBAoT\n" +
            "EUdvRGFkZHkuY29tLCBJbmMuMTEwLwYDVQQDEyhHbyBEYWRkeSBSb290IENlcnRp\n" +
            "ZmljYXRlIEF1dGhvcml0eSAtIEcyMB4XDTExMDUwMzA3MDAwMFoXDTMxMDUwMzA3\n" +
            "MDAwMFowgbQxCzAJBgNVBAYTAlVTMRAwDgYDVQQIEwdBcml6b25hMRMwEQYDVQQH\n" +
            "EwpTY290dHNkYWxlMRowGAYDVQQKExFHb0RhZGR5LmNvbSwgSW5jLjEtMCsGA1UE\n" +
            "CxMkaHR0cDovL2NlcnRzLmdvZGFkZHkuY29tL3JlcG9zaXRvcnkvMTMwMQYDVQQD\n" +
            "EypHbyBEYWRkeSBTZWN1cmUgQ2VydGlmaWNhdGUgQXV0aG9yaXR5IC0gRzIwggEi\n" +
            "MA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC54MsQ1K92vdSTYuswZLiBCGzD\n" +
            "BNliF44v/z5lz4/OYuY8UhzaFkVLVat4a2ODYpDOD2lsmcgaFItMzEUz6ojcnqOv\n" +
            "K/6AYZ15V8TPLvQ/MDxdR/yaFrzDN5ZBUY4RS1T4KL7QjL7wMDge87Am+GZHY23e\n" +
            "cSZHjzhHU9FGHbTj3ADqRay9vHHZqm8A29vNMDp5T19MR/gd71vCxJ1gO7GyQ5HY\n" +
            "pDNO6rPWJ0+tJYqlxvTV0KaudAVkV4i1RFXULSo6Pvi4vekyCgKUZMQWOlDxSq7n\n" +
            "eTOvDCAHf+jfBDnCaQJsY1L6d8EbyHSHyLmTGFBUNUtpTrw700kuH9zB0lL7AgMB\n" +
            "AAGjggEaMIIBFjAPBgNVHRMBAf8EBTADAQH/MA4GA1UdDwEB/wQEAwIBBjAdBgNV\n" +
            "HQ4EFgQUQMK9J47MNIMwojPX+2yz8LQsgM4wHwYDVR0jBBgwFoAUOpqFBxBnKLbv\n" +
            "9r0FQW4gwZTaD94wNAYIKwYBBQUHAQEEKDAmMCQGCCsGAQUFBzABhhhodHRwOi8v\n" +
            "b2NzcC5nb2RhZGR5LmNvbS8wNQYDVR0fBC4wLDAqoCigJoYkaHR0cDovL2NybC5n\n" +
            "b2RhZGR5LmNvbS9nZHJvb3QtZzIuY3JsMEYGA1UdIAQ/MD0wOwYEVR0gADAzMDEG\n" +
            "CCsGAQUFBwIBFiVodHRwczovL2NlcnRzLmdvZGFkZHkuY29tL3JlcG9zaXRvcnkv\n" +
            "MA0GCSqGSIb3DQEBCwUAA4IBAQAIfmyTEMg4uJapkEv/oV9PBO9sPpyIBslQj6Zz\n" +
            "91cxG7685C/b+LrTW+C05+Z5Yg4MotdqY3MxtfWoSKQ7CC2iXZDXtHwlTxFWMMS2\n" +
            "RJ17LJ3lXubvDGGqv+QqG+6EnriDfcFDzkSnE3ANkR/0yBOtg2DZ2HKocyQetawi\n" +
            "DsoXiWJYRBuriSUBAA/NxBti21G00w9RKpv0vHP8ds42pM3Z2Czqrpv1KrKQ0U11\n" +
            "GIo/ikGQI31bS/6kA1ibRrLDYGCD+H1QQc7CoZDDu+8CL9IVVO5EFdkKrqeKM+2x\n" +
            "LXY2JtwE65/3YR8V3Idv7kaWKK2hJn0KCacuBKONvPi8BDAB";
    private static String TAG = SocketUtils.class.getSimpleName();
    private static OkHttpClient okHttpClient;

//    private SSLContext sslContext;
//    private final TrustManager[] trustAllCerts= new TrustManager[] { new X509TrustManager() {
//        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//            return new java.security.cert.X509Certificate[] {};
//        }
//
//        public void checkClientTrusted(X509Certificate[] chain,
//                                       String authType) throws CertificateException {
//        }
//
//        public void checkServerTrusted(X509Certificate[] chain,
//                                       String authType) throws CertificateException {
//        }
//    } };

    public SocketUtils(Context context) {
        this.context = context;
        configureSocketForSSL(buildSslSocketFactory(context));

//        sslContext.init(null, trustAllCerts, null);

        try {
            //mSocket = IO.socket("https://socket-io-chat.now.sh/");
//                configureSocketForSSL();
// Install the all-trusting trust manager
            //IO.setDefaultSSLContext(sslContext);
            IO.Options options = new IO.Options();
            options.path = SOCKET_PATH;
//            options.transports = TRANSPORTS;
            options.transports = new String[]{WebSocket.NAME};
            options.reconnection = true;
            options.reconnectionDelay = 5000;
//            options.callFactory = okHttpClient;
            options.webSocketFactory = okHttpClient;
//            options.sslContext = configureSocketForSSL();
            // options.timeout = 60000;
            mSocket = IO.socket(SOCKET_URI, options);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public Socket getmSocket() {
        return mSocket;
    }

    public void connect() {
        if (mSocket != null) {
            Log.d(TAG, "mSocket: connected");
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

            mSocket.on(Socket.EVENT_RECONNECTING, onReconnecting);
            mSocket.on(Socket.EVENT_RECONNECT, onReconnect);
            mSocket.on(Socket.EVENT_RECONNECT_ERROR, onReconnectError);
            mSocket.on(Socket.EVENT_RECONNECT_FAILED, onReconnectFailed);

            mSocket.on(EVENT_UNAUTHORIZED, onUnAuthorized);
            // mSocket.on("onGameResult", onGameResult);
            mSocket.on(EVENT_CONTEST_START, onContestStart);
            mSocket.on(EVENT_CONTEST_END, onContestEnd);
            mSocket.on(EVENT_PAYMENT_UPDATE, onPaymentUpdate);
            mSocket.on(EVENT_CONTEST_UPDATE, onContestUpdate);
            mSocket.on(EVENT_HOST_NOTIFY, onHostNotify);
            mSocket.on(EVENT_CONTEST_LIVE_UPDATE, onContestLiveUpdate);
            mSocket.on(EVENT_CONTEST_ALERT, onContestAlert);
            mSocket.on(EVENT_APP_VERSION, onVersionUpdate);
            mSocket.on(EVENT_KYC_UPDATE, onKycUpdate);

            //mSocket.on("onCherry", onCherry);
            mSocket.connect();

        }
    }

    private static SSLSocketFactory buildSslSocketFactory(Context context) {
        // Add support for self-signed (local) SSL certificates
        // Based on http://developer.android.com/training/articles/security-ssl.html#UnknownCa
        try {

            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // From https://www.washington.edu/itconnect/security/ca/load-der.crt
            InputStream is = context.getResources().getAssets().open("raw/ssl.crt");
            InputStream caInput = new BufferedInputStream(is);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                // System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
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
            return sslContext.getSocketFactory();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private static void configureSocketForSSL(SSLSocketFactory sslSocketFactory) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // From https://www.washington.edu/itconnect/security/ca/load-der.crt
            InputStream is = context.getResources().getAssets().open("raw/ssl.crt");
            InputStream caInput = new BufferedInputStream(is);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                // System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf1 = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf1.init(keyStore);

         /*   HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    HostnameVerifier hv =
                            HttpsURLConnection.getDefaultHostnameVerifier();
                    return hv.verify(SOCKET_URI, session);
                }
            };

            okHttpClient = new OkHttpClient.Builder()
                    .hostnameVerifier(hostnameVerifier)
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) tmf1)
                    .build();*/

            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {

                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            }};
            X509TrustManager trustManager = (X509TrustManager) trustAllCerts[0];

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, null);
            SSLSocketFactory sslSocketFactorys = sslContext.getSocketFactory();

            okHttpClient = new OkHttpClient.Builder()
                    .hostnameVerifier(hostnameVerifier)
                    .sslSocketFactory(sslSocketFactorys, trustManager)
                    .build();
            IO.Options opts = new IO.Options();
            opts.callFactory = okHttpClient;
            opts.webSocketFactory = okHttpClient;
            mSocket = IO.socket(SOCKET_URI, opts);

            // IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
            //IO.setDefaultOkHttpCallFactory(okHttpClient);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Emitter.Listener onReconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // if(!isConnected){
                    // System.out.println("onConnect Connected");
                    //   isConnected = true;
                    PrintLog.e(TAG, "On Reconnected");
                    // }
                }
            });
        }
    };

    private Emitter.Listener onReconnecting = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // if(!isConnected){
                    // System.out.println("onConnect Connected");
                    //   isConnected = true;
                    PrintLog.e(TAG, "On Reconnecting");
                    EventBus.getDefault().post(new SocketConnectionEvent("On Reconnecting"));
                    // }
                }
            });
        }
    };

    private Emitter.Listener onReconnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // if(!isConnected){
                    // System.out.println("onConnect Connected");
                    //   isConnected = true;
                    PrintLog.e(TAG, "On Reconnect Error");
                    // }
                }
            });
        }
    };

    private Emitter.Listener onReconnectFailed = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // if(!isConnected){
                    // System.out.println("onConnect Connected");
                    //   isConnected = true;
                    PrintLog.e(TAG, "On Reconnect Failed");
                    // }
                }
            });
        }
    };


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // if(!isConnected){
                    // System.out.println("onConnect Connected");
                    //   isConnected = true;
                    PrintLog.e(TAG, "Connected");
                    EventBus.getDefault().post(new SocketConnectionEvent("Connected"));
                    // }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //PrintLog.e(TAG, "disconnected");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // System.out.println("onDisconnect disconnected");
                    // isConnected = false;

                    //disConnect();
                    PrintLog.e(TAG, "disconnected");
                    EventBus.getDefault().post(new SocketConnectionEvent("disconnected"));
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            //PrintLog.e(TAG, "Error connecting");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //disConnect();
                    // System.out.println("onConnectError Error connecting");
                    mSocket.connect();
                    PrintLog.e(TAG, "E " + args[0].toString());

                    EventBus.getDefault().post(new SocketConnectionEvent("Error connecting"));
                }
            });
        }
    };

    private Emitter.Listener onUnAuthorized = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        PrintLog.e(SocketUtils.TAG, "UnAuthorized " + args[0].toString());
                        String res = "";
                        res = CBit.getCryptLib().decryptCipherTextWithRandomIV(args[0].toString(), context.getString(R.string.crypt_pass));
                        JSONObject jsonObject = new JSONObject(res);
                        PrintLog.e(TAG, jsonObject.toString());
                        EventBus.getDefault().post(new UnAuthorizedEvent(jsonObject.getString("message")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onContestStart = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    String res = "";
                    res = CBit.getCryptLib().decryptCipherTextWithRandomIV(args[0].toString(), context.getString(R.string.crypt_pass));
                    JSONObject jsonObject = new JSONObject(res);
                    PrintLog.e(TAG, jsonObject.toString());
                    String contestId = "";
                    contestId = jsonObject.getString("contestId");
                    EventBus.getDefault().post(new GameStartEvent(contestId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    };


    private Emitter.Listener onContestAlert = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //System.out.println("onUnAuthorized ");
                    try {
                        String res = "";
                        res = CBit.getCryptLib().decryptCipherTextWithRandomIV(args[0].toString(), context.getString(R.string.crypt_pass));
                        JSONObject jsonObject = new JSONObject(res);
                        PrintLog.e(TAG, jsonObject.toString());
                        SessionUtil sessionUtil = new SessionUtil(context);
                        if (sessionUtil.getNotification() == 1) {
                           sendNotification(String.valueOf(jsonObject.getInt("contestId")));
                        }
                        EventBus.getDefault().post(new GameAlertEvent(String.valueOf(jsonObject.getInt("contestId"))));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onVersionUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //System.out.println("onUnAuthorized ");
                    try {
                        String res = "";
                        res = CBit.getCryptLib().decryptCipherTextWithRandomIV(args[0].toString(), context.getString(R.string.crypt_pass));
                        JSONObject jsonObject = new JSONObject(res);
                        PrintLog.e(TAG, jsonObject.toString());
                        Log.d(TAG, "onVersionUpdate: " + jsonObject.toString());

                        String VersionNo = jsonObject.getString("Version");
                        String Message = jsonObject.getString("Message");
                        if (!VersionNo.equalsIgnoreCase(BuildConfig.VERSION_NAME)) {
                            EventBus.getDefault().post(new UnAuthorizedEvent(Message));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onKycUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //System.out.println("onUnAuthorized ");
                    try {
                        String res = "";
                        res = CBit.getCryptLib().decryptCipherTextWithRandomIV(args[0].toString(), context.getString(R.string.crypt_pass));
                        PrintLog.e(TAG,"onKycUpdate=>"+ res);
                        EventBus.getDefault().post(new UpdateWallet());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    private Emitter.Listener onPaymentUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        String res = "";
                        res = CBit.getCryptLib().decryptCipherTextWithRandomIV(args[0].toString(), context.getString(R.string.crypt_pass));
                        JSONObject jsonObject = new JSONObject(res);
                        PrintLog.e(TAG, jsonObject.toString());
                        Log.d(TAG, "onPaymentUpdate: " + jsonObject.toString());
                        DecimalFormat format = new DecimalFormat("0.##");
                        SessionUtil sessionUtil = new SessionUtil(context);
                        sessionUtil.setAmount(String.valueOf(format.format(jsonObject.getDouble("pbAmount"))));
                        sessionUtil.setWAmount(String.valueOf(format.format(jsonObject.getDouble("sbAmount"))));
                        sessionUtil.setCredentiaCurrency(String.valueOf(format.format(jsonObject.getDouble("ccAmount"))));
                        Log.d(TAG, "ccAmountSocket: " + jsonObject.getDouble("ccAmount"));

                        //  sessionUtil.setWalletAuth(String.valueOf(format.format(jsonObject.getDouble("WalletAuth"))));
                        EventBus.getDefault().post(new UpdateWallet());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onContestUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new UpdateUpcomingContestEvent());
                    EventBus.getDefault().post(new UpdateSpecialContestEvent());
                    EventBus.getDefault().post(new UpdateMyContestEvent());
                    EventBus.getDefault().post(new UpdateHistoryEvent());
                }
            });
        }
    };

    private Emitter.Listener onContestEnd = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //System.out.println("onUnAuthorized ");
                    try {
                        String res = "";
                        res = CBit.getCryptLib().decryptCipherTextWithRandomIV(args[0].toString(), context.getString(R.string.crypt_pass));
                        JSONObject jsonObject = new JSONObject(res);
                        PrintLog.e(TAG, jsonObject.toString());
                        String contestId = "";
                        contestId = jsonObject.getString("contestId");
                        EventBus.getDefault().post(new GameResultEvent(contestId));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onHostNotify = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //System.out.println("onUnAuthorized ");
                    try {
                        String res = "";
                        res = CBit.getCryptLib().decryptCipherTextWithRandomIV(args[0].toString(), context.getString(R.string.crypt_pass));
                        JSONObject jsonObject = new JSONObject(res);
                        PrintLog.e(TAG, "Host Notify " + jsonObject.toString());
                        SessionUtil sessionUtil = new SessionUtil(context);
                        if (sessionUtil.getNotification() == 1) {
                            sendPrivateNotification(String.valueOf(jsonObject.getInt("contestId")),
                                    jsonObject.getString("contestName"), jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    private Emitter.Listener onContestLiveUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        String res = "";
                        res = CBit.getCryptLib().decryptCipherTextWithRandomIV(args[0].toString(), context.getString(R.string.crypt_pass));
                        PrintLog.e(TAG, res);
                        EventBus.getDefault().post(new ContestLiveUpdate(res));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
/*
private Emitter.Listener onContestLive = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        String res = "";
                       res = CBit.getCryptLib().decryptCipherTextWithRandomIV(args[0].toString(), context.getString(R.string.crypt_pass));
                        PrintLog.e(TAG, res);
                        EventBus.getDefault().post(new ContestLive(res));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
*/
    public void loginEmit(String id) {
        try {
            JSONObject object = new JSONObject();
            object.put("user_id", id);
            byte[] data;
            String request = "";
            request = object.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, context.getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
            mSocket.emit(EVENT_LOGIN, request, new Ack() {
                @Override
                public void call(Object... args) {
                    final String response = args[0].toString();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            //System.out.println("onLogin ");
                            try {
                                String res = "";
                                res = CBit.getCryptLib().decryptCipherTextWithRandomIV(response, context.getString(R.string.crypt_pass));
                                JSONObject jsonObject = new JSONObject(res);
                                PrintLog.e(TAG, jsonObject.toString());
                                Gson gson = new Gson();
                                WalletTransferModel wtm = gson.fromJson(res, WalletTransferModel.class);
                                if (wtm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                                    DecimalFormat format = new DecimalFormat("0.##");
                                    SessionUtil sessionUtil = new SessionUtil(context);
                                    sessionUtil.setAmount(String.valueOf(format.format(wtm.getContent().getPbAmount())));
                                    sessionUtil.setWAmount(String.valueOf(format.format(wtm.getContent().getSbAmount())));
                                    EventBus.getDefault().post(new UpdateWallet());
                                }

                                //EventBus.getDefault().post(new UnAuthorizedEvent(jsonObject.getString("message")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendNotification(String contestId) {

        int notificationID = (int) System.currentTimeMillis();
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(GameViewActivity.CONTESTID, contestId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationID,
                intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.app_green_icon);
        String CHANNEL_ID = context.getApplicationContext().getPackageName();
        CharSequence name = context.getString(R.string.app_name);
        String Description = "This is Cbit channel";

        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.app_green_icon)
                .setColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorPrimary))
                .setLargeIcon(icon)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setAutoCancel(true);

        notificationBuilder.setContentTitle(context.getString(R.string.app_name));
        notificationBuilder.setContentText("Contest starts in 1 mins. Be in time. All the best.");

        NotificationManager notificationManager =
                (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        if (pendingIntent != null)
            notificationBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(Integer.parseInt(contestId), notificationBuilder.build());
    }

    private void sendPrivateNotification(String contestId, String contestName, String msg) {
        int notificationID = (int) System.currentTimeMillis();
        Intent intent = new Intent(context, PrivateContestDetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(PrivateContestDetailsActivity.CONTESTID, contestId);
        intent.putExtra(PrivateContestDetailsActivity.CONTESTNAME, contestName);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationID,
                intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.app_green_icon);
        String CHANNEL_ID = context.getApplicationContext().getPackageName();
        CharSequence name = context.getString(R.string.app_name);
        String Description = "This is Cbit channel";
        Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.getPackageName() + "/raw/alarm");

        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.app_green_icon)
                .setColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorPrimary))
                .setLargeIcon(icon)
                .setSound(alarmSound)
                .setAutoCancel(true);

        notificationBuilder.setContentTitle(context.getString(R.string.app_name));
        notificationBuilder.setContentText(msg);

        NotificationManager notificationManager =
                (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            mChannel.setSound(alarmSound,att);
            notificationManager.createNotificationChannel(mChannel);
        }

        if (pendingIntent != null)
            notificationBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(007, notificationBuilder.build());
    }

    public void disConnect() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.off(EVENT_UNAUTHORIZED, onUnAuthorized);
            mSocket.off(EVENT_CONTEST_START, onContestStart);
            mSocket.off(EVENT_CONTEST_END, onContestEnd);
            mSocket.off(EVENT_PAYMENT_UPDATE, onPaymentUpdate);
            mSocket.off(EVENT_CONTEST_UPDATE, onContestUpdate);
            mSocket.off(EVENT_HOST_NOTIFY, onHostNotify);
            mSocket.off(EVENT_CONTEST_LIVE_UPDATE, onContestLiveUpdate);
            mSocket.off(EVENT_CONTEST_ALERT, onContestAlert);
            mSocket.off(EVENT_APP_VERSION, onVersionUpdate);
            mSocket.off(EVENT_KYC_UPDATE, onKycUpdate);

            mSocket.off(Socket.EVENT_RECONNECTING, onReconnecting);
            mSocket.off(Socket.EVENT_RECONNECT, onReconnect);
            mSocket.off(Socket.EVENT_RECONNECT_ERROR, onReconnectError);
            mSocket.off(Socket.EVENT_RECONNECT_FAILED, onReconnectFailed);
        }
    }
}
