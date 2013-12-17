package robot.mxm.quest;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

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
		String dailyElement = (String) session.get("dailyElement");
		String path = String.format("/user/%s/room", userId);
		final String html = this.httpGet(path);
		this.resolveMxmToken(html);
		JSONObject userRoom = this.resloveUserRoomData(html);
		if (userRoom != null) {
			this.log.debug(userRoom);
			JSONObject tableData = userRoom.optJSONObject("tableData");
			for (int i = 1; i <= 5; i++) {
				JSONObject monster = tableData.optJSONObject(String.valueOf(i));
				if (monster != null) {
					String name = monster.optString("name");
					String summonId = monster.optString("summonId");
					String elementId = monster.optString("elementId");
					String rarityId = monster.optString("rarityId");
					if (StringUtils.equals(elementId, dailyElement)) {
						if (StringUtils.equals(rarityId, "3")) {
							this.log.info(String.format("take %s", name));
							session.put("summonId", summonId);
							return "/quest/summon";
						}
					}
				}
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
}
