package robot.tnk47.duel;

import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class DuelBattleAnimationHandler extends Tnk47EventHandler {

    public DuelBattleAnimationHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String deckId = (String) session.get("deckId");
        final String token = (String) session.get("token");
        final String path = "/duel/duel-battle-animation";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("attackDeckId", deckId));
        nvps.add(new BasicNameValuePair("token", token));
        final String html = this.httpPost(path, nvps);

        this.resolveInputToken(html);
        return "/duel/duel-battle-result";
    }
}
