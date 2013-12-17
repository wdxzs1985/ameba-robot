package robot.tnk47.quest;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.tnk47.Tnk47Robot;

public class QuestBossHandler extends AbstractEventHandler<Tnk47Robot> {

    private static final Pattern BOSS_RESULT_PATTERN = Pattern.compile("var bossResult = '(.*)';");

    public QuestBossHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String token = (String) session.get("token");

        final String path = "/quest/boss-animation";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("token", token));
        final String html = this.httpPost(path, nvps);

        if (this.log.isInfoEnabled()) {
            String bossName = "???";
            final Matcher bossResultMatcher = QuestBossHandler.BOSS_RESULT_PATTERN.matcher(html);
            if (bossResultMatcher.find()) {
                final String jsonString = bossResultMatcher.group(1);
                final JSONObject data = JSONObject.fromObject(jsonString);
                final JSONObject bossResult = data.optJSONObject("bossResult");
                final JSONObject bossInfo = bossResult.optJSONObject("bossInfo");
                bossName = bossInfo.optString("name");
                this.log.info("击败BOSS: " + bossName);
            }
        }
        return "/quest";
    }

}
