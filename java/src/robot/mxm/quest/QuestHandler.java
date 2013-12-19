package robot.mxm.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public class QuestHandler extends MxmEventHandler {

	private static final Pattern DAILY_ELEMENT_PATTERN = Pattern
			.compile("http://stat100.ameba.jp/mxm/ver01/page/img/orgn/daily_mission/icon_daily_element([\\d]).png");
	private static final Pattern DAILY_CLEAR_PATTERN = Pattern
			.compile("クリアまであと<span class=\"colorDeepOrange\">([1-5])回！</span>");
	private static final Pattern TABLE_DATA_PATTERN = Pattern
			.compile("mxm.tableData\\[\"(\\d)\"\\] = (\\{.*?\\})");
	private static final Pattern SUMMON_PATTERN = Pattern
			.compile("<li data-btn=\"push\" data-summon-point=\"(\\d)\" data-max-sumon-point=\"(\\d)\" data-iusermonster-id=\"(\\d+)\" data-index=\"(\\d)\" data-recipe-id=\"(\\d)\" data-mst-name=\"(.*?)\" data-mst-path=\"(.*?)\" data-rarity-id=\"(\\d)\" data-element-id=\"(\\d)\">");

	public QuestHandler(final MxmRobot robot) {
		super(robot);
		this.reset();
	}

	@Override
	public String handleIt() {
		final String html = this.httpGet("/mypage");
		this.resolveMxmToken(html);
		if (!this.hasMonsterType()) {
			return "/monster";
		}
		this.setDailyElement(html);
		this.summon(html);

		return "/quest/user/list";
	}

	private void summon(String html) {
		List<String> positionList = this.findPosition(html);
		List<String> summonList = this.findSummonList(html);
		for (String position : positionList) {
			if (CollectionUtils.isEmpty(summonList)) {
				break;
			}
			String[] summon = StringUtils.split(summonList.remove(0), ",");
			this.summon(position, summon);
		}
	}

	private void summon(String position, String[] summon) {
		String monsterId = summon[0];
		String name = summon[1];

		String path = "/summon/summon";
		List<BasicNameValuePair> nvps = this.createNameValuePairs();
		nvps.add(new BasicNameValuePair("monsterId", monsterId));
		nvps.add(new BasicNameValuePair("position", position));

		JSONObject jsonResponse = this.httpPostJSON(path, nvps);
		this.resolveJsonToken(jsonResponse);
		if (this.log.isInfoEnabled()) {
			JSONObject summonParameter = jsonResponse
					.optJSONObject("summonParameter");
			int beforeRingExperience = summonParameter
					.optInt("beforeRingExperience");
			int afterRingExperience = summonParameter
					.optInt("afterRingExperience");
			this.log.info(String.format("exp : %d > %d", beforeRingExperience,
					afterRingExperience));
		}
	}

	private List<String> findPosition(String html) {
		Map<String, Boolean> positionMap = new HashMap<String, Boolean>();
		positionMap.put("1", true);
		positionMap.put("2", true);
		positionMap.put("3", true);
		positionMap.put("4", true);
		positionMap.put("5", true);

		Matcher matcher = TABLE_DATA_PATTERN.matcher(html);
		while (matcher.find()) {
			String position = matcher.group(1);
			String tableDataString = matcher.group(2);
			JSONObject monster = JSONObject.fromObject(tableDataString);
			String treasurePath = monster.optString("treasurePath");
			if (StringUtils.isBlank(treasurePath)) {
				positionMap.remove(position);
			} else {
				String summonId = monster.optString("summonId");
				this.clearSummon(summonId);
			}
		}
		return new ArrayList<String>(positionMap.keySet());
	}

	private List<String> findSummonList(String html) {
		List<String> summonList = new ArrayList<String>();
		Matcher matcher = SUMMON_PATTERN.matcher(html);
		while (matcher.find()) {
			String recipeId = matcher.group(5);
			String mstName = matcher.group(6);
			int summonPoint = Integer.valueOf(matcher.group(1));
			for (int i = 0; i < summonPoint; i++) {
				summonList.add(String.format("%s,%s", recipeId, mstName));
			}
		}
		return summonList;
	}

	private void clearSummon(String summonId) {
		String path = "/summon/clear";
		List<BasicNameValuePair> nvps = this.createNameValuePairs();
		nvps.add(new BasicNameValuePair("summonId", summonId));
		JSONObject jsonResponse = this.httpPostJSON(path, nvps);
		this.resolveJsonToken(jsonResponse);

		if (this.log.isInfoEnabled()) {
			JSONObject crystalParameter = jsonResponse
					.optJSONObject("crystalParameter");
			if (crystalParameter != null) {
				int beforeCrystal = crystalParameter.optInt("beforeCrystal");
				int afterCrystal = crystalParameter.optInt("afterCrystal");
				this.log.info(String.format("get crystal: %d > %d",
						beforeCrystal, afterCrystal));
			}
			JSONObject getTreasureDto = jsonResponse
					.optJSONObject("getTreasureDto");
			if (getTreasureDto != null) {
				JSONObject treasureDto = getTreasureDto
						.optJSONObject("treasureDto");
				if (treasureDto != null) {
					String name = treasureDto.optString("name");
					this.log.info(String.format("get: %s", name));
				}
				JSONObject userExplorerDto = getTreasureDto
						.optJSONObject("userExplorerDto");
				if (userExplorerDto != null) {
					int beforePoint = userExplorerDto.optInt("beforePoint");
					int currentPoint = userExplorerDto.optInt("currentPoint");
					this.log.info(String.format("point: %d > %d", beforePoint,
							currentPoint));

					int beforeRank = userExplorerDto.optInt("beforeRank");
					int currentRank = userExplorerDto.optInt("currentRank");
					this.log.info(String.format("rank: %d > %d", beforeRank,
							currentRank));
				}
			}
		}
	}

	private boolean hasMonsterType() {
		final Map<String, Object> session = this.robot.getSession();
		return session.containsKey("leaderType");
	}

	private void setDailyElement(String html) {
		final Map<String, Object> session = this.robot.getSession();
		String element = null;
		if (this.isDailyClear(html)) {
			element = (String) session.get("leaderType");
		} else {
			element = this.findDailyElement(html);
		}
		session.put("element", element);
	}

	private boolean isDailyClear(String html) {
		Matcher matcher = DAILY_CLEAR_PATTERN.matcher(html);
		if (matcher.find()) {
			String times = matcher.group(1);
			if (this.log.isInfoEnabled()) {
				this.log.info(String.format("今日の召喚獣をクリアまであと%s回！", times));
			}
			return false;
		} else {
			if (this.log.isInfoEnabled()) {
				this.log.info("今日の召喚獣はクリア済みです！");
			}
			return true;
		}
	}

	private String findDailyElement(String html) {
		String element = null;
		Matcher matcher = DAILY_ELEMENT_PATTERN.matcher(html);
		if (matcher.find()) {
			element = matcher.group(1);
			if (this.log.isInfoEnabled()) {
				if (StringUtils.equals("1", element)) {
					this.log.info("今日の召喚獣は火です。");
				} else if (StringUtils.equals("2", element)) {
					this.log.info("今日の召喚獣は水です。");
				} else if (StringUtils.equals("3", element)) {
					this.log.info("今日の召喚獣は木です。");
				} else if (StringUtils.equals("4", element)) {
					this.log.info("今日の召喚獣は雷です。");
				} else if (StringUtils.equals("5", element)) {
					this.log.info("今日の召喚獣は風です。");
				} else if (StringUtils.equals("6", element)) {
					this.log.info("今日の召喚獣は土です。");
				} else {
					this.log.info("今日の召喚獣は精霊です。");
					element = "";
				}
			}
		}
		return element;
	}

	private void reset() {
		final Map<String, Object> session = this.robot.getSession();
		session.put("isMypage", false);
		session.put("isQuestEnable", this.robot.isQuestEnable());
	}
}
