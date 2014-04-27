package robot.tnk47.gacha;

import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47Robot;

public class ConquestGachaHandler extends AbstractGachaHandler {

    public ConquestGachaHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String prefectureBattleId = (String) session.get("conquestBattleId");
        final String bonusIds = (String) session.get("bonusIds");
        final String token = (String) session.get("token");

        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("conquestBattleId", prefectureBattleId));
        nvps.add(new BasicNameValuePair("bonusIds", bonusIds));
        nvps.add(new BasicNameValuePair("token", token));
        final String html = this.httpPost("/conquest/conquest-battle-reward-animation",
                                          nvps);
        this.resolveGachaResult(html);
        return "/conquest";
    }

}
