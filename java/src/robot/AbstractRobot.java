package robot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.CommonHttpClient;

public abstract class AbstractRobot implements Robot, Runnable {

    protected final Log log = LogFactory.getLog(this.getClass());
    private final Map<String, Object> session = Collections.synchronizedMap(new HashMap<String, Object>());
    private final Map<String, EventHandler> handlerMapping = new HashMap<String, EventHandler>();

    private CommonHttpClient httpClient = null;
    private EventHandler nextHandler = null;
    private Properties config = null;

    protected void registerHandler(final String path, final EventHandler handler) {
        this.handlerMapping.put(path, handler);
    }

    @Override
    public void run() {
        this.dispatch("/");
        while (this.nextHandler != null) {
            final EventHandler currEventHandler = this.nextHandler;
            this.nextHandler = null;
            currEventHandler.handle();
            this.httpClient.saveCookie();
        }
    }

    @Override
    public void dispatch(final String event) {
        if (StringUtils.equals(event, "/exit")) {
            this.exit();
        } else {
            this.nextHandler = this.handlerMapping.get(event);
            if (this.nextHandler == null) {
                if (this.log.isWarnEnabled()) {
                    this.log.warn(String.format("未知方法[%s]", event));
                }
                this.exit();
            }
        }
    }

    private void exit() {
        this.nextHandler = null;
        if (this.log.isInfoEnabled()) {
            final int delay = this.getScheduleDelay();
            this.log.info(String.format("休息%d分钟", delay));
        }
    }

    public String buildPath(final String path) {
        final String host = this.getHost();
        return host + path;
    }

    public Map<String, Object> getSession() {
        return this.session;
    }

    public CommonHttpClient getHttpClient() {
        return this.httpClient;
    }

    public void setHttpClient(final CommonHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Properties getConfig() {
        return this.config;
    }

    public void setConfig(final Properties config) {
        this.config = config;
    }

    protected abstract String getHost();

    public int getRequestDelay() {
        final String key = "Robot.requestDelay";
        final String value = this.getConfig().getProperty(key, "3");
        return Integer.valueOf(value);
    }

    public int getScheduleDelay() {
        final String key = "Robot.scheduleDelay";
        final String value = this.getConfig().getProperty(key, "5");
        return Integer.valueOf(value);
    }

    public String getUsername() {
        final String key = "Robot.username";
        final String value = this.getConfig().getProperty(key);
        return value;
    }

    public String getPassword() {
        final String key = "Robot.password";
        final String value = this.getConfig().getProperty(key);
        return value;
    }

}
