package robot.tnk47.marathon;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class MarathonMissionAnimationHandler extends Tnk47EventHandler {

    public MarathonMissionAnimationHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String eventId = (String) session.get("eventId");
        final String userMissionId = (String) session.get("userMissionId");
        final String missionId = (String) session.get("missionId");
        final String missionKeyId = (String) session.get("missionKeyId");
        final String useItemCount = (String) session.get("useItemCount");
        final String token = (String) session.get("token");
        String html = null;
        if (StringUtils.equals(missionKeyId, "1")) {
            final String path = String.format("/event/marathon/marathon-mission-animation?eventId=%s&userMissionId=%s&missionId=%s&missionKeyId=%s&token=%s",
                                              eventId,
                                              userMissionId,
                                              missionId,
                                              missionKeyId,
                                              token);
            html = this.httpGet(path);
        } else {
            final String path = "/event/marathon/marathon-mission-animation";
            final List<BasicNameValuePair> nvps = this.createNameValuePairs();
            nvps.add(new BasicNameValuePair("eventId", eventId));
            nvps.add(new BasicNameValuePair("userMissionId", userMissionId));
            nvps.add(new BasicNameValuePair("missionId", missionId));
            nvps.add(new BasicNameValuePair("missionKeyId", missionKeyId));
            nvps.add(new BasicNameValuePair("useItemCount", useItemCount));
            nvps.add(new BasicNameValuePair("token", token));
            html = this.httpPost(path, nvps);
        }
        this.resolveInputToken(html);
        return "/marathon/mission/result";
    }
}
