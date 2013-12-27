package robot.mxm;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import robot.Launcher;

public class MxmLauncher extends Launcher {

    public static void main(final String[] args) {
        final String setup = args.length > 0 ? args[0] : "setup.txt";
        final MxmLauncher launcher = new MxmLauncher();
        launcher.init(setup);
        launcher.launch();
    }

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public void launch() {
        if (this.log.isInfoEnabled()) {
            this.log.info(MxmRobot.VERSION);
        }
        final MxmRobot robot = new MxmRobot();
        robot.setConfig(this.config);
        robot.setHttpClient(this.httpClient);
        robot.init();
        robot.reset();
        int delay = robot.getScheduleDelay();
        this.executor.scheduleWithFixedDelay(robot, 0, delay, TimeUnit.MINUTES);
    }
}
