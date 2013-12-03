package robot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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

	public AbstractRobot(final String host, final String setup) {
		this.host = host;
		this.log = LogFactory.getLog(this.getClass());

		InputStream inputConfig = null;
		this.session = Collections
				.synchronizedMap(new HashMap<String, Object>());
		this.config = new Properties();
		try {
			inputConfig = FileUtils.openInputStream(new File(setup));
			this.getConfig().load(inputConfig);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(inputConfig);
		}
		final String username = this.getConfig().getProperty(
				"LoginHandler.username");
		this.cookieFile = new File(username + ".cookie");
		this.httpClient = new CommonHttpClient();
		this.handlerMapping = new HashMap<String, EventHandler>();
	}

	protected void registerHandler(final String path, final EventHandler handler) {
		this.handlerMapping.put(path, handler);
	}

	@Override
	public void run() {
		this.httpClient.loadCookie(this.cookieFile);
		this.dispatch("/");
		try {
			while (this.nextHandler != null) {
				final EventHandler currEventHandler = this.nextHandler;
				this.nextHandler = null;
				currEventHandler.handle();
				this.httpClient.saveCookie(this.cookieFile);
				try {
					Thread.sleep(500);
				} catch (final InterruptedException e) {
				}
			}
		} catch (final Exception e) {
			this.log.error("发生异常退出", e);
		}
	}

	@Override
	public void dispatch(final String event) {
		this.nextHandler = this.handlerMapping.get(event);
		if (this.nextHandler == null) {
			if (this.log.isInfoEnabled()) {
				this.log.info(String.format("未知方法[%s]，返回主页", event));
			}
			this.dispatch("/mypage");
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
