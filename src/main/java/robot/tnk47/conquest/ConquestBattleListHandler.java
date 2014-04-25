package robot.tnk47.conquest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47Robot;

public class ConquestBattleListHandler extends AbstractConquestBattleHandler {

    public ConquestBattleListHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();

        final String html = this.httpGet("/conquest/conquest-battle-list?affiliationId=");
        this.resolveInputToken(html);

        JSONObject pageParams = this.resolvePageParams(html);
        String searchAjaxUrl = pageParams.optString("searchAjaxUrl");
        String affiliationId = pageParams.optString("affiliationId");
        String battleStartType = pageParams.optString("battleStartType");
        String eventId = pageParams.optString("eventId");

        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("affiliationId", affiliationId));
        nvps.add(new BasicNameValuePair("searchType", "3"));

        JSONObject response = this.httpPostJSON(searchAjaxUrl, nvps);
        JSONObject data = response.optJSONObject("data");
        JSONArray conquestBattleUsers = data.optJSONArray("conquestBattleUsers");

        if (conquestBattleUsers.size() > 0) {
            final JSONObject enemy = this.filterEnemy(conquestBattleUsers);
            if (enemy != null) {
                final String userId = enemy.optString("userId");
                session.put("battleStartType", battleStartType);
                session.put("enemyId", userId);
                session.put("eventId", eventId);
                if (this.log.isInfoEnabled()) {
                    final String prefectureName = enemy.optString("prefectureName");
                    final String userName = enemy.optString("userName");
                    final int userLevel = enemy.optInt("level");
                    final int userDefencePoint = enemy.optInt("defencePoint");
                    final int getBattlePoint = enemy.optInt("battlePoint");
                    this.log.info(String.format("%s / %s (Lv: %d / 防御P: %d / 功績: %d)",
                                                prefectureName,
                                                userName,
                                                userLevel,
                                                userDefencePoint,
                                                getBattlePoint));
                }
                return "/conquest/battle-check";
            }
        }
        return "/mypage";
    }

}
