package robot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import robot.tnk47.Tnk47Robot;

import common.CommonHttpClient;

public class MainApplication {

	public static void main(final String[] args) {
		final String setup = args.length > 0 ? args[0] : "setup.txt";
		MainApplication app = new MainApplication();
		app.init(setup);
		app.runTnk47Robot();
	}

	private final ExecutorService executor = Executors.newFixedThreadPool(1);
	private final Properties config = new Properties();
	private final CommonHttpClient httpClient = new CommonHttpClient();

	private final Log log = LogFactory.getLog(this.getClass());

	private void init(String setup) {
		this.initConfig(setup);
		this.initHttpClient(setup);
	}

	private void initConfig(String setup) {
		InputStream inputConfig = null;
		try {
			inputConfig = FileUtils.openInputStream(new File(setup));
			this.config.load(inputConfig);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(inputConfig);
		}
	}

	private void initHttpClient(String setup) {
		final String username = this.config
				.getProperty("MainApplication.username");
		File cookieFile = new File(username + ".cookie");
		this.httpClient.loadCookie(cookieFile);
	}

	protected void runTnk47Robot() {
		Tnk47Robot robot = new Tnk47Robot();
		robot.setConfig(this.config);
		robot.setHttpClient(this.httpClient);
		robot.init();
		this.executor.execute(robot);
	}
}
