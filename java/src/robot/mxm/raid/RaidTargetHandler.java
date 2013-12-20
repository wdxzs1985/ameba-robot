package robot.mxm.raid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.mxm.MxmRobot;

public class RaidTargetHandler extends AbstractRaidHandler {

	private static final Pattern TOKEN_PATTERN = Pattern
			.compile("<input type=\"hidden\" name=\"token\" value=\"([a-zA-Z0-9]{6})\">");

	public RaidTargetHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		String raidId = (String) session.get("raidId");
		String raidPirtyId = (String) session.get("raidPirtyId");
		String targetMonsterCategoryId = (String) session
				.get("targetMonsterCategoryId");

		String path = String.format("/raid/%s/%s/target/%s/choice", raidId,
				raidPirtyId, targetMonsterCategoryId);
		String html = this.httpGet(path);

		this.log.debug(html);

		this.resolveInputToken(html);

		return "/raid/attack";
	}

	private void resolveInputToken(String html) {
		final Map<String, Object> session = this.robot.getSession();
		Matcher matcher = TOKEN_PATTERN.matcher(html);
		if (matcher.find()) {
			String token = matcher.group(1);
			session.put("token", token);
		}
	}

}
