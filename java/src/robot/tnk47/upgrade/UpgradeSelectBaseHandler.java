package robot.tnk47.upgrade;

import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class UpgradeSelectBaseHandler extends AbstractEventHandler {

    public UpgradeSelectBaseHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String userCardId = (String) session.get("userCardId");
        final String token = (String) session.get("token");
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("userCardId", userCardId));
        nvps.add(new BasicNameValuePair("token", token));
        final String html = this.httpPost("/upgrade", nvps);
        this.resolveInputToken(html);
        return "/upgrade";
    }
}
