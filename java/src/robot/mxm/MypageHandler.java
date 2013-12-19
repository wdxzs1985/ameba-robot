package robot.mxm;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

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
		this.log.debug(html);
		this.resolveMxmToken(html);
		this.findDailyElement(html);

		if (!this.hasMonsterType()) {
			return "/monster";
		}

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
		// <span class="colorDeepOrange">[今日の召喚獣]</span>はクリア済みです！
		if (matcher.find()) {
			String dailyElement = matcher.group(1);
			session.put("dailyElement", dailyElement);
			if (this.log.isInfoEnabled()) {
				if (StringUtils.equals("1", dailyElement)) {
					this.log.info("今日の召喚獣は火です。");
				} else if (StringUtils.equals("2", dailyElement)) {
					this.log.info("今日の召喚獣は水です。");
				} else if (StringUtils.equals("3", dailyElement)) {
					this.log.info("今日の召喚獣は木です。");
				} else if (StringUtils.equals("4", dailyElement)) {
					this.log.info("今日の召喚獣は雷です。");
				} else if (StringUtils.equals("5", dailyElement)) {
					this.log.info("今日の召喚獣は風です。");
				} else if (StringUtils.equals("6", dailyElement)) {
					this.log.info("今日の召喚獣は土です。");
				}
			}
		}
	}

	private boolean hasMonsterType() {
		final Map<String, Object> session = this.robot.getSession();
		return session.containsKey("monsterType");
	}

	private void reset() {
		final Map<String, Object> session = this.robot.getSession();
		session.put("isMypage", false);
		session.put("isQuestEnable", this.robot.isQuestEnable());
	}
}
