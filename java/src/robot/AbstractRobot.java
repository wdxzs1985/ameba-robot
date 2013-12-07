package robot;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.CommonHttpClient;

public abstract class AbstractRobot implements Robot, Runnable {

    protected final Log log;
    private final String host;
    private final CommonHttpClient httpClient;
    private final File cookieFile;
    private EventHandler nextHandler = null;
    private final Map<String, EventHandler> handlerMapping;
    private final Map<String, Object> session;
    private final Properties config;

    public AbstractRobot(final String host, final Properties config) {
        this.host = host;
        this.log = LogFactory.getLog(this.getClass());

        this.session = Collections.synchronizedMap(new HashMap<String, Object>());
        this.config = config;

        final String username = this.getConfig()
                                    .getProperty("LoginHandler.username");
        this.httpClient = new CommonHttpClient();
        this.cookieFile = new File(username + ".cookie");
        this.httpClient.loadCookie(this.cookieFile);
        this.handlerMapping = new HashMap<String, EventHandler>();
    }

    protected void registerHandler(final String path, final EventHandler handler) {
        this.handlerMapping.put(path, handler);
    }

    @Override
    public void run() {
        this.dispatch("/");
        while (this.nextHandler != null) {
            final EventHandler currEventHandler = this.nextHandler;
            this.nextHandler = null;
            try {
                currEventHandler.handle();
            } catch (final Exception e) {
                final String message = e.getMessage();
                this.log.error("发生异常: " + message, e);
                if (currEventHandler instanceof LoginHandler == false) {
                    this.dispatch("/");
                }
            } finally {
                this.httpClient.saveCookie(this.cookieFile);
                this.sleep();
            }
        }
    }

    private void sleep() {
        final String actionTime = this.config.getProperty("AbstractRobot.actionTime",
                                                          "3");
        int sleepTime = Integer.valueOf(actionTime);
        sleepTime = sleepTime + RandomUtils.nextInt(sleepTime);
        try {
            Thread.sleep(sleepTime * 1000);
        } catch (final InterruptedException e) {
        }
    }

    @Override
    public void dispatch(final String event) {
        this.nextHandler = this.handlerMapping.get(event);
        if (this.nextHandler == null) {
            if (this.log.isInfoEnabled()) {
                this.log.warn(String.format("未知方法[%s]", event));
            }
        }
    }

    @Override
    public String buildPath(final String path) {
        return this.host + path;
    }

    @Override
    public CommonHttpClient getHttpClient() {
        return this.httpClient;
    }

    @Override
    public Map<String, Object> getSession() {
        return this.session;
    }

    @Override
    public Properties getConfig() {
        return this.config;
    }
}
