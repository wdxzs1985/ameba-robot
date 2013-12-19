package robot.mxm.quest;

import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public class QuestUserRoomHandler extends MxmEventHandler {

	private static final Pattern USER_ROOM_DATA_PATTERN = Pattern
			.compile("new mxm.UserRoom\\((.*?)\\);");

	public QuestUserRoomHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		String userId = (String) session.get("userId");
		String element = (String) session.get("element");
		String path = String.format("/user/%s/room", userId);
		final String html = this.httpGet(path);
		this.resolveMxmToken(html);
		JSONObject userRoom = this.resloveUserRoomData(html);
		if (userRoom != null && userRoom.optBoolean("hasTableMonster")) {
			JSONObject tableData = userRoom.optJSONObject("tableData");
			JSONObject monster = this.findSummon(tableData, element);
			if (monster != null) {
				String summonId = monster.optString("summonId");
				session.put("summonId", summonId);
				if (this.log.isInfoEnabled()) {
					String name = monster.optString("name");
					this.log.info(String.format("take %s", name));
				}
				return "/quest/summon";
			}
		}
		return "/quest/user/list";
	}

	private JSONObject resloveUserRoomData(String html) {
		Matcher matcher = USER_ROOM_DATA_PATTERN.matcher(html);
		if (matcher.find()) {
			String data = matcher.group(1);
			return JSONObject.fromObject(data);
		}
		return null;
	}

	private JSONObject findSummon(JSONObject tableData, String element) {
		LinkedList<JSONObject> summonList = new LinkedList<JSONObject>();
		for (int i = 1; i <= 5; i++) {
			JSONObject monster = tableData.optJSONObject(String.valueOf(i));
			if (monster != null) {
				String elementId = monster.optString("elementId");
				String rarityId = monster.optString("rarityId");
				if (StringUtils.equals(element, "")) {
					if (StringUtils.equals(rarityId, "1")) {
						// 精霊の場合
						summonList.addFirst(monster);
					} else if (StringUtils.equals(rarityId, "3")) {
						// 神獣の場合
						summonList.addLast(monster);
					} else if (StringUtils.equals(rarityId, "2")) {
						// 幻獣の場合
						summonList.addLast(monster);
					}
				} else {
					if (StringUtils.equals(elementId, element)) {
						if (StringUtils.equals(rarityId, "3")) {
							// 神獣の場合
							summonList.addFirst(monster);
						} else if (StringUtils.equals(rarityId, "2")) {
							// 幻獣の場合
							summonList.addLast(monster);
						}
					}
				}
			}
		}
		if (CollectionUtils.isNotEmpty(summonList)) {
			return summonList.getFirst();
		}
		return null;
	}
}
