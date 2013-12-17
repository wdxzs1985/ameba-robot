package robot.gf.cupid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class CupidStampResultHandler extends GFEventHandler {

	private static final Pattern CARD_NAME_PATTERN = Pattern
			.compile("<input id=\"memberName\" type=\"hidden\" value=\"(.*?)\"/>");

	public CupidStampResultHandler(final GFRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final String html = this.httpGet("/cupid/stamp-result");
		Matcher cardNameMatcher = CARD_NAME_PATTERN.matcher(html);
		while (cardNameMatcher.find()) {
			String cardName = cardNameMatcher.group(1);
			if (this.log.isInfoEnabled()) {
				this.log.info(String.format("get card: %s", cardName));
			}
		}
		return "/mypage";
	}
}
