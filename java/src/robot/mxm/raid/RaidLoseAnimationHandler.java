package robot.mxm.raid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.mxm.MxmRobot;

public class RaidLoseAnimationHandler extends AbstractRaidHandler {

    private static final Pattern NEXT_URL_PATTERN = Pattern.compile("_json.nextUrl = \"(.*?)\";");

    public RaidLoseAnimationHandler(final MxmRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidId = (String) session.get("raidId");
        final String raidPirtyId = (String) session.get("raidPirtyId");
        final String path = String.format("/raid/%s/%s/lose/animation",
                                          raidId,
                                          raidPirtyId);
        final String html = this.httpGet(path);

        final Matcher matcher = RaidLoseAnimationHandler.NEXT_URL_PATTERN.matcher(html);
        if (matcher.find()) {
            final String url = matcher.group(1);
            return this.resolveNextUrl(url);
        }
        return "/mypage";
    }

}
