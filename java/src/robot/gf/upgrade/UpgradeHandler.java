package robot.gf.upgrade;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class UpgradeHandler extends GFEventHandler {

	private static final Pattern STEP_PATTERN = Pattern
			.compile("var step = '(.*?)';");
	private static final Pattern BASE_CARD_ID_PATTERN = Pattern
			.compile("var userCardId = \"(.*?)\";");
	private static final Pattern USER_CARD_ID_PATTERN = Pattern
			.compile("<li class=\"listbox noteBg listId0 .*?\" rel=\"([0-9\\._]+)\">");

	public UpgradeHandler(final GFRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		final String html = this.httpGet("/upgrade");
		Matcher stepMatcher = null;
		if ((stepMatcher = UpgradeHandler.STEP_PATTERN.matcher(html)).find()) {
			final String step = stepMatcher.group(1);
			if (StringUtils.equals(step, "base")) {
				final Matcher userCardIdMatcher = UpgradeHandler.USER_CARD_ID_PATTERN
						.matcher(html);
				if (userCardIdMatcher.find()) {
					final String userCardId = userCardIdMatcher.group(1);

					final String path = "/upgrade/ajax/upgrade-card-search";
					final List<BasicNameValuePair> nvps = this
							.createNameValuePairs();
					nvps.add(new BasicNameValuePair("cond", "material"));
					nvps.add(new BasicNameValuePair("sphere", "ALL"));
					nvps.add(new BasicNameValuePair("sortType", "capability"));
					nvps.add(new BasicNameValuePair("sort", "desc"));
					nvps.add(new BasicNameValuePair("page", "1"));
					nvps.add(new BasicNameValuePair("userCardId", userCardId));
					nvps.add(new BasicNameValuePair("rarity", "0"));
					nvps.add(new BasicNameValuePair("skill", "0"));
					nvps.add(new BasicNameValuePair("status", "0"));

					this.httpPostJSON(path, nvps);
					return "/upgrade";
				}
			} else if (StringUtils.equals(step, "material")) {
				final Matcher baseUserCardIdMatcher = UpgradeHandler.BASE_CARD_ID_PATTERN
						.matcher(html);
				if (baseUserCardIdMatcher.find()) {
					final String baseUserCardId = baseUserCardIdMatcher
							.group(1);
					session.put("baseUserCardId", baseUserCardId);
					return "/upgrade/confirm";
				}
			}
		}
		return "/mypage";
	}
}
