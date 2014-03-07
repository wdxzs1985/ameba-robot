package robot.tnk47.battle;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47Robot;

public class PrefectureBattleListHandler extends AbstractBattleHandler {

    public PrefectureBattleListHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String prefectureId = (String) session.get("prefectureId");
        final String path = "/battle/ajax/get-prefecture-battle-list";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("prefectureId", prefectureId));
        nvps.add(new BasicNameValuePair("searchType", "3"));
        final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        final JSONObject data = jsonResponse.optJSONObject("data");
        final JSONObject prefectureBattleSystemDto = data.optJSONObject("prefectureBattleSystemDto");
        final String prefectureBattleSystemStatus = prefectureBattleSystemDto.optString("prefectureBattleSystemStatus");
        if (StringUtils.equals(prefectureBattleSystemStatus, "ACTIVE")) {
            final JSONArray prefectureBattleOutlines = data.optJSONArray("prefectureBattleOutlines");
            if (prefectureBattleOutlines.size() > 0) {
                final JSONObject battle = this.filterBattle(prefectureBattleOutlines);
                final String prefectureBattleId = battle.optString("prefectureBattleId");
                session.put("prefectureBattleId", prefectureBattleId);
                if (this.log.isInfoEnabled()) {
                    final String ownPrefectureName = battle.optString("ownPrefectureName");
                    final String otherPrefectureName = battle.optString("otherPrefectureName");
                    final int ownMemberCount = battle.optInt("ownMemberCount");
                    final int otherMemberCount = battle.optInt("otherMemberCount");
                    final int ownPrefectureHpRate = battle.optInt("ownPrefectureHPRate");
                    final int otherPrefectureHpRate = battle.optInt("otherPrefectureHPRate");
                    final int ownPrefectureHp = battle.optInt("ownPrefectureHp");
                    final int ownPrefectureHpLast = ownPrefectureHp * ownPrefectureHpRate
                                                    / 100;
                    final int otherPrefectureHp = battle.optInt("otherPrefectureHp");
                    final int otherPrefectureHpLast = otherPrefectureHp * otherPrefectureHpRate
                                                      / 100;
                    this.log.info(String.format("%s (共%d人参战，HP:%d/%d) vs %s (共%d人参战，HP:%d/%d)",
                                                ownPrefectureName,
                                                ownMemberCount,
                                                ownPrefectureHpLast,
                                                ownPrefectureHp,
                                                otherPrefectureName,
                                                otherMemberCount,
                                                otherPrefectureHpLast,
                                                otherPrefectureHp));
                }
                return "/battle/detail";
            }
            if (this.log.isInfoEnabled()) {
                this.log.info("没有合战情报");
            }
            final JSONArray prefectureBattleUsers = data.optJSONArray("prefectureBattleUsers");
            if (prefectureBattleUsers.size() > 0) {
                final JSONObject enemy = this.filterLowPowerUser(prefectureBattleUsers);
                final String enemyId = enemy.optString("userId");
                session.put("battleStartType", "1");
                session.put("enemyId", enemyId);
                if (this.log.isInfoEnabled()) {
                    final String enemyName = enemy.optString("userName");
                    this.log.info(String.format("向%s发动攻击", enemyName));
                }
                return "/battle/battle-check";
            }
        }
        return "/mypage";
    }

    private JSONObject filterBattle(final JSONArray outlines) {
        for (int i = 0; i < outlines.size(); i++) {
            final JSONObject battle = outlines.optJSONObject(i);
            if (battle.optBoolean("isInvite", false)) {
                if (this.log.isInfoEnabled()) {
                    this.log.info("收到救援信息");
                }
                return battle;
            }
        }
        // TODO 按时间排序
        // TODO 按人数排序
        // TODO 按HP排序
        return outlines.optJSONObject(0);
    }

    private JSONObject filterLowPowerUser(final JSONArray users) {
        int minDefencePower = Integer.MAX_VALUE;
        JSONObject minDefencePowerUser = null;
        for (int i = 0; i < users.size(); i++) {
            final JSONObject user = users.optJSONObject(i);
            final int defencePower = user.optInt("defencePower");
            if (minDefencePower > defencePower) {
                minDefencePowerUser = user;
                minDefencePower = defencePower;
            }
        }
        return minDefencePowerUser;
    }
}
