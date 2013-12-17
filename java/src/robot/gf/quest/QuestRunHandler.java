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
		Map<String, Object> session = this.robot.getSession();
		String questId = (String) session.get("questId");
		String stageId = (String) session.get("stageId");
		String token = (String) session.get("token");

		String path = String.format(
				"/quest/ajax/quest-run?questId=%s&stageId=%s&token=%s",
				questId, stageId, token);
		JSONObject jsonResponse = this.httpGetJSON(path);
		this.resolveJsonToken(jsonResponse);

		JSONObject data = jsonResponse.optJSONObject("data");

		if (data.containsKey("tiredWord")) {
			if (this.log.isInfoEnabled()) {
				JSONObject tiredWord = data.getJSONObject("tiredWord");
				this.log.info("ti li bu zhi");
				this.log.info(tiredWord.optString("word"));
			}
			return "/mypage";
		}

		if (this.log.isInfoEnabled()) {
			String questName = data.optString("questName");
			this.log.info(questName);
			String stageName = data.optString("stageName");
			this.log.info(stageName);
		}

		if (data.optBoolean("newGetFlg", false)) {
			String rewardCardName = data.optString("rewardCardName");
			if (this.log.isInfoEnabled()) {
				this.log.info(String.format("new card get: %s", rewardCardName));
			}
			if (this.isCardFull(data)) {
				if (this.log.isInfoEnabled()) {
					this.log.info("card is full");
				}
				if (this.robot.isUpgradeEnable()) {
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

	private boolean isCardFull(JSONObject data) {
		int cardCount = data.optInt("cardCount", 0);
		int maxCardCount = data.optInt("maxCardCount", 0);
		return cardCount == maxCardCount;
	}

}
