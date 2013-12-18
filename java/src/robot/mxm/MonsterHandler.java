package robot.mxm;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MonsterHandler extends MxmEventHandler {

	private static final Pattern MONSTER_TYPE_PATTERN = Pattern
			.compile("<span class=\"iconAttr type(\\d)\">(.*?)</span>");

	public MonsterHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		final String html = this.httpGet("/user/monsters");
		this.log.debug(html);

		Matcher matcher = MONSTER_TYPE_PATTERN.matcher(html);
		if (matcher.find()) {
			String monsterType = matcher.group(1);
			session.put("monsterType", monsterType);
		}

		return "/mypage";
	}
}
