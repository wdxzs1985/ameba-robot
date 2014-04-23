package robot.tnk47.conquest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public abstract class AbstractConquestBattleHandler extends Tnk47EventHandler {

    public AbstractConquestBattleHandler(final Tnk47Robot robot) {
        super(robot);
    }

    protected JSONObject filterBattle(JSONArray battles) {
        JSONObject target = null;
        int maxRank = 0;
        for (int i = 0; i < battles.size(); i++) {
            final JSONObject battle = battles.optJSONObject(i);
            final JSONObject allyInfo = battle.optJSONObject("allyInfo");
            final JSONObject enemyInfo = battle.optJSONObject("enemyInfo");
            int rank = allyInfo.optInt("peopleNum") - enemyInfo.optInt("peopleNum");
            if (target == null || maxRank < rank) {
                target = battle;
                maxRank = rank;
            }
        }
        return target;
    }

    protected JSONObject filterEnemy(JSONArray users) {
        JSONObject target = null;
        int maxRank = 0;
        for (int i = 0; i < users.size(); i++) {
            final JSONObject enemy = users.optJSONObject(i);
            final int userDefencePoint = enemy.optInt("userDefencePoint");
            final int getBattlePoint = enemy.optInt("getBattlePoint");
            int userRank = (1000 - userDefencePoint) * getBattlePoint;
            if (target == null || maxRank < userRank) {
                target = enemy;
                maxRank = userRank;
            }
        }
        return target;
    }
}
