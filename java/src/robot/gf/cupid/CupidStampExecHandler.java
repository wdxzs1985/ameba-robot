package robot.gf.cupid;

import java.util.Map;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class CupidStampExecHandler extends GFEventHandler {

	private static final Pattern POST_URL_PATTERN = Pattern
			.compile("var postUrl = \"/cupid/stamp-result\";");

	public CupidStampExecHandler(final GFRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		String token = (String) session.get("token");
		String path = String.format("/cupid/stamp-exec?token=%s", token);
		this.httpGet(path);
		return "/cupid/stamp/result";
	}
}
