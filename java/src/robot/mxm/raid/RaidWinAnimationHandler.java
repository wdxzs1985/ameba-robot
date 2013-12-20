package robot.mxm.raid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.mxm.MxmRobot;

public class RaidWinAnimationHandler extends AbstractRaidHandler {

	private static final Pattern NEXT_URL_PATTERN = Pattern
			.compile("_json.nextUrl = \"(.*?)\";");

	public RaidWinAnimationHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		String raidId = (String) session.get("raidId");
		String raidPirtyId = (String) session.get("raidPirtyId");
		String path = String.format("/raid/%s/%s/win/animation", raidId,
				raidPirtyId);
		final String html = this.httpGet(path);

		Matcher matcher = NEXT_URL_PATTERN.matcher(html);
		if (matcher.find()) {
			String url = matcher.group(1);
			return this.resolveNextUrl(url);
		}
		return "/raid/history";
	}

}
