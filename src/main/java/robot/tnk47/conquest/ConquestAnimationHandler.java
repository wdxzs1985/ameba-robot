package robot.tnk47.conquest;

import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47Robot;

public class ConquestAnimationHandler extends AbstractConquestBattleHandler {

    public ConquestAnimationHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String useApSmall = (String) session.get("useApSmall");
        final String useApFull = (String) session.get("useApFull");
        final String usePowerHalf = (String) session.get("usePowerHalf");
        final String usePowerFull = (String) session.get("usePowerFull");
        final String battleStartType = (String) session.get("battleStartType");
        final String enemyId = (String) session.get("enemyId");
        final String deckId = (String) session.get("deckId");
        final String attackType = (String) session.get("attackType");
        final String token = (String) session.get("token");

        final String path = "/conquest/conquest-battle-animation";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("useApSmall", useApSmall));
        nvps.add(new BasicNameValuePair("useApFull", useApFull));
        nvps.add(new BasicNameValuePair("usePowerHalf", usePowerHalf));
        nvps.add(new BasicNameValuePair("usePowerFull", usePowerFull));
        nvps.add(new BasicNameValuePair("battleStartType", battleStartType));
        nvps.add(new BasicNameValuePair("enemyId", enemyId));
        nvps.add(new BasicNameValuePair("deckId", deckId));
        nvps.add(new BasicNameValuePair("attackType", attackType));
        nvps.add(new BasicNameValuePair("token", token));

        final String html = this.httpPost(path, nvps);
        this.resolveInputToken(html);

        if (this.isBattleResult(html)) {
            return "/conquest/field-result";
        }

        return "/conquest/conquest-result";
    }
}
