package robot.gf;

import java.util.concurrent.TimeUnit;

import robot.Launcher;

public class GFLauncher extends Launcher {

	public static void main(final String[] args) {
		final String setup = args.length > 0 ? args[0] : "setup.txt";
		final GFLauncher launcher = new GFLauncher();
		launcher.init(setup);
		launcher.launch();
	}

	public void launch() {
		if (this.log.isInfoEnabled()) {
			this.log.info(GFRobot.VERSION);
		}
		final GFRobot robot = new GFRobot();
		robot.setConfig(this.config);
		robot.setHttpClient(this.httpClient);
		robot.init();
		final int delay = robot.getScheduleDelay();
		this.executor.scheduleWithFixedDelay(robot, 0, delay, TimeUnit.MINUTES);
	}

}
