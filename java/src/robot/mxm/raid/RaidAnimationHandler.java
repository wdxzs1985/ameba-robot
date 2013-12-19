package robot.mxm.raid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public class RaidAnimationHandler extends MxmEventHandler {

	private static final Pattern RESULT_PATTERN = Pattern
			.compile("var _json = (.*?);?_json");

	private static final Pattern NEXT_URL_PATTERN = Pattern
			.compile("_json.nextUrl = '(.*?)';");

	public RaidAnimationHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final String html = this.httpGet("/touch/raid/animation");
		if (this.log.isInfoEnabled()) {
			this.printBossInfo(html);
		}

		Matcher matcher = NEXT_URL_PATTERN.matcher(html);
		if (matcher.find()) {
			String nextUrl = matcher.group(1);
			return this.resolveNextUrl(nextUrl);
		}
		return "/mypage";
	}

	private void printBossInfo(String html) {
		Matcher matcher = RESULT_PATTERN.matcher(html);
		if (matcher.find()) {
			String jsonString = matcher.group(1);
			JSONObject data = JSONObject.fromObject(jsonString);
			String bossName = data.optString("bossName");
			int bossLevel = data.optInt("bossLevel");
			this.log.info(String.format("LV%d BOSS %s", bossName, bossLevel));
		}
	}
}
