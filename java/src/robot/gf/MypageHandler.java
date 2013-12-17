package robot.gf;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MypageHandler extends GFEventHandler {

	private static final Pattern HTML_TITLE_PATTERN = Pattern
			.compile("<title>(.*?)</title>");
	private static final Pattern HTML_USER_NAME_PATTERN = Pattern
			.compile("<h1><a href=\"/profile\">(.*?)</a></h1>");

	public MypageHandler(final GFRobot robot) {
		super(robot);
		this.reset();
	}

	private void reset() {
		final Map<String, Object> session = this.robot.getSession();
		session.put("isMypage", false);
		session.put("isUpgradeEnable", this.robot.isUpgradeEnable());
		session.put("isCupidEnable", this.robot.isCupidEnable());
		session.put("isGiftEnable", this.robot.isGiftEnable());
		session.put("isQuestEnable", this.robot.isQuestEnable());
		session.put("isBattleEnable", this.robot.isBattleEnable());
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		final String html = this.httpGet("/mypage");
		if (this.log.isDebugEnabled()) {
			this.log.debug(html);
		}
		if (!this.is("isMypage")) {
			final Matcher userNameMatcher = MypageHandler.HTML_USER_NAME_PATTERN
					.matcher(html);
			if (userNameMatcher.find()) {
				final String userName = userNameMatcher.group(1);
				this.log.info(String.format("角色： %s", userName));
				session.put("isMypage", true);
			} else {
				if (this.log.isInfoEnabled()) {
					final Matcher titleMatcher = MypageHandler.HTML_TITLE_PATTERN
							.matcher(html);
					if (titleMatcher.find()) {
						final String title = titleMatcher.group(1);
						this.log.info(title);
					}
				}
				return "/mypage";
			}
		}

		if (this.is("isCupidEnable")) {
			session.put("isCupidEnable", false);
			return "/cupid";
		}

		if (this.is("isUpgradeEnable")) {
			session.put("isUpgradeEnable", false);
			return "/upgrade";
		}

		if (this.is("isGiftEnable")) {
			session.put("isGiftEnable", false);
			return "/gift";
		}

		if (this.is("isQuestEnable")) {
			session.put("isQuestEnable", false);
			return "/quest";
		}
		this.reset();
		return "/exit";
	}
}
