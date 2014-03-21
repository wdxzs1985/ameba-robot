package robot.tnk47.marathon;

import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class MarathonStageBossHandler extends Tnk47EventHandler {

    public MarathonStageBossHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String eventId = (String) session.get("eventId");
        final String token = (String) session.get("token");

        final String path = "/event/marathon/event-boss-animation";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("eventId", eventId));
        nvps.add(new BasicNameValuePair("token", token));
        final String html = this.httpPost(path, nvps);
        this.resolveInputToken(html);
        return "/marathon/stage";
    }

}
