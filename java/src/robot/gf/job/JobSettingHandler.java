package robot.gf.job;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class JobSettingHandler extends GFEventHandler {

	private static final Pattern USER_CARD_ID_PATTERN = Pattern
			.compile("<li class=\"clickableArea listbox noteBg listId0 .*\" rel=\"([0-9\\._]+)\">");

	public JobSettingHandler(final GFRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		Map<String, Object> session = this.robot.getSession();
		final String html = this.httpGet("/job/job-card-setting");
		Matcher userCardIdMatcher = USER_CARD_ID_PATTERN.matcher(html);
		if (userCardIdMatcher.find()) {
			String userCardId = userCardIdMatcher.group(1);
			session.put("userCardId", userCardId);
			return "/job/start";
		}
		return "/mypage";
	}
}
