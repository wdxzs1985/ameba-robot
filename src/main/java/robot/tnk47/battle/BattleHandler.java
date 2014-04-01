package robot.tnk47.battle;

import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import robot.tnk47.Tnk47Robot;

public class BattleHandler extends AbstractBattleHandler {

    public BattleHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();

        final String html = this.httpGet("/battle");
        if (this.isBattleResult(html)) {
            return "/battle/prefecture-battle-result";
        }

        this.resolveInputToken(html);

        final JSONObject jsonPageParams = this.resolvePageParams(html);
        if (jsonPageParams != null) {
            final String battleStartType = jsonPageParams.optString("battleStartType");
            session.put("battleStartType", battleStartType);

            if (StringUtils.equals(battleStartType, "1")) {
                if (this.is("isBattlePowerFull") || this.is("isPointRace")) {
                    if (this.log.isInfoEnabled()) {
                        this.log.info("尚未参战");
                    }
                    final String prefectureId = jsonPageParams.optString("prefectureId");
                    session.put("prefectureId", prefectureId);
                    return "/battle/prefecture-battle-list";
                }
            } else {
                if (this.log.isInfoEnabled()) {
                    this.log.info("合战中");
                }
                if (this.is("isBattlePowerOut") || this.is("isBattlePointEnough")) {
                    return "/mypage";
                }
                final String prefectureBattleId = jsonPageParams.optString("prefectureBattleId");
                session.put("prefectureBattleId", prefectureBattleId);
                return "/battle/detail";
            }
        }
        return "/mypage";
    }
}
