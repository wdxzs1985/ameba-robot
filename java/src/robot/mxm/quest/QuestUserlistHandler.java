package robot.mxm.quest;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public class QuestUserlistHandler extends MxmEventHandler {

	private static final Pattern USER_ROOM_PATTERN = Pattern
			.compile("/user/(\\d+)/room");

	public QuestUserlistHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		final String html = this.httpGet("/user/user_list");
		Matcher matcher = USER_ROOM_PATTERN.matcher(html);
		if (matcher.find()) {
			String userId = matcher.group(1);
			session.put("userId", userId);
			return "/quest/user/room";
		}
		return "/mypage";
	}
}
