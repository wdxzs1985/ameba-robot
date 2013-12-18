package robot.gf.quest;

import java.util.Map;

import net.sf.json.JSONObject;
import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class QuestRunHandler extends GFEventHandler {

	public QuestRunHandler(final GFRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		final String questId = (String) session.get("questId");
		final String stageId = (String) session.get("stageId");
		final String token = (String) session.get("token");

		final String path = String.format(
				"/quest/ajax/quest-run?questId=%s&stageId=%s&token=%s",
				questId, stageId, token);
		final JSONObject jsonResponse = this.httpGetJSON(path);
		this.resolveJsonToken(jsonResponse);

		if (this.log.isDebugEnabled()) {
			this.log.debug(jsonResponse);
		}
		final JSONObject data = jsonResponse.optJSONObject("data");

		if (data.containsKey("tiredWord")) {
			if (this.log.isInfoEnabled()) {
				this.log.info("精尽人亡");
			}
			return "/mypage";
		}

		if (this.log.isInfoEnabled()) {
			final String questName = data.optString("questName");
			final String stageName = data.optString("stageName");
			final String afterProgress = data.optString("afterProgress");
			this.log.info(String.format("%s / %s (%s%%)", questName, stageName,
					afterProgress));
		}

		if (data.optBoolean("newGetFlg", false)) {
			final String rewardCardName = data.optString("rewardCardName");
			if (this.log.isInfoEnabled()) {
				this.log.info(String.format("发现新妹纸: %s", rewardCardName));
			}
			if (this.isCardFull(data)) {
				if (this.log.isInfoEnabled()) {
					this.log.info("后宫里的妹子满出来了");
				}
				if (this.robot.isUpgradeEnable()) {
					session.put("isQuestEnable", true);
					return "/upgrade";
				}
			}
		}

		if (data.optBoolean("questClear", false)) {
			if (this.log.isInfoEnabled()) {
				this.log.info("quest Clear");
			}
			return "/quest";
		}

		if (data.optBoolean("stageClear", false)) {
			if (this.log.isInfoEnabled()) {
				this.log.info("stage Clear");
			}
			return "/quest";
		}

		return "/quest/run";
	}

	private boolean isCardFull(final JSONObject data) {
		final int cardCount = data.optInt("cardCount", 0);
		final int maxCardCount = data.optInt("maxCardCount", 0);
		return cardCount == maxCardCount;
	}

}
