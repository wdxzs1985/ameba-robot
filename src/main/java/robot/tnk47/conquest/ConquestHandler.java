package robot.tnk47.conquest;

import java.util.regex.Pattern;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class ConquestHandler extends Tnk47EventHandler {

    private static final Pattern AREA_TOP_PATTERN = Pattern.compile("<a href=\"/conquest/conquest-area-top\" class=\"actBtn btnMainWire\" >");
    private static final Pattern BATTLE_PATTERN = Pattern.compile("<a href=\"/conquest/conquest-battle\" class=\"actBtn btnMainWire\" >");

    public ConquestHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final String html = this.httpGet("/conquest");
        if (BATTLE_PATTERN.matcher(html).find()) {
            return "/conquest/battle";
        } else if (AREA_TOP_PATTERN.matcher(html).find()) {
            return "/conquest/area-top";
        }
        return "/mypage";
    }

}
