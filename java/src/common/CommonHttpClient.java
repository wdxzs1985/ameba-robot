package common;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

public class CommonHttpClient {

    private static final String USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25";
    private static final int TIMEOUT = 20000;

    private final Log log = LogFactory.getLog(CommonHttpClient.class);
    private final HttpClient client;
    private final BasicCookieStore cookieStore;
    private String referer = "";

    public CommonHttpClient() {
        final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        // Increase max total connection to 200
        connManager.setMaxTotal(200);
        // Increase default max connection per route to 20
        connManager.setDefaultMaxPerRoute(20);

        this.cookieStore = new BasicCookieStore();

        final RequestConfig defaultRequestConfig = RequestConfig.custom()
                                                                .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY)
                                                                .build();

        final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setConnectionManager(connManager);
        clientBuilder.setDefaultCookieStore(this.cookieStore);
        clientBuilder.setDefaultRequestConfig(defaultRequestConfig);
        clientBuilder.setUserAgent(CommonHttpClient.USER_AGENT);
        clientBuilder.setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE);
        final Collection<Header> defaultHeaders = new LinkedList<Header>();
        defaultHeaders.add(new BasicHeader("Accept",
                                           "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
        defaultHeaders.add(new BasicHeader("Accept-Charset", "UTF-8;"));
        defaultHeaders.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
        defaultHeaders.add(new BasicHeader("Accept-Language", "ja"));
        defaultHeaders.add(new BasicHeader("Cache-Control", "no-cache"));
        defaultHeaders.add(new BasicHeader("Connection", "keep-alive"));
        defaultHeaders.add(new BasicHeader("Pragma", "no-cache"));
        clientBuilder.setDefaultHeaders(defaultHeaders);
        this.client = clientBuilder.build();

    }

    public String get(final String url) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("method : GET");
            this.log.debug("   url : " + url);
        }
        final HttpGet httpget = new HttpGet(url);
        final RequestConfig defaultRequestConfig = RequestConfig.custom()
                                                                .setSocketTimeout(CommonHttpClient.TIMEOUT)
                                                                .setConnectTimeout(CommonHttpClient.TIMEOUT)
                                                                .setConnectionRequestTimeout(CommonHttpClient.TIMEOUT)
                                                                .build();
        httpget.setConfig(defaultRequestConfig);
        this.initHttpHeader(httpget);

        String result = null;
        final HttpClientContext localContext = new HttpClientContext();
        // Bind custom cookie store to the local context
        localContext.setCookieStore(this.cookieStore);
        try {
            // Pass local context as a parameter
            final HttpResponse response = this.client.execute(httpget,
                                                              localContext);
            final HttpEntity entity = response.getEntity();
            result = this.entityToString(entity);
            // Consume response content
            EntityUtils.consume(entity);
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("result : " + result);
        }
        return result;
    }

    public String post(final String url,
                       final List<? extends NameValuePair> nvps) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("method : POST");
            this.log.debug("   url : " + url);
            for (final NameValuePair nvp : nvps) {
                this.log.debug("form-" + nvp.getName() + " : " + nvp.getValue());
            }
        }
        final HttpPost httppost = new HttpPost(url);
        final RequestConfig defaultRequestConfig = RequestConfig.custom()
                                                                .setSocketTimeout(CommonHttpClient.TIMEOUT)
                                                                .setConnectTimeout(CommonHttpClient.TIMEOUT)
                                                                .setConnectionRequestTimeout(CommonHttpClient.TIMEOUT)
                                                                .build();
        httppost.setConfig(defaultRequestConfig);
        this.initHttpHeader(httppost);

        String result = null;
        final HttpClientContext localContext = new HttpClientContext();
        // Bind custom cookie store to the local context
        localContext.setCookieStore(this.cookieStore);
        try {
            final UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(nvps,
                                                                             SystemConstants.ENCODING);
            httppost.setEntity(postEntity);
            if (this.log.isDebugEnabled()) {
                this.log.debug("entity : " + this.entityToString(postEntity));
            }
            final HttpResponse response = this.client.execute(httppost,
                                                              localContext);
            final HttpEntity entity = response.getEntity();
            result = this.entityToString(entity);

            // Consume response content
            EntityUtils.consume(entity);
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("result : " + result);
        }
        return result;
    }

    public byte[] getByte(final String url) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("method : GET");
            this.log.debug("   url : " + url);
        }
        final HttpGet httpget = new HttpGet(url);
        this.initHttpHeader(httpget);

        byte[] result = null;
        final HttpClientContext localContext = new HttpClientContext();
        // Bind custom cookie store to the local context
        localContext.setCookieStore(this.cookieStore);
        try {
            // Pass local context as a parameter
            final HttpResponse response = this.client.execute(httpget,
                                                              localContext);
            final HttpEntity entity = response.getEntity();
            result = EntityUtils.toByteArray(entity);
            // Consume response content
            EntityUtils.consume(entity);
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("result : " + result);
        }
        return result;
    }

    public String findCookie(final String cookieName) {
        final List<Cookie> cookies = this.cookieStore.getCookies();
        for (final Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    protected void initHttpHeader(final HttpMessage httpMessage) {
        httpMessage.addHeader("Referer", this.referer);
    }

    private String entityToString(final HttpEntity entity)
            throws ParseException, IOException {
        String result = null;
        if (this.isGzip(entity)) {
            result = EntityUtils.toString(new GzipDecompressingEntity(entity));
        } else {
            result = EntityUtils.toString(entity, SystemConstants.ENCODING);
        }
        return result;
    }

    private boolean isGzip(final HttpEntity entity) {
        final Header contentEncodingHeader = entity.getContentEncoding();
        if (contentEncodingHeader != null) {
            final String contentEncoding = contentEncodingHeader.getValue();
            if (StringUtils.equalsIgnoreCase(contentEncoding, "gzip")) {
                return true;
            }
        }
        return false;
    }

    public void loadCookie(final File localFile) {
        try {
            final List<String> lines = FileUtils.readLines(localFile, "utf-8");
            for (final String line : lines) {
                final String[] cookieValue = StringUtils.split(line, ";");
                final String[] nameValue = StringUtils.split(cookieValue[0],
                                                             "=");
                final String name = nameValue[0];
                final String value = nameValue[1];
                final String domain = StringUtils.split(cookieValue[1], "=")[1];
                final String path = StringUtils.split(cookieValue[2], "=")[1];
                final long expires = Long.valueOf(StringUtils.split(cookieValue[3],
                                                                    "=")[1]);
                final int version = Integer.valueOf(StringUtils.split(cookieValue[4],
                                                                      "=")[1]);
                final boolean secure = Boolean.valueOf(StringUtils.split(cookieValue[5],
                                                                         "=")[1]);

                final BasicClientCookie cookie = new BasicClientCookie(name,
                                                                       value);
                cookie.setDomain(domain);
                cookie.setPath(path);
                cookie.setExpiryDate(new Date(expires));
                cookie.setVersion(version);
                cookie.setSecure(secure);
                this.cookieStore.addCookie(cookie);
            }
        } catch (final IOException e) {
            this.log.debug("no cookie to load.");
        }
    }

    public void saveCookie(final File localFile) {
        final List<Cookie> cookieList = this.cookieStore.getCookies();
        if (CollectionUtils.isEmpty(cookieList)) {
            this.log.debug("no cookie to load.");
            return;
        }
        final List<String> lines = new LinkedList<String>();
        for (final Cookie cookie : cookieList) {
            final String line = this.cookieToString(cookie);
            if (StringUtils.isNotBlank(line)) {
                lines.add(line);
            }
        }
        if (CollectionUtils.isNotEmpty(lines)) {
            try {
                FileUtils.writeLines(localFile, "utf-8", lines);
            } catch (final IOException e) {
                this.log.error("write cookie failed.", e);
            }
        }
    }

    private String cookieToString(final Cookie cookie) {
        if (cookie.getExpiryDate() == null) {
            return null;
        }

        final String name = cookie.getName();
        final String value = cookie.getValue();
        final String domain = cookie.getDomain();
        final String path = cookie.getPath();
        final long expires = cookie.getExpiryDate().getTime();
        final int version = cookie.getVersion();
        final boolean secure = cookie.isSecure();
        return String.format("%s=%s; Domain=%s; Path=%s; Expires=%d; Version=%d; Secure=%s",
                             name,
                             value,
                             domain,
                             path,
                             expires,
                             version,
                             secure);
    }

    public String getReferer() {
        return this.referer;
    }

    public void setReferer(final String referer) {
        this.referer = referer;
    }
}
