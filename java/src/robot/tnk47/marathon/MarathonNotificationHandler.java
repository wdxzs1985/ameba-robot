package robot.tnk47.marathon;

import java.util.Map;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class MarathonNotificationHandler extends Tnk47EventHandler {

    public MarathonNotificationHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String eventId = (String) session.get("eventId");
        final String missionId = (String) session.get("missionId");
        final String userMissionId = (String) session.get("userMissionId");
        final String userId = (String) session.get("userId");
        final String discoveryFlg = (String) session.get("discoveryFlg");
        final String token = (String) session.get("token");
        final String path = String.format("/event/marathon/marathon-notification?eventId=%s&missionId=%s&userMissionId=%s&discoveryFlg=%s&userId=%s&token=%s",
                                          eventId,
                                          missionId,
                                          userMissionId,
                                          discoveryFlg,
                                          userId,
                                          token);
        final String html = this.httpGet(path);
        this.log.info(html);
        return "/marathon";
    }
}
