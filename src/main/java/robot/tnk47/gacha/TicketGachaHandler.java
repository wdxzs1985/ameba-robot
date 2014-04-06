package robot.tnk47.gacha;

import java.util.Map;

import robot.tnk47.Tnk47Robot;

public class TicketGachaHandler extends AbstractGachaHandler {

    public TicketGachaHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String gachaId = (String) session.get("gachaId");
        final String gachaUseCount = (String) session.get("gachaUseCount");
        final String token = (String) session.get("token");

        final String path = String.format("/gacha/gacha-ticket-animation?gachaId=%s&gachaUseCount=%s&token=%s",
                                          gachaId,
                                          gachaUseCount,
                                          token);
        final String html = this.httpGet(path);
        this.resolveGachaResult(html);
        this.boxGachareset(html);
        return "/gacha";
    }

}
