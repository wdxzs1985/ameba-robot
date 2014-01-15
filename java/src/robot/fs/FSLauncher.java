package robot.fs;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import robot.Launcher;

public class FSLauncher extends Launcher {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final Log log = LogFactory.getLog(this.getClass());

    public static void main(final String[] args) {
        final String setup = args.length > 0 ? args[0] : "setup.txt";
        final FSLauncher launcher = new FSLauncher();
        launcher.init(setup);
        launcher.launch();
    }

    public void launch() {
        if (this.log.isInfoEnabled()) {
            this.log.info(FSRobot.VERSION);
        }
        final FSRobot robot = new FSRobot();
        robot.setConfig(this.config);
        robot.setHttpClient(this.httpClient);
        robot.init();
        robot.reset();
        final int delay = robot.getScheduleDelay();
        this.executor.scheduleWithFixedDelay(robot, 0, delay, TimeUnit.MINUTES);
    }

}
