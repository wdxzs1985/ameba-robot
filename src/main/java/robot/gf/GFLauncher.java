package robot.gf;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import robot.gf.GFRobot;

public class GFLauncher implements CommandLineRunner {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final Log log = LogFactory.getLog(this.getClass());

    public static void main(final String[] args) {
        final SpringApplication app = new SpringApplication(GFLauncher.class);
        app.setShowBanner(false);
        app.setLogStartupInfo(false);
        app.run(args);
    }

    @Override
    public void run(final String... args) {
        final String setup = args.length > 0 ? args[0] : "setup.txt";
        if (this.log.isInfoEnabled()) {
            this.log.info(GFRobot.VERSION);
        }
        final GFRobot robot = new GFRobot();
        robot.setConfigPath(setup);
        robot.init();
        final int delay = robot.getScheduleDelay();
        this.executor.scheduleWithFixedDelay(robot, 0, delay, TimeUnit.MINUTES);
    }

}
