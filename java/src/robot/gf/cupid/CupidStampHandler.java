package robot.gf.cupid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class CupidStampHandler extends GFEventHandler {

    private static final Pattern STAMP_EXEC_PATTERN = Pattern.compile("/cupid/stamp-exec\\?token=([a-zA-Z0-9]{6})");

    public CupidStampHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String html = this.httpGet("/cupid/stamp");
        final Matcher matcher = CupidStampHandler.STAMP_EXEC_PATTERN.matcher(html);
        if (matcher.find()) {
            final String token = matcher.group(1);
            session.put("token", token);
            return "/cupid/stamp/exec";
        }
        return "/mypage";
    }
}
