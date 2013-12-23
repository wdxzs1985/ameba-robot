package robot.mxm.raid;

import java.util.Map;

import robot.mxm.MxmRobot;

public class RaidLoseResultHandler extends AbstractRaidHandler {

    public RaidLoseResultHandler(final MxmRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidId = (String) session.get("raidId");
        final String raidPirtyId = (String) session.get("raidPirtyId");
        final String path = String.format("/raid/%s/%s/lose/result",
                                          raidId,
                                          raidPirtyId);
        this.httpGet(path);
        if (this.log.isInfoEnabled()) {
            this.log.info("魔兽逃跑了，小伙伴还要继续努力。");
        }
        return "/raid/history";
    }
}
