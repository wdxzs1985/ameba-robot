package robot.tnk47.raid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class RaidHandler extends Tnk47EventHandler {

    private static final Pattern RAID_AP_NUM_PATTERN = Pattern.compile("<span id=\"jsiRaidApNum\">(\\d)/5</span>");

    public RaidHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String path = "/raid/raid-battle-list";
        final String html = this.httpGet(path);
        this.resolveInputToken(html);
        final JSONObject pageParams = this.resolvePageParams(html);
        final String raidId = pageParams.optString("raidId");
        session.put("raidId", raidId);

        final int apNow = this.findApNum(html);
        session.put("apNow", apNow);

        return "/raid/battle-list";
    }

    private int findApNum(final String html) {
        final Matcher matcher = RaidHandler.RAID_AP_NUM_PATTERN.matcher(html);
        if (matcher.find()) {
            final String apNum = matcher.group(1);
            return Integer.valueOf(apNum);
        }
        return 0;
    }
}
