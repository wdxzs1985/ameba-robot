package robot.tnk47.conquest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class ConquestBattleCheckHandler extends Tnk47EventHandler {

    private static final Pattern POINT_UP_PATTERN = Pattern.compile("<span class=\"bonusTitle\">功績</span>+(\\d+)%</span>");

    public ConquestBattleCheckHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        String battleStartType = (String) session.get("battleStartType");
        String conquestBattleId = (String) session.get("conquestBattleId");
        String eventId = (String) session.get("eventId");
        String enemyId = (String) session.get("enemyId");

        String path = "/conquest/conquest-battle-check";
        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("battleStartType", battleStartType));
        if (StringUtils.isNotBlank(eventId)) {
            nvps.add(new BasicNameValuePair("eventId", eventId));
        }
        if (StringUtils.isNotBlank(conquestBattleId)) {
            nvps.add(new BasicNameValuePair("conquestBattleId",
                                            conquestBattleId));
        }
        nvps.add(new BasicNameValuePair("enemyId", enemyId));

        String html = this.httpPost(path, nvps);
        this.resolveInputToken(html);

        int pointUp = this.getPointUp(html);
        if (this.log.isInfoEnabled()) {
            this.log.info(String.format("功績+%d%%", pointUp));
        }

        JSONObject jsonPageParams = this.resolvePageParams(html);
        if (jsonPageParams != null) {
            final String deckId = jsonPageParams.optString("selectedDeckId");
            session.put("useApSmall", "0");
            session.put("useApFull", "0");
            session.put("usePowerHalf", "0");
            session.put("usePowerFull", "0");
            session.put("deckId", deckId);
            session.put("attackType", "1");
            return "/conquest/battle-animation";
        }
        return "/mypage";
    }

    private int getPointUp(String html) {
        Matcher matcher = POINT_UP_PATTERN.matcher(html);
        if (matcher.find()) {
            return Integer.valueOf(matcher.group(1));
        }
        return 0;
    }

}
