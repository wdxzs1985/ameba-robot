package robot.gf.cupid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class CupidExecHandler extends GFEventHandler {

    // free cupid
    private static final Pattern DAILY_FREE_PATTERN = Pattern.compile("/cupid/cupid-exec?cupidId=1&gachaExecKind=DAILY_FREE&token=[a-zA-Z0-9]{6}");

    public CupidExecHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String token = (String) session.get("token");
        final String html = this.httpGet(String.format("/cupid/cupid-exec?cupidId=1&gachaExecKind=DAILY_FREE&token=%s",
                                                       token));
        this.resolveInputToken(html);

        Matcher matcher = null;
        if ((matcher = CupidExecHandler.DAILY_FREE_PATTERN.matcher(html)).find()) {
            matcher.group();

            session.put("cupidId", "1");
            session.put("gachaExecKind", "DAILY_FREE");

            return "/cupid/cupid-exec";
        }

        return "/mypage";
    }
}
