package robot.mxm.convert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;

public class EventPointPrinter {

    private static final Pattern POINT_PATTERN = Pattern.compile("<div><span class=\"colorDeepOrange.*?\">お宝pt：</span>([\\d,]+pt)（.*?）</div>");

    private static final Pattern RANKING_PATTERN = Pattern.compile("<div><span class=\"colorDeepOrange.*?\">ランキング：</span>(\\d+位)</div>");

    private static final Pattern TREATURE_PATTERN = Pattern.compile("<div><span class=\"colorDeepOrange.*?\">お宝発見数：</span>(\\d+/\\d+)</div>");

    public static void printPoint(final Log log, final String html) {
        final Matcher matcher = EventPointPrinter.POINT_PATTERN.matcher(html);
        if (matcher.find()) {
            final String point = matcher.group(1);
            log.info(String.format("积分： %s", point));
        }
    }

    public static void printRanking(final Log log, final String html) {
        final Matcher matcher = EventPointPrinter.RANKING_PATTERN.matcher(html);
        if (matcher.find()) {
            final String ranking = matcher.group(1);
            log.info(String.format("排名： %s", ranking));
        }
    }

    public static void printTreature(final Log log, final String html) {
        final Matcher matcher = EventPointPrinter.TREATURE_PATTERN.matcher(html);
        if (matcher.find()) {
            final String treature = matcher.group(1);
            log.info(String.format("宝物发现： %s", treature));
        }
    }
}
