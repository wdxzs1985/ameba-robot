package robot.tnk47.marathon;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class MarathonStageHandler extends Tnk47EventHandler {

    private static final Pattern STAGE_DETAIL_PATTERN = Pattern.compile("<a href=\"/event/marathon/event-stage-detail\\?eventId=[0-9]+\">(.*?)</a>");
    private static final Pattern BOSS_PATTERN = Pattern.compile("<section class=\"questInfo infoBox boss\">");

    public MarathonStageHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String eventId = (String) session.get("eventId");
        final String path = String.format("/event/marathon/event-quest?eventId=%s",
                                          eventId);
        final String html = this.httpGet(path);

        this.resolveInputToken(html);

        final Matcher bossMatcher = MarathonStageHandler.BOSS_PATTERN.matcher(html);
        if (bossMatcher.find()) {
            if (this.log.isInfoEnabled()) {
                this.log.info("BOSS出现");
            }
            return "/marathon/stage/boss";
        }

        if (this.log.isInfoEnabled()) {
            final Matcher matcher = MarathonStageHandler.STAGE_DETAIL_PATTERN.matcher(html);
            if (matcher.find()) {
                final String stageName = matcher.group(1);
                this.log.info(String.format("进入地图: %s", stageName));
            }
        }
        return "/marathon/stage/detail";
    }

}
