package robot.mxm.quest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public class QuestGetRingHandler extends MxmEventHandler {

	private static final Pattern RING_NAME_PATTERN = Pattern
			.compile("<div class=\"fsLarge\">(.*?)</div>");
	private static final Pattern RING_TYPE_PATTERN = Pattern
			.compile("<span class=\"iconAttr type\\d\">(.)</span>");
	private static final Pattern NEXT_URL_PATTERN = Pattern
			.compile("<form id=\"getRingForm\" action=\"(.*?)\" method=\"GET\">");

	public QuestGetRingHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final String html = this.httpGet("/ring/ring_get_animation");
		if (this.log.isInfoEnabled()) {
			this.printRingName(html);
			this.printRingType(html);
		}

		Matcher matcher = NEXT_URL_PATTERN.matcher(html);
		if (matcher.find()) {
			String nextUrl = matcher.group(1);
			return this.resolveNextUrl(nextUrl);
		}
		return "/mypage";
	}

	private void printRingName(String html) {
		Matcher matcher = RING_NAME_PATTERN.matcher(html);
		if (matcher.find()) {
			String stageName = matcher.group(1);
			this.log.info(String.format("GET RING: %s", stageName));
		}
	}

	private void printRingType(String html) {
		Matcher matcher = RING_TYPE_PATTERN.matcher(html);
		if (matcher.find()) {
			String stageName = matcher.group(1);
			this.log.info(String.format("属性： %s", stageName));
		}
	}
}
