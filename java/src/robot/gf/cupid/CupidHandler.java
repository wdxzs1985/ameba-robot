package robot.gf.cupid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class CupidHandler extends GFEventHandler {

	// free cupid
	private static final Pattern FREE_CUPID_PATTERN = Pattern
			.compile("/cupid/cupid-exec\\?cupidId=(\\d+)&gachaExecKind=(.*?)&token=([a-zA-Z0-9]{6})");

	private static final Pattern NORMAL_CUPID_PATTERN = Pattern
			.compile("/cupid/cupid-exec\\?cupidId=(\\d+)&cupidCount=(\\d+)&token=([a-zA-Z0-9]{6})");

	public CupidHandler(final GFRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		Map<String, Object> session = this.robot.getSession();
		final String html = this.httpGet("/cupid");

		Matcher matcher = null;
		if ((matcher = FREE_CUPID_PATTERN.matcher(html)).find()) {
			String cupidId = matcher.group(1);
			String gachaExecKind = matcher.group(2);
			String token = matcher.group(3);
			session.put("cupidId", cupidId);
			session.put("gachaExecKind", gachaExecKind);
			session.put("token", token);
			session.put("cupidCount", "");
			return "/cupid/exec";
		}
		if ((matcher = NORMAL_CUPID_PATTERN.matcher(html)).find()) {
			String cupidId = matcher.group(1);
			String cupidCount = matcher.group(2);
			String token = matcher.group(3);
			session.put("cupidId", cupidId);
			session.put("cupidCount", cupidCount);
			session.put("token", token);
			session.put("gachaExecKind", "");
			return "/cupid/exec";
		}
		return "/mypage";
	}
}
