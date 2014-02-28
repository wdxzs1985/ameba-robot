package robot.tnk47.duel;

import java.util.Map;

import net.sf.json.JSONObject;
import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class DuelBattleCheckHandler extends Tnk47EventHandler {

    public DuelBattleCheckHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        String enemyId = (String) session.get("enemyId");
        String path = String.format("/duel/duel-battle-check?enemyId=%s",
                                    enemyId);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);

        JSONObject jsonPageParams = this.resolvePageParams(html);
        if (jsonPageParams != null) {
            final String deckId = jsonPageParams.optString("selectedDeckId");
            session.put("deckId", deckId);
            return "/duel/duel-battle-animation";
        }
        return "/mypage";
    }

}
