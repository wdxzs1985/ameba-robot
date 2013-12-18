package robot.gf;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MypageHandler extends GFEventHandler {

	private static final Pattern HTML_TITLE_PATTERN = Pattern
			.compile("<title>(.*?)</title>");
	private static final Pattern HTML_USER_NAME_PATTERN = Pattern
			.compile("<h1><a href=\"/profile\">(.*?)</a></h1>");
	private static final Pattern HTML_JOB_CARD_SETTING_PATTERN = Pattern
			.compile("/job/job-card-setting");
	private static final Pattern HTML_JOB_FINISH_PATTERN = Pattern
			.compile("<a id=\"finishJobBtn\" class=\"btnPink\">受け取る</a>");

	public MypageHandler(final GFRobot robot) {
		super(robot);
		this.reset();
	}

	private void reset() {
		final Map<String, Object> session = this.robot.getSession();
		session.put("isMypage", false);
		session.put("isUpgradeEnable", this.robot.isUpgradeEnable());
		session.put("isCupidEnable", this.robot.isCupidEnable());
		session.put("isCupidStampEnable", this.robot.isCupidStampEnable());
		session.put("isGiftEnable", this.robot.isGiftEnable());
		session.put("isJobEnable", this.robot.isJobEnable());
		session.put("isQuestEnable", this.robot.isQuestEnable());
		session.put("isBattleEnable", this.robot.isBattleEnable());
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		final String html = this.httpGet("/mypage");

		this.resolveJavascriptToken(html);

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

		if (this.is("isCupidStampEnable")) {
			session.put("isCupidStampEnable", false);
			return "/cupid/stamp";
		}

		if (this.is("isUpgradeEnable")) {
			session.put("isUpgradeEnable", false);
			return "/upgrade";
		}

		if (this.is("isGiftEnable")) {
			session.put("isGiftEnable", false);
			return "/gift";
		}

		if (this.is("isJobEnable")) {
			session.put("isJobEnable", false);
			if (HTML_JOB_CARD_SETTING_PATTERN.matcher(html).find()) {
				return "/job/setting";
			}
			if (HTML_JOB_FINISH_PATTERN.matcher(html).find()) {
				return "/job/payment";
			}
		}

		if (this.is("isQuestEnable")) {
			session.put("isQuestEnable", false);
			return "/quest";
		}
		this.reset();
		return "/exit";
	}
}
