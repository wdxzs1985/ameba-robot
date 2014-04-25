package robot.tnk47.conquest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class ConquestBattleEnemyListHandler extends Tnk47EventHandler {

    public ConquestBattleEnemyListHandler(final Tnk47Robot robot) {
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
            final JSONObject battle = this.filterBattle(conquestBattleUsers);
            if (battle != null) {
                final String userId = battle.optString("userId");
                session.put("battleStartType", battleStartType);
                session.put("enemyId", userId);
                session.put("eventId", eventId);
                if (this.log.isInfoEnabled()) {
                    final String prefectureName = battle.optString("prefectureName");
                    final String userName = battle.optString("userName");
                    final int level = battle.optInt("level");
                    final int defencePower = battle.optInt("defencePower");
                    final int battlePoints = battle.optInt("battlePoints");
                    this.log.info(String.format("%s / %s (Lv: %d / 防御P: %d / 功績: %d)",
                                                prefectureName,
                                                userName,
                                                level,
                                                defencePower,
                                                battlePoints));
                }
                return "/conquest/battle-check";
            }
        }

        return "/mypage";
    }

    private JSONObject filterBattle(JSONArray conquestBattleUsers) {
        JSONObject target = null;
        int minRank = Integer.MAX_VALUE;
        for (int i = 0; i < conquestBattleUsers.size(); i++) {
            final JSONObject battle = conquestBattleUsers.optJSONObject(i);
            final int defencePower = battle.optInt("defencePower");
            final int battlePoints = battle.optInt("battlePoints");
            int userRank = (1000 - defencePower) * battlePoints;
            if (target == null || minRank > userRank) {
                target = battle;
                minRank = userRank;
            }
        }
        return target;
    }

}
