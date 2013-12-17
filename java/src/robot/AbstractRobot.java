package robot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.CommonHttpClient;

public abstract class AbstractRobot implements Robot, Runnable {

	protected final Log log = LogFactory.getLog(this.getClass());
	private final Map<String, Object> session = Collections
			.synchronizedMap(new HashMap<String, Object>());
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
			try {
				currEventHandler.handle();
			} catch (final Exception e) {
				final String message = e.getMessage();
				this.log.error("发生异常: " + message, e);
			} finally {
				this.httpClient.saveCookie();
				this.sleep();
			}
		}
	}

	private void sleep() {
		final int requestDelay = this.getRequestDelay();
		final int sleepTime = requestDelay + RandomUtils.nextInt(requestDelay);
		try {
			Thread.sleep(sleepTime * 1000);
		} catch (final InterruptedException e) {
		}
	}

	@Override
	public void dispatch(final String event) {
		if (StringUtils.equals(event, "/exit")) {
			if (this.log.isInfoEnabled()) {
				this.log.info("exit");
			}
		} else {
			this.nextHandler = this.handlerMapping.get(event);
			if (this.nextHandler == null) {
				if (this.log.isInfoEnabled()) {
					this.log.warn(String.format("未知方法[%s]", event));
				}
			}
		}
	}

	public String buildPath(final String path) {
		final String host = this.getHost();
		return host + path;
	}

	@Override
	public Map<String, Object> getSession() {
		return this.session;
	}

	@Override
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

	@Override
	public int getRequestDelay() {
		final String key = "Robot.requestDelay";
		final String value = this.getConfig().getProperty(key, "3");
		return Integer.valueOf(value);
	}

	@Override
	public int getScheduleDelay() {
		final String key = "Robot.scheduleDelay";
		final String value = this.getConfig().getProperty(key, "5");
		return Integer.valueOf(value);
	}

	@Override
	public String getUsername() {
		final String key = "Robot.username";
		final String value = this.getConfig().getProperty(key);
		return value;
	}

	@Override
	public String getPassword() {
		final String key = "Robot.password";
		final String value = this.getConfig().getProperty(key);
		return value;
	}
}
