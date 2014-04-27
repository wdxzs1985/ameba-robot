package robot.tnk47.conquest;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public abstract class AbstractConquestBattleHandler extends Tnk47EventHandler {

    private static final Pattern BATTLE_RESULT_PATTERN = Pattern.compile("nextUrl: \"/conquest/conquest-field-battle-result\\?conquestBattleId=([0-9_]*?)\"");

    public AbstractConquestBattleHandler(final Tnk47Robot robot) {
        super(robot);
    }

    protected JSONObject filterBattle(final JSONArray battles) {
        JSONObject target = null;
        int maxRank = 0;
        for (int i = 0; i < battles.size(); i++) {
            final JSONObject battle = battles.optJSONObject(i);
            final JSONObject allyInfo = battle.optJSONObject("allyInfo");
            final JSONObject enemyInfo = battle.optJSONObject("enemyInfo");
            final int rank = allyInfo.optInt("peopleNum") - enemyInfo.optInt("peopleNum");
            if (target == null || maxRank < rank) {
                target = battle;
                maxRank = rank;
            }
        }
        return target;
    }

    protected JSONObject filterEnemy(final JSONArray users) {
        JSONObject target = null;
        int maxRank = 0;
        for (int i = 0; i < users.size(); i++) {
            final JSONObject enemy = users.optJSONObject(i);
            final int userDefencePoint = enemy.optInt("userDefencePoint");
            final int getBattlePoint = enemy.optInt("getBattlePoint");
            if (userDefencePoint == 0) {
                continue;
            }

            final int userRank = (1000 - userDefencePoint) * getBattlePoint;
            if (target == null || maxRank < userRank) {
                target = enemy;
                maxRank = userRank;
            }
        }
        return target;
    }

    protected boolean isBattleResult(final String html) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher matcher = AbstractConquestBattleHandler.BATTLE_RESULT_PATTERN.matcher(html);
        if (matcher.find()) {
            final String conquestBattleId = matcher.group(1);
            session.put("conquestBattleId", conquestBattleId);
            return true;
        }
        return false;
    }

}
