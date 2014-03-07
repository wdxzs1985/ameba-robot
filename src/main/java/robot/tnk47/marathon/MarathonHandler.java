package robot.tnk47.marathon;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class MarathonHandler extends Tnk47EventHandler {

    private static final Pattern MISSION_PATTERN = Pattern.compile("/event/marathon/event-stage-detail\\?eventId=[0-9]+&userMissionId=([0-9_]+)&token=[a-zA-Z0-9]{6}");

    public MarathonHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String eventId = (String) session.get("eventId");
        final String path = String.format("/event/marathon/event-marathon?eventId=%s",
                                          eventId);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);

        final Matcher matcher = MarathonHandler.MISSION_PATTERN.matcher(html);
        if (matcher.find()) {
            final String userMissionId = matcher.group(1);
            session.put("userMissionId", userMissionId);
            return "/marathon/mission";
        }
        return "/marathon/stage";
    }

}
