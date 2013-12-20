package robot.mxm.convert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;

public class EventPointPrinter {

	private static final Pattern POINT_PATTERN = Pattern
			.compile("<div><span class=\"colorDeepOrange.*?\">お宝pt：</span>([\\d,]+pt)（.*?）</div>");

	private static final Pattern RANKING_PATTERN = Pattern
			.compile("<div><span class=\"colorDeepOrange.*?\">ランキング：</span>(\\d+位)</div>");

	private static final Pattern TREATURE_PATTERN = Pattern
			.compile("<div><span class=\"colorDeepOrange.*?\">お宝発見数：</span>(\\d+/\\d+)</div>");

	public static void printPoint(Log log, String html) {
		Matcher matcher = POINT_PATTERN.matcher(html);
		if (matcher.find()) {
			String point = matcher.group(1);
			log.info(String.format("お宝pt： %s", point));
		}
	}

	public static void printRanking(Log log, String html) {
		Matcher matcher = RANKING_PATTERN.matcher(html);
		if (matcher.find()) {
			String ranking = matcher.group(1);
			log.info(String.format("ランキング： %s", ranking));
		}
	}

	public static void printTreature(Log log, String html) {
		Matcher matcher = TREATURE_PATTERN.matcher(html);
		if (matcher.find()) {
			String treature = matcher.group(1);
			log.info(String.format("お宝発見数： %s", treature));
		}
	}
}
