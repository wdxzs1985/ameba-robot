package robot.mxm.quest;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public class QuestSummonHandler extends MxmEventHandler {

	private static final Pattern QUEST_DATA_PATTERN = Pattern
			.compile("new mxm.Quest530\\((.*?)\\);");

	public QuestSummonHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		String userId = (String) session.get("userId");
		String summonId = (String) session.get("summonId");
		String token = (String) session.get("token");

		String path = String.format("/touch_summon/%s/%s/update", userId,
				summonId);
		List<BasicNameValuePair> nvps = this.createNameValuePairs();
		nvps.add(new BasicNameValuePair("token", token));
		final String html = this.httpPost(path, nvps);
		JSONObject data = this.resloveQuestData(html);
		if (data != null) {
			this.log.debug(data);
			this.resolveJsonToken(data);
			if (data.optBoolean("bpRecovered", false)) {
				if (this.log.isInfoEnabled()) {
					this.log.info("BP +1");
				}
			}

			JSONObject experienceParam = data.optJSONObject("experienceParam");
			if (experienceParam != null) {
				this.log.debug(experienceParam);
				if (experienceParam.optBoolean("levelUp", false)) {
					int beforeLv = experienceParam.optInt("beforeLv");
					int afterLv = experienceParam.optInt("afterLv");
					if (this.log.isInfoEnabled()) {
						this.log.info(String.format("Level Up: %d > %d",
								beforeLv, afterLv));
					}
					if (experienceParam.optBoolean("reachMaxLevel", false)) {
						if (this.log.isInfoEnabled()) {
							this.log.info("Max Level");
						}
						return "/monster";
					}
				}
			}

			String redirectType = data.optString("redirectType");
			if (StringUtils.equals("RAID", redirectType)) {
				return "/raid/animation";
			} else if (StringUtils.equals("TOUCH_RESULT", redirectType)) {
				return "/quest/result";
			} else if (StringUtils.equals("RING_GET", redirectType)) {
				return "/quest/getRing";
			} else if (StringUtils.equals("STAGE_CLEAR", redirectType)) {
				return "/quest/stageClear";
			} else {
				this.log.debug(redirectType);
			}

			if (data.optBoolean("noFatigue", false)) {
				if (this.log.isInfoEnabled()) {
					this.log.info("ti li bu zhi");
				}
				return "/mypage";
			}
		}
		return "/quest";
	}

	private JSONObject resloveQuestData(String html) {
		Matcher matcher = QUEST_DATA_PATTERN.matcher(html);
		if (matcher.find()) {
			String data = matcher.group(1);
			return JSONObject.fromObject(data);
		}
		return null;
	}
}
