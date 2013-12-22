package robot.gf.raidwar;

import java.util.Map;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class RaidwarHandler extends GFEventHandler {

    public RaidwarHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String eventId = (String) session.get("eventId");
        final String path = String.format("/raidwar?eventId=%s", eventId);
        final String html = this.httpGet(path);
        this.resolveJavascriptToken(html);

        return "/raidwar/quest/detail";
    }

}
