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

    public static final int MAX_RETRY = 3;
    public static final int SLEEP_TIME = 5000;

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
        int wrongTime = 0;
        while (this.nextHandler != null && AbstractRobot.MAX_RETRY > wrongTime) {
            try {
                final EventHandler currEventHandler = this.nextHandler;
                this.nextHandler = null;
                currEventHandler.handle();
                this.httpClient.saveCookie(this.cookieFile);
                Thread.sleep(AbstractRobot.SLEEP_TIME + RandomUtils.nextInt(AbstractRobot.SLEEP_TIME));
            } catch (final Exception e) {
                wrongTime++;
                this.log.error("发生异常", e);
                this.dispatch("/");
            }
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
