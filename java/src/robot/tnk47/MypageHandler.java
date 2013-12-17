package robot.tnk47;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MypageHandler extends Tnk47EventHandler {

	private static final Pattern HTML_TITLE_PATTERN = Pattern
			.compile("<title>(.*)?</title>");
	private static final Pattern HTML_USER_STATUS_PATTERN = Pattern
			.compile("<div class=\"userStatusParams\">(.*?)</div>");
	private static final Pattern HTML_USER_NAME_PATTERN = Pattern
			.compile("<p class=\"userName\">(.*?)</p>");
	private static final Pattern HTML_USER_LEVEL_PATTERN = Pattern
			.compile("<dl class=\"userLevel\"><dt>Lv</dt><dd>(.*?)</dd></dl>");

	public MypageHandler(final Tnk47Robot robot) {
		super(robot);
		this.reset();
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		final String html = this.httpGet("/mypage");
		this.resolveInputToken(html);
		if (!this.is("isMypage")) {
			final Matcher userStatusMatcher = MypageHandler.HTML_USER_STATUS_PATTERN
					.matcher(html);
			if (userStatusMatcher.find()) {
				final String userStatusHtml = userStatusMatcher.group(1);
				this.printMyInfo(userStatusHtml);
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

		if (this.is("isStampGachaEnable")) {
			session.put("isStampGachaEnable", false);
			return "/gacha/stamp-gacha";
		}

		if (this.is("isGiftEnable")) {
			session.put("isGiftEnable", false);
			return "/gift";
		}

		if (this.is("isEventEnable")) {
			session.put("isEventEnable", false);
			return "/event-infomation";
		}

		if (this.is("isBattleEnable")) {
			session.put("isBattleEnable", false);
			return "/battle";
		}

		if (this.is("isQuestEnable")) {
			session.put("isQuestEnable", false);
			return "/quest";
		}

		this.reset();
		return "/exit";
	}

	private void printMyInfo(final String userStatusHtml) {
		if (this.log.isInfoEnabled()) {
			final Matcher userNameMatcher = MypageHandler.HTML_USER_NAME_PATTERN
					.matcher(userStatusHtml);
			if (userNameMatcher.find()) {
				final String userName = userNameMatcher.group(1);
				this.log.info(String.format("角色： %s", userName));
			}
			final Matcher useLevelMatcher = MypageHandler.HTML_USER_LEVEL_PATTERN
					.matcher(userStatusHtml);
			if (useLevelMatcher.find()) {
				final String userLevel = useLevelMatcher.group(1);
				this.log.info(String.format("等级： %s", userLevel));
			}
		}
	}

	private void reset() {
		final Map<String, Object> session = this.robot.getSession();
		session.put("isMypage", false);
		session.put("isStampGachaEnable", this.robot.isStampGachaEnable());
		session.put("isEventEnable", this.robot.isEventEnable());
		session.put("isGiftEnable", this.robot.isGiftEnable());
		session.put("isQuestEnable", this.robot.isQuestEnable());
		session.put("isBattleEnable", this.robot.isBattleEnable());
		session.put("isUpgradeEnable", this.robot.isUpgradeEnable());

		session.put("isQuestCardFull", false);
		session.put("isQuestFindAll", false);
		session.put("isBattlePowerOut", false);
		session.put("isBattlePointEnough", false);
	}
}
