package robot.mxm.raid;

import java.util.Map;

import robot.mxm.MxmRobot;
import robot.mxm.convert.EventPointPrinter;

public class RaidWinResultHandler extends AbstractRaidHandler {

    public RaidWinResultHandler(final MxmRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidId = (String) session.get("raidId");
        final String raidPirtyId = (String) session.get("raidPirtyId");
        final String path = String.format("/raid/%s/%s/win/result",
                                          raidId,
                                          raidPirtyId);
        final String html = this.httpGet(path);
        if (this.log.isInfoEnabled()) {
            EventPointPrinter.printPoint(this.log, html);
            EventPointPrinter.printRanking(this.log, html);
            EventPointPrinter.printTreature(this.log, html);
        }
        return "/raid/history";
    }
}
