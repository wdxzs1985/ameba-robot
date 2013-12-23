package robot.mxm.raid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import robot.mxm.MxmRobot;

public class RaidTopHandler extends AbstractRaidHandler {

    private static final Pattern HELP_PATTERN = Pattern.compile("/raid/\\d+/help/shouted");

    public RaidTopHandler(final MxmRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidId = (String) session.get("raidId");
        final String raidPirtyId = (String) session.get("raidPirtyId");
        final String path = String.format("/raid/%s/%s/top",
                                          raidId,
                                          raidPirtyId);
        final String html = this.httpGet(path);

        if (this.isRaidWin(html)) {
            return "/raid/win/result";
        } else if (this.isRaidLose(html)) {
            return "/raid/lose/result";
        }

        this.shoutHelp(html);

        final JSONObject monster = this.findAttackMonster(html);
        if (monster != null && this.getBpCount(html) > 0) {
            final String targetMonsterCategoryId = this.chooseTarget(monster);
            if (StringUtils.isNotBlank(targetMonsterCategoryId)) {
                session.put("targetMonsterCategoryId", targetMonsterCategoryId);
                return "/raid/target";
            }
        }

        return "/mypage";
    }

    private void shoutHelp(final String html) {
        final Matcher matcher = RaidTopHandler.HELP_PATTERN.matcher(html);
        if (matcher.find()) {
            final Map<String, Object> session = this.robot.getSession();
            final String raidId = (String) session.get("raidId");
            final String path = String.format("/raid/%s/help/shouted", raidId);
            this.httpGet(path);
            if (this.log.isInfoEnabled()) {
                this.log.info("给小伙伴们发了情报。");
            }
        }
    }

}
