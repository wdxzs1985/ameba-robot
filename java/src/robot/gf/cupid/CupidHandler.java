package robot.gf.cupid;

import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class CupidHandler extends GFEventHandler {

    // free cupid
    private static final Pattern DAILY_FREE_PATTERN = Pattern.compile("/cupid/cupid-exec?cupidId=1&gachaExecKind=DAILY_FREE&token=[a-zA-Z0-9]{6}");

    public CupidHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final String html = this.httpGet("/cupid");
        this.resolveInputToken(html);
        if (CupidHandler.DAILY_FREE_PATTERN.matcher(html).find()) {
            return "/cupid/cupid-exec";
        }
        return "/mypage";
    }
}
