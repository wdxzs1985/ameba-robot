package robot.tnk47;

import robot.Launcher;

public class Tnk47Launcher extends Launcher {

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
		this.executor.execute(robot);
	}
}
