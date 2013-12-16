package robot.gf.cupid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class CupidResultHandler extends GFEventHandler {

	private static final Pattern CARD_NAME_PATTERN = Pattern
			.compile("<input id=\"memberName\" type=\"hidden\" value=\"(.*?)\"/>");

	public CupidResultHandler(final GFRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		final String token = (String) session.get("token");
		final String html = this.httpGet(String.format(
				"/cupid/cupid-result?token=%s", token));
		Matcher cardNameMatcher = CARD_NAME_PATTERN.matcher(html);
		while (cardNameMatcher.find()) {
			String cardName = cardNameMatcher.group(1);
			if (this.log.isInfoEnabled()) {
				this.log.info(String.format("get card: %s", cardName));
			}
		}
		return "/cupid";
	}
}
