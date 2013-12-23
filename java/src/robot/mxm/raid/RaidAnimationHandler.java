package robot.mxm.raid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import robot.mxm.MxmRobot;

public class RaidAnimationHandler extends AbstractRaidHandler {

    private static final Pattern JSON_PATTERN = Pattern.compile("var _json = (.*?);?_json");

    private static final Pattern NEXT_URL_PATTERN = Pattern.compile("_json.nextUrl = '(.*?)';");

    public RaidAnimationHandler(final MxmRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final String html = this.httpGet("/touch/raid/animation");
        if (this.log.isInfoEnabled()) {
            this.printBossInfo(html);
        }

        final Matcher matcher = RaidAnimationHandler.NEXT_URL_PATTERN.matcher(html);
        if (matcher.find()) {
            final String nextUrl = matcher.group(1);
            return this.resolveNextUrl(nextUrl);
        }
        return "/mypage";
    }

    private void printBossInfo(final String html) {
        final Matcher matcher = RaidAnimationHandler.JSON_PATTERN.matcher(html);
        if (matcher.find()) {
            final String jsonString = matcher.group(1);
            final JSONObject data = JSONObject.fromObject(jsonString);
            final int bossLevel = data.optInt("bossLevel");
            final String bossName = data.optString("bossName");
            this.log.info(String.format("%d 级的 %s 出现了", bossLevel, bossName));
        }
    }
}
