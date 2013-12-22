package robot.mxm.raid;

import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import robot.mxm.MxmRobot;

public class RaidEncountHandler extends AbstractRaidHandler {

    public RaidEncountHandler(final MxmRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidId = (String) session.get("raidId");
        final String raidPirtyId = (String) session.get("raidPirtyId");
        final String path = String.format("/raid/%s/%s/encount",
                                          raidId,
                                          raidPirtyId);
        final String html = this.httpGet(path);

        if (this.isRaidWin(html)) {
            return "/raid/win/result";
        }

        final JSONObject monster = this.findAttackMonster(html);
        if (monster != null) {
            final String targetMonsterCategoryId = this.chooseTarget(monster);
            if (StringUtils.isNotBlank(targetMonsterCategoryId)) {
                session.put("targetMonsterCategoryId", targetMonsterCategoryId);
                return "/raid/target";
            }
        }
        return "/mypage";
    }

}
