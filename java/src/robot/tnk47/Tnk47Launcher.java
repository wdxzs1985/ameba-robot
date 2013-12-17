package robot.tnk47;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import robot.Launcher;

public class Tnk47Launcher extends Launcher {

	private final ScheduledExecutorService executor = Executors
			.newScheduledThreadPool(1);
	private final Log log = LogFactory.getLog(this.getClass());

	public static void main(final String[] args) {
		final String setup = args.length > 0 ? args[0] : "setup.txt";
		final Tnk47Launcher launcher = new Tnk47Launcher();
		launcher.init(setup);
		launcher.launch();
	}

	public void launch() {
		if (this.log.isInfoEnabled()) {
			this.log.info(Tnk47Robot.VERSION);
		}
		final Tnk47Robot robot = new Tnk47Robot();
		robot.setConfig(this.config);
		robot.setHttpClient(this.httpClient);
		robot.init();

		int delay = robot.getScheduleDelay();
		this.executor.scheduleWithFixedDelay(robot, 0, delay, TimeUnit.MINUTES);
	}
}
