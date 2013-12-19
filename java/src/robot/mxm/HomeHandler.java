package robot.mxm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.AbstractEventHandler;

public class HomeHandler extends AbstractEventHandler<MxmRobot> {

	private static final Pattern HTML_TITLE_PATTERN = Pattern
			.compile("<title>トップ | フレンダリアと魔法の指輪</title>");

	public HomeHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final String html = this.httpGet("/");
		final Matcher matcher = HomeHandler.HTML_TITLE_PATTERN.matcher(html);
		if (matcher.find()) {
			return ("/mypage");
		} else {
			return "/login";
		}
	}
}
