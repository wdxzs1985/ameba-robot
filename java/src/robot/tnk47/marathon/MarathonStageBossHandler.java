package robot.tnk47.marathon;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.tnk47.Tnk47Robot;

public class MarathonStageBossHandler extends AbstractEventHandler<Tnk47Robot> {

    private static final Pattern BOSS_RESULT_PATTERN = Pattern.compile("var bossResult = '(.*)';");

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

        if (this.log.isInfoEnabled()) {
            String bossName = "???";
            final Matcher bossResultMatcher = MarathonStageBossHandler.BOSS_RESULT_PATTERN.matcher(html);
            if (bossResultMatcher.find()) {
                final String jsonString = bossResultMatcher.group(1);
                final JSONObject bossResult = JSONObject.fromObject(jsonString)
                                                        .getJSONObject("bossResult");
                final JSONObject bossInfo = bossResult.getJSONObject("bossInfo");
                bossName = bossInfo.getString("name");
                this.log.info("击败BOSS: " + bossName);
            }
        }
        return "/marathon/stage";
    }

}
