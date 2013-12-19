package robot.mxm.quest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public class QuestStageClearHandler extends MxmEventHandler {

	private static final Pattern NEW_STAGE_PATTERN = Pattern
			.compile("ステージ\\d+「(.*?)」に進みました！");
	private static final Pattern NEXT_URL_PATTERN = Pattern
			.compile("var _next = \"(.*?);\"");

	public QuestStageClearHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final String html = this.httpGet("/stage/stage_clear_animation");
		this.printNewStageName(html);
		Matcher matcher = NEXT_URL_PATTERN.matcher(html);
		if (matcher.find()) {
			String nextUrl = matcher.group(1);
			return this.resolveNextUrl(nextUrl);
		}
		return "/mypage";
	}

	private void printNewStageName(String html) {
		if (this.log.isInfoEnabled()) {
			Matcher matcher = NEW_STAGE_PATTERN.matcher(html);
			if (matcher.find()) {
				String stageName = matcher.group(1);
				this.log.info(String.format("%sに進みました！", stageName));
			}
		}
	}
}
