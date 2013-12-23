package robot.mxm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetRingHandler extends MxmEventHandler {

    private static final Pattern RING_NAME_PATTERN = Pattern.compile("<div class=\"fsLarge\">(.*?)</div>");
    private static final Pattern RING_TYPE_PATTERN = Pattern.compile("<span class=\"iconAttr type\\d\">(.)</span>");
    private static final Pattern NEXT_URL_PATTERN = Pattern.compile("<form id=\"getRingForm\" action=\"(.*?)\" method=\"GET\">");

    public GetRingHandler(final MxmRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final String html = this.httpGet("/ring/ring_get_animation");
        if (this.log.isInfoEnabled()) {
            this.printRingName(html);
            this.printRingType(html);
        }

        final Matcher matcher = GetRingHandler.NEXT_URL_PATTERN.matcher(html);
        if (matcher.find()) {
            final String nextUrl = matcher.group(1);
            return this.resolveNextUrl(nextUrl);
        }
        return "/mypage";
    }

    private void printRingName(final String html) {
        final Matcher matcher = GetRingHandler.RING_NAME_PATTERN.matcher(html);
        if (matcher.find()) {
            final String ringName = matcher.group(1);
            this.log.info(String.format("获得新戒指: %s", ringName));
        }
    }

    private void printRingType(final String html) {
        final Matcher matcher = GetRingHandler.RING_TYPE_PATTERN.matcher(html);
        if (matcher.find()) {
            final String ringType = matcher.group(1);
            this.log.info(String.format("属性： %s", ringType));
        }
    }
}
