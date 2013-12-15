package robot.tnk47.marathon;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class MarathonStageDetailHandler extends Tnk47EventHandler {

    private static final Pattern MISSION_PATTERN = Pattern.compile("/event/marathon/marathon-mission-animation\\?eventId=[0-9]+&userMissionId=([0-9_]+)&missionId=([0-9]+)&missionKeyId=([0-9]+)&token=[a-zA-Z0-9]{6}");

    public MarathonStageDetailHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String eventId = (String) session.get("eventId");
        final String path = String.format("/event/marathon/event-stage-detail?eventId=%s",
                                          eventId);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);

        final Matcher matcher = MarathonStageDetailHandler.MISSION_PATTERN.matcher(html);
        if (matcher.find()) {
            final String userMissionId = matcher.group(1);
            final String missionId = matcher.group(2);
            final String missionKeyId = matcher.group(3);

            session.put("userMissionId", userMissionId);
            session.put("missionId", missionId);
            session.put("missionKeyId", missionKeyId);
            return "/marathon/mission/animation";
        }
        if (!session.containsKey("needExpForNextLevel")) {
            session.put("needExpForNextLevel", 0);
        }
        session.put("isQuestCardFull", false);
        session.put("isQuestFindAll", false);
        return "/marathon/stage/forward";
    }
}
