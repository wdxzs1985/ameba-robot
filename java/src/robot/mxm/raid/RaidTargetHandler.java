package robot.mxm.raid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.mxm.MxmRobot;

public class RaidTargetHandler extends AbstractRaidHandler {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("<input type=\"hidden\" name=\"token\" value=\"([a-zA-Z0-9]{6})\">");

    public RaidTargetHandler(final MxmRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidId = (String) session.get("raidId");
        final String raidPirtyId = (String) session.get("raidPirtyId");
        final String targetMonsterCategoryId = (String) session.get("targetMonsterCategoryId");

        final String path = String.format("/raid/%s/%s/target/%s/choice",
                                          raidId,
                                          raidPirtyId,
                                          targetMonsterCategoryId);
        final String html = this.httpGet(path);

        this.resolveInputToken(html);

        return "/raid/attack";
    }

    private void resolveInputToken(final String html) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher matcher = RaidTargetHandler.TOKEN_PATTERN.matcher(html);
        if (matcher.find()) {
            final String token = matcher.group(1);
            session.put("token", token);
        }
    }

}
