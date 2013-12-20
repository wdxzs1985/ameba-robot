package robot.mxm.raid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import robot.mxm.MxmRobot;

public class RaidHelpListHandler extends AbstractRaidHandler {

	private static final Pattern JSON_PATTERN = Pattern
			.compile("var _json = (.*?);");

	public RaidHelpListHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		String raidId = (String) session.get("raidId");
		String path = String.format("/raid/%s/help/list", raidId);
		String html = this.httpGet(path);
		// TODO
		this.log.debug(html);

		Matcher matcher = JSON_PATTERN.matcher(html);
		if (matcher.find()) {
			String jsonString = matcher.group(1);
			JSONObject data = JSONObject.fromObject(jsonString);
			JSONArray list = data.optJSONArray("list");
			JSONObject raid = this.selectRaid(list);
			String raidPirtyId = raid.optString("raidPirtyId");
			session.put("raidPirtyId", raidPirtyId);
			return "/raid/encount";
		}
		return "/mypage";
	}

	private JSONObject selectRaid(JSONArray list) {
		JSONObject selectedRaid = null;
		int maxJoinedMemberCount = 0;
		for (int i = 0; i < list.size(); i++) {
			JSONObject raid = list.optJSONObject(i);
			int joinedMemberCount = raid.optInt("joinedMemberCount");
			if (joinedMemberCount > maxJoinedMemberCount) {
				selectedRaid = raid;
			}
		}

		if (this.log.isInfoEnabled()) {
			JSONObject user = selectedRaid.optJSONObject("user");
			String name = user.optString("name");
			String raidPirtyBossName = selectedRaid
					.optString("raidPirtyBossName");
			int raidPirtyBossLevel = selectedRaid.optInt("raidPirtyBossLevel");
			int raidPirtyCount = selectedRaid.optInt("raidPirtyCount");
			int killMonsterCount = selectedRaid.optInt("killMonsterCount");
			int maxMembers = selectedRaid.optInt("maxMembers");
			int joinedMemberCount = selectedRaid.optInt("joinedMemberCount");

			this.log.info(String.format("Help from %s.", name));
			this.log.info(String.format("%s (%d)", raidPirtyBossName,
					raidPirtyBossLevel));
			this.log.info(String.format("members: %d/%d", joinedMemberCount,
					maxMembers));
			this.log.info(String.format("monster: %d/%d", killMonsterCount,
					raidPirtyCount));
			this.log.info(String.format("%s (%d)", raidPirtyBossName,
					raidPirtyBossLevel));

			String lastTime = selectedRaid.optString("lastTime");
			this.log.info(String.format("last time: %s", lastTime));
		}

		return selectedRaid;
	}
}
