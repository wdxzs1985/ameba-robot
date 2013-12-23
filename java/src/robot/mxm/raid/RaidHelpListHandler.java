package robot.mxm.raid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import robot.mxm.MxmRobot;

public class RaidHelpListHandler extends AbstractRaidHandler {

    private static final Pattern JSON_PATTERN = Pattern.compile("var _json = (.*?);");

    public RaidHelpListHandler(final MxmRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidId = (String) session.get("raidId");
        final String path = String.format("/raid/%s/help/list", raidId);
        final String html = this.httpGet(path);

        final Matcher matcher = RaidHelpListHandler.JSON_PATTERN.matcher(html);
        if (matcher.find()) {
            final String jsonString = matcher.group(1);
            final JSONObject data = JSONObject.fromObject(jsonString);
            final JSONArray list = data.optJSONArray("list");
            final JSONObject raid = this.selectRaid(list);
            final String raidPirtyId = raid.optString("raidPirtyId");
            session.put("raidPirtyId", raidPirtyId);
            return "/raid/encount";
        }
        return "/mypage";
    }

    private JSONObject selectRaid(final JSONArray list) {
        JSONObject selectedRaid = null;
        final int maxJoinedMemberCount = 0;
        for (int i = 0; i < list.size(); i++) {
            final JSONObject raid = list.optJSONObject(i);
            final int joinedMemberCount = raid.optInt("joinedMemberCount");
            if (joinedMemberCount > maxJoinedMemberCount) {
                selectedRaid = raid;
            }
        }

        if (this.log.isInfoEnabled()) {
            final JSONObject user = selectedRaid.optJSONObject("user");
            final String name = user.optString("name");
            final String raidPirtyBossName = selectedRaid.optString("raidPirtyBossName");
            final int raidPirtyBossLevel = selectedRaid.optInt("raidPirtyBossLevel");
            final int raidPirtyCount = selectedRaid.optInt("raidPirtyCount");
            final int killMonsterCount = selectedRaid.optInt("killMonsterCount");
            final int maxMembers = selectedRaid.optInt("maxMembers");
            final int joinedMemberCount = selectedRaid.optInt("joinedMemberCount");

            this.log.info(String.format("来自 %s 的救援", name));
            this.log.info(String.format("%s (%d)",
                                        raidPirtyBossName,
                                        raidPirtyBossLevel));
            this.log.info(String.format("战斗人员: %d/%d",
                                        joinedMemberCount,
                                        maxMembers));
            this.log.info(String.format("怪物: %d/%d",
                                        raidPirtyCount - killMonsterCount,
                                        raidPirtyCount));
            final String lastTime = selectedRaid.optString("lastTime");
            this.log.info(String.format("last time: %s", lastTime));
        }

        return selectedRaid;
    }
}
