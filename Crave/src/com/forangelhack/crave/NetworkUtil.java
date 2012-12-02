package com.forangelhack.crave;

import org.apache.http.client.HttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HeaderElement;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import org.apache.http.entity.HttpEntityWrapper;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import android.content.Context;
import java.io.InputStream;
import android.content.pm.PackageManager.NameNotFoundException;
import org.apache.http.Header;
import android.text.format.DateUtils;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.protocol.HTTP;
import android.util.Log;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpStatus;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import javax.net.ssl.SSLException;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.util.EntityUtils;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.GeneralSecurityException;

/**
 * Describe class NetworkUtil here.
 *
 *
 * Created: Tue Jun 07 15:09:35 2011
 *
 * @author <a href="mailto:"></a>
 * @version 1.0
 */
public class NetworkUtil {
    private static final String TAG = "NetworkUtil";
    private static final int SECOND_IN_MILLIS = (int) DateUtils.SECOND_IN_MILLIS;
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    public NetworkUtil() {

    }

    public static boolean exists(Context ctx, String URL){
	boolean res = false;
	final HttpClient lClient = getHttpClient(ctx);
        final HttpUriRequest request = new HttpGet(URL);
	try {
	    HttpResponse resp = lClient.execute(request);
	    res = (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK );
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	return res;
	}


    public static InputStream getURLStream(Context ctx, String url) throws IOException {
	final HttpClient lClient = getHttpClient(ctx, url.startsWith("https"));
        final HttpUriRequest request = new HttpGet(url);
	HttpResponse resp = lClient.execute(request);
	InputStream is = resp.getEntity().getContent();
	return is;
    }

    private static DefaultHttpClient getDefaultHttpClient(ClientConnectionManager conMgr, HttpParams params, 
							 boolean useHttps) {
	DefaultHttpClient client = new DefaultHttpClient(conMgr, params);
	// if ( UIUtils.isAfterEclair() || useHttps == false ) // will use default client in froyo...
	//     return client;
	// Log.d(TAG, "try to use more tolarate http client");
	// // for earlier android that does not think *.domain cert is the same as domain cert...
	// SSLSocketFactory sslSocketFactory = (SSLSocketFactory) client
        //     .getConnectionManager().getSchemeRegistry().getScheme("https")
        //     .getSocketFactory();

	// final X509HostnameVerifier delegate = sslSocketFactory.getHostnameVerifier();
	// if(!(delegate instanceof MyVerifier)) {
	//     sslSocketFactory.setHostnameVerifier(new MyVerifier(delegate));
	// }

	return client;

    }

    public static HttpClient getHttpClient(Context context, boolean useHttps) {
        final HttpParams params = new BasicHttpParams();
	
	HttpConnectionParams.setStaleCheckingEnabled(params, false);

        // Use generous timeouts for slow mobile networks
        HttpConnectionParams.setConnectionTimeout(params, 20 * SECOND_IN_MILLIS);
        HttpConnectionParams.setSoTimeout(params, 20 * SECOND_IN_MILLIS);

        HttpConnectionParams.setSocketBufferSize(params, 8192);
	HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpProtocolParams.setUserAgent(params, buildUserAgent(context));

	SchemeRegistry schReg = new SchemeRegistry();
	schReg.register(new Scheme("http", PlainSocketFactory
				   .getSocketFactory(), 80));
	schReg.register(new Scheme("https",
				   SSLSocketFactory.getSocketFactory(), 443));
	ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);


        final DefaultHttpClient client = getDefaultHttpClient(conMgr, params, useHttps); //new DefaultHttpClient(conMgr, params);

        client.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context) {
                // Add header to accept gzip content
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
            }
        });

        client.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(HttpResponse response, HttpContext context) {
                // Inflate any responses compressed with gzip
                final HttpEntity entity = response.getEntity();
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
			    //Log.d(TAG, "using gzip");
                            response.setEntity(new InflatingEntity(response.getEntity()));
                            break;
                        }
                    }
                }
            }
        });

        return client;
    }

    public static HttpClient getHttpClient(Context context) {
	return getHttpClient(context, true);
    }

    /**
     * Build and return a user-agent string that can identify this application
     * to remote servers. Contains the package name and version code.
     */
    private static String buildUserAgent(Context context) {
	if ( context == null )
	    return "cattail software default UA (gzip)";

        try {
            final PackageManager manager = context.getPackageManager();
            final PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

            // Some APIs require "(gzip)" in the user-agent string.
            return info.packageName + "/" + info.versionName
                    + " (" + info.versionCode + ") (gzip)";
        } catch (NameNotFoundException e) {
            return null;
        }

    }

    /**
     * Simple {@link HttpEntityWrapper} that inflates the wrapped
     * {@link HttpEntity} by passing it through {@link GZIPInputStream}.
     */
    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }

    // from Stackoverflow 
    // http://stackoverflow.com/questions/3135679/android-httpclient-hostname-in-certificate-didnt-match-example-com-exa/3136980#3136980
    static class MyVerifier extends AbstractVerifier {

	private final X509HostnameVerifier delegate;

	public MyVerifier(final X509HostnameVerifier delegate) {
	    this.delegate = delegate;
	}

	public void verify(String host, String[] cns, String[] subjectAlts)
	    throws SSLException {
	    boolean ok = false;
	    try {
		delegate.verify(host, cns, subjectAlts);
	    } catch (SSLException e) {
		for (String cn : cns) {
		    if (cn.startsWith("*.")) {
			try {
			    delegate.verify(host, new String[] { 
						cn.substring(2) }, subjectAlts);
			    ok = true;
			} catch (Exception e1) { }
		    }
		}
		if(!ok) throw e;
	    }
	}
    }

    public static JSONArray commonInit(String qUrl, Context ctx, boolean decrypt) throws IOException,
											  JSONException, 
											  GeneralSecurityException
    {
	HttpClient lClient = getHttpClient(ctx, qUrl.startsWith("https"));
	HttpGet lGetMethod = new HttpGet(qUrl);
	HttpResponse lResp = null;
	lResp = lClient.execute(lGetMethod);
	String lInfoStr = null;
	lInfoStr = EntityUtils.toString(lResp.getEntity(), "UTF-8");// lBOS.toString("UTF-8");
	if ( decrypt == true ) {
	    Log.d(TAG, "decrypting");
	    lInfoStr = decrypt(lInfoStr);
	}
	Log.d(TAG, "infostr="+lInfoStr);
	JSONArray lGRes = null;
	lGRes = new JSONArray(lInfoStr);
	lClient.getConnectionManager().shutdown();

	return lGRes;

    }

    public static JSONArray commonInit(String qUrl, Context ctx) throws IOException,
									 JSONException {
	try {
	return commonInit(qUrl, ctx, false);
	}
	catch(GeneralSecurityException e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static byte[] copyArray(byte[] in, int len) {
	    byte[] ret = new byte[len];
	    // pad with 0 first
	    for ( int i = 0; i < ret.length; i++ )
		ret[i] = 0;
	    for( int i = 0; i < in.length && i < len; i++ ) {
		ret[i] = in[i];
	    }
	    return ret;

    }

    public static String decrypt(String inString) throws GeneralSecurityException, IOException  {
	return inString;
    }

}
