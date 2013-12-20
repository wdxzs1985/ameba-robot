package robot.mxm.raid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public abstract class AbstractRaidHandler extends MxmEventHandler {

	private static final Pattern TITLE_PATTERN = Pattern
			.compile("<title>レイド 勝利シーン \\| フレンダリアと魔法の指輪</title>");

	private static final Pattern RESULT_PATTERN = Pattern
			.compile("var _json = (.*?);?_json");

	public AbstractRaidHandler(final MxmRobot robot) {
		super(robot);
	}

	public boolean isRaidWin(String html) {
		Matcher matcher = TITLE_PATTERN.matcher(html);
		if (matcher.find()) {
			if (this.log.isInfoEnabled()) {
				this.printRaidPrizes(html);
			}
			return true;
		}
		return false;
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
