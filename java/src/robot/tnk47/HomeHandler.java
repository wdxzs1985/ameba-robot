package robot.tnk47;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.AbstractEventHandler;

public class HomeHandler extends AbstractEventHandler<Tnk47Robot> {

	private static final Pattern HTML_TITLE_PATTERN = Pattern
			.compile("<title>天下統一クロニクル</title>");

	public HomeHandler(final Tnk47Robot robot) {
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
