package robot.tnk47.gacha;

import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import robot.Robot;

public class BattleGachaHandler extends AbstractGachaHandler {

    public BattleGachaHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String prefectureBattleId = (String) session.get("prefectureBattleId");
        final String bonusIds = (String) session.get("bonusIds");
        final String token = (String) session.get("token");

        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("prefectureBattleId",
                                        prefectureBattleId));
        nvps.add(new BasicNameValuePair("bonusIds", bonusIds));
        nvps.add(new BasicNameValuePair("token", token));
        final String html = this.httpPost("/gacha/gacha-battle-reward-animation",
                                          nvps);
        this.resolveGachaResult(html);
        return "/battle";
    }

}
