package robot.gf.upgrade;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class UpgradeAnimationHandler extends GFEventHandler {

	private static final Pattern CARD_NAME_PATTERN = Pattern
			.compile("var cardName = \"(.*?)\";");
	private static final Pattern CARD_LEVEL_PATTERN = Pattern
			.compile("var cardAfterLevelNumber = (\\d+);");

	public UpgradeAnimationHandler(final GFRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		Map<String, Object> session = this.robot.getSession();
		String baseUserCardId = (String) session.get("baseUserCardId");
		String materialUserCardId = (String) session.get("materialUserCardId");
		String token = (String) session.get("token");
		String path = String
				.format("/upgrade/upgrade-animation?baseUserCardId=%s&materialUserCardId=%s&token=%s",
						baseUserCardId, materialUserCardId, token);
		final String html = this.httpGet(path);

		if (this.log.isInfoEnabled()) {
			String cardName = null;
			String cardLevel = null;
			Matcher matcher = null;
			if ((matcher = CARD_NAME_PATTERN.matcher(html)).find()) {
				cardName = matcher.group(1);
			}
			if ((matcher = CARD_LEVEL_PATTERN.matcher(html)).find()) {
				cardLevel = matcher.group(1);
			}
			this.log.info(String.format("%s upgrade to Lv%s", cardName,
					cardLevel));
		}
		return "/upgrade";
	}
}
