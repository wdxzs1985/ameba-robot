package robot.gf.cupid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class CupidExecHandler extends GFEventHandler {

	private static final Pattern POST_URL_PATTERN = Pattern
			.compile("var postUrl = \"/cupid/cupid-result\\?token=([a-zA-Z0-9]{6})\";");

	public CupidExecHandler(final GFRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		final String cupidId = (String) session.get("cupidId");
		final String gachaExecKind = (String) session.get("gachaExecKind");
		final String cupidCount = (String) session.get("cupidCount");
		String token = (String) session.get("token");
		String path = String
				.format("/cupid/cupid-exec?cupidId=%s&gachaExecKind=%s&cupidCount=%s&token=%s",
						cupidId, gachaExecKind, cupidCount, token);
		final String html = this.httpGet(path);
		Matcher postUrlMatcher = POST_URL_PATTERN.matcher(html);
		if (postUrlMatcher.find()) {
			token = postUrlMatcher.group(1);
			session.put("token", token);
			return "/cupid/result";
		}
		return "/cupid";
	}
}
