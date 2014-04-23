package robot.tnk47.conquest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47Robot;

public class ConquestAreaTopHandler extends AbstractConquestBattleHandler {

    private static final Pattern TENP_PATTERN = Pattern.compile("<span class=\"jscTenPIcon tenPIcon \">");

    public ConquestAreaTopHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();

        final String html = this.httpGet("/conquest/conquest-area-top?affiliationId=");
        if (this.hasPoint(html)) {
            JSONObject pageParams = this.resolvePageParams(html);
            String getConquestBattleListAjaxUrl = pageParams.optString("getConquestBattleListAjaxUrl");

            List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
            nvps.add(new BasicNameValuePair("affiliationId", ""));
            JSONObject response = this.httpPostJSON(getConquestBattleListAjaxUrl,
                                                    nvps);
            JSONObject data = response.optJSONObject("data");
            JSONArray battles = data.optJSONArray("battles");

            if (battles.size() > 0) {
                final JSONObject battle = this.filterBattle(battles);
                final String battleId = battle.optString("battleId");
                session.put("conquestBattleId", battleId);
                return "/conquest/battle";
            }
            return "/conquest/battle-list";
        }
        return "/mypage";
    }

    private boolean hasPoint(String html) {
        Matcher matcher = TENP_PATTERN.matcher(html);
        return matcher.find();
    }

}
