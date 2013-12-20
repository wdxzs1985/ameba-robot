package robot.mxm.raid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public class RaidWinAnimationHandler extends MxmEventHandler {

	private static final Pattern RESULT_PATTERN = Pattern
			.compile("var _json = (.*?);?_json");

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

		if (this.log.isInfoEnabled()) {
			this.printRaidPrizes(html);
		}

		Matcher matcher = NEXT_URL_PATTERN.matcher(html);
		if (matcher.find()) {
			String url = matcher.group(1);
			return this.resolveNextUrl(url);
		}
		return "/raid/history";
	}

	private void printRaidPrizes(String html) {
		Matcher matcher = RESULT_PATTERN.matcher(html);
		if (matcher.find()) {
			String jsonString = matcher.group(1);
			JSONObject data = JSONObject.fromObject(jsonString);
			JSONArray prizes = data.optJSONArray("prizes");
			if (prizes != null) {
				for (int i = 0; i < prizes.size(); i++) {
					JSONObject prize = prizes.optJSONObject(i);
					this.printPrizeInfo(prize);
				}
			}
			JSONObject medalPrize = data.optJSONObject("medalPrize");
			if (medalPrize != null) {
				this.printPrizeInfo(medalPrize);
			}
		}
	}

	private void printPrizeInfo(JSONObject prize) {
		String name = prize.optString("name");
		int amount = prize.optInt("amount");
		this.log.info(String.format("GET %s %d 個", name, amount));
	}
}
