package robot.tnk47.duel;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class DuelBattleSelectHandler extends Tnk47EventHandler {

    public DuelBattleSelectHandler(Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String html = this.httpGet("/duel/duel-battle-select");
        this.resolveInputToken(html);

        String token = (String) session.get("token");
        final String path = "/duel/ajax/get-duel-battle-target-list";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("token", token));
        final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        String resultStatus = jsonResponse.optString("resultStatus");
        if (StringUtils.equals("success", resultStatus)) {
            JSONArray data = jsonResponse.optJSONArray("data");
            if (data != null) {
                JSONObject bestTarget = null;
                int minWinRate = 100;
                for (int i = 0; i < data.size(); i++) {
                    JSONObject target = data.optJSONObject(i);
                    int totalWin = target.optInt("totalWins");
                    int totalLosses = target.optInt("totalLosses");
                    int winRate = totalWin * 100 / (totalWin + totalLosses);
                    if (bestTarget == null) {
                        bestTarget = target;
                        minWinRate = winRate;
                    } else if (minWinRate > winRate) {
                        bestTarget = target;
                        minWinRate = winRate;
                    }
                }

                if (bestTarget != null) {
                    session.put("enemyId", bestTarget.optString("userId"));
                    return "/duel/duel-battle-check";
                }
            }
        }

        return "/mypage";
    }

}
