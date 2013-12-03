package robot.tnk47.battle;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.Robot;

public class BattleAnimationHandler extends AbstractBattleHandler {

    public BattleAnimationHandler(final Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String battleStartType = (String) session.get("battleStartType");
        final String enemyId = (String) session.get("enemyId");
        final String deckId = (String) session.get("deckId");
        final String attackType = (String) session.get("attackType");
        final String token = (String) session.get("token");
        final String powerRegenItemType = (String) session.get("powerRegenItemType");
        final String path = "/battle/battle-animation";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("battleStartType", battleStartType));
        nvps.add(new BasicNameValuePair("enemyId", enemyId));
        nvps.add(new BasicNameValuePair("deckId", deckId));
        nvps.add(new BasicNameValuePair("attackType", attackType));
        nvps.add(new BasicNameValuePair("token", token));
        if (StringUtils.isNotBlank(powerRegenItemType)) {
            nvps.add(new BasicNameValuePair("powerRegenItemType",
                                            powerRegenItemType));
            nvps.add(new BasicNameValuePair("useRegenItemCount", "1"));
        }
        final String html = this.httpPost(path, nvps);

        if (this.isBattleResult(html)) {
            return "/battle/prefecture-battle-result";
        }

        this.resolveInputToken(html);
        return "/battle/battle-result";
    }
}
