package robot.tnk47.quest;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.AbstractEventHandler;
import robot.Robot;

public class QuestHandler extends AbstractEventHandler {

	private static final Pattern STAGE_INTRODUCTION_PATTERN = Pattern
			.compile("<a href=\"/quest\\?introductionFinish=true\">");
	private static final Pattern STAGE_DETAIL_PATTERN = Pattern
			.compile("<a href=\"/quest/stage-detail\\?questId=(\\d+)&areaId=(\\d+)&stageId=(\\d+)\">(.*)</a>");
	private static final Pattern BOSS_PATTERN = Pattern
			.compile("<section class=\"questInfo infoBox boss\">");

	public QuestHandler(final Robot robot) {
		super(robot);
	}

	@Override
	protected String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		Properties config = this.robot.getConfig();
		boolean autoSelectStage = Boolean.valueOf(config.getProperty(
				"QuestHandler.autoSelectStage", "true"));
		if (autoSelectStage) {
			String html = this.httpGet("/quest");

			final Matcher stageIntrodutionMatcher = QuestHandler.STAGE_INTRODUCTION_PATTERN
					.matcher(html);
			if (stageIntrodutionMatcher.find()) {
				if (this.log.isInfoEnabled()) {
					this.log.info("进入新关卡");
				}
				html = this.httpGet("/quest?introductionFinish=true");
			}

			final Matcher bossMatcher = QuestHandler.BOSS_PATTERN.matcher(html);
			if (bossMatcher.find()) {
				if (this.log.isInfoEnabled()) {
					this.log.info("BOSS出现");
				}
				this.resolveInputToken(html);
				return ("/quest/boss-animation");
			}

			this.resolveInputToken(html);

			final Matcher matcher = QuestHandler.STAGE_DETAIL_PATTERN
					.matcher(html);
			if (matcher.find()) {
				final String questId = matcher.group(1);
				final String areaId = matcher.group(2);
				final String stageId = matcher.group(3);

				session.put("questId", questId);
				session.put("areaId", areaId);
				session.put("stageId", stageId);
			}
		} else {
			final String questId = config.getProperty("QuestHandler.questId",
					"1");
			final String areaId = config
					.getProperty("QuestHandler.areaId", "1");
			final String stageId = config.getProperty("QuestHandler.stageId",
					"1");

			session.put("questId", questId);
			session.put("areaId", areaId);
			session.put("stageId", stageId);
		}
		return ("/quest/stage/detail");
	}
}
