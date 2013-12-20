package robot.mxm.raid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import robot.mxm.MxmRobot;

public class RaidTopHandler extends AbstractRaidHandler {

	private static final Pattern MONSTER_PATTERN = Pattern
			.compile("var _monsterData = (\\[.*\\]);");
	private static final Pattern TARGET_PATTERN = Pattern
			.compile("/raid/\\d+/\\d+/target/(\\d+)/choice");
	private static final Pattern HELP_PATTERN = Pattern
			.compile("/raid/\\d+/help/shouted");

	public RaidTopHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		String raidId = (String) session.get("raidId");
		String raidPirtyId = (String) session.get("raidPirtyId");
		String path = String.format("/raid/%s/%s/top", raidId, raidPirtyId);
		String html = this.httpGet(path);
		this.log.debug(html);

		if (this.isRaidWin(html)) {
			return "/raid/win/result";
		}

		this.shoutHelp(html);

		if (this.hasBP(html)) {
			JSONObject monster = this.getMonsterData(html);
			if (monster != null) {
				String targetMonsterCategoryId = this.chooseTarget(monster);
				if (StringUtils.isNotBlank(targetMonsterCategoryId)) {
					session.put("targetMonsterCategoryId",
							targetMonsterCategoryId);
					return "/raid/target";
				}
			}
		}

		return "/mypage";
	}

	private void shoutHelp(String html) {
		Matcher matcher = HELP_PATTERN.matcher(html);
		if (matcher.find()) {
			final Map<String, Object> session = this.robot.getSession();
			String raidId = (String) session.get("raidId");
			String path = String.format("/raid/%s/help/shout", raidId);
			this.httpGet(path);
		}
	}

	private JSONObject getMonsterData(String html) {
		List<JSONObject> monsterList = new ArrayList<JSONObject>();
		Matcher matcher = MONSTER_PATTERN.matcher(html);
		if (matcher.find()) {
			String jsonString = matcher.group(1);
			JSONArray monsterData = JSONArray.fromObject(jsonString);
			for (int i = 0; i < monsterData.size(); i++) {
				JSONObject monster = monsterData.optJSONObject(i);
				String name = monster.optString("name");
				int lv = monster.optInt("lv");
				int HP = monster.optInt("HP");
				int maxHP = monster.optInt("maxHP");
				if (HP > 0) {
					monsterList.add(monster);
					if (this.log.isInfoEnabled()) {
						if (monster.optBoolean("boss")) {
							this.log.info(String.format(
									"[Boss] %s(Lv%d) %d/%d", name, lv, HP,
									maxHP));
						} else {
							this.log.info(String.format("%s(Lv%d) %d/%d", name,
									lv, HP, maxHP));
						}
					}
				}
			}
		}
		if (CollectionUtils.isNotEmpty(monsterList)) {
			Collections.sort(monsterList, new Comparator<JSONObject>() {

				@Override
				public int compare(JSONObject monster1, JSONObject monster2) {
					int HP1 = monster1.optInt("HP");
					int HP2 = monster2.optInt("HP");

					return HP2 - HP1;
				}
			});

			return monsterList.get(0);
		}
		return null;
	}

	private String chooseTarget(JSONObject monster) {
		String url = monster.optString("url");
		Matcher matcher = TARGET_PATTERN.matcher(url);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

}
