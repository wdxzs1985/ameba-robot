package robot.tnk47.guildbattle;

import java.util.Map;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class GuildBattleChargeHandler extends Tnk47EventHandler {

    public GuildBattleChargeHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String token = (String) session.get("token");
        final String path = String.format("/guildbattle/roundbattle-charge-comp?token=%s",
                                          token);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);
        return "/guildbattle";
    }

}
