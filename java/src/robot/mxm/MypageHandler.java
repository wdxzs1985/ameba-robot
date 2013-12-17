package robot.mxm;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MypageHandler extends MxmEventHandler {

	private static final Pattern HTML_TITLE_PATTERN = Pattern
			.compile("<title>(.*)?</title>");
	private static final Pattern HTML_USER_NAME_PATTERN = Pattern
			.compile("<div class=\"fsLarge marginRight10\">(.*?)</div>");
	private static final Pattern DAILY_ELEMENT_PATTERN = Pattern
			.compile("http://stat100.ameba.jp/mxm/ver01/page/img/orgn/daily_mission/icon_daily_element([\\d]).png");

	public MypageHandler(final MxmRobot robot) {
		super(robot);
		this.reset();
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		final String html = this.httpGet("/mypage");
		this.resolveMxmToken(html);
		this.log.debug(html);
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

		this.findDailyElement(html);

		if (this.is("isQuestEnable")) {
			session.put("isQuestEnable", false);
			return "/quest/user/list";
		}

		this.reset();
		return "/exit";
	}

	private void findDailyElement(String html) {
		final Map<String, Object> session = this.robot.getSession();
		Matcher matcher = DAILY_ELEMENT_PATTERN.matcher(html);
		if (matcher.find()) {
			String dailyElement = matcher.group(1);
			session.put("dailyElement", dailyElement);
		}
	}

	private void reset() {
		final Map<String, Object> session = this.robot.getSession();
		session.put("isMypage", false);
		session.put("isQuestEnable", this.robot.isQuestEnable());
	}
}
