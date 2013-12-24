package robot.tnk47.raid;

import java.util.Map;

import net.sf.json.JSONObject;
import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class RaidHandler extends Tnk47EventHandler {

    public RaidHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        Map<String, Object> session = this.robot.getSession();
        final String path = "/raid/raid-battle-list";
        final String html = this.httpGet(path);
        this.resolveInputToken(html);
        JSONObject pageParams = this.resolvePageParams(html);
        String raidId = pageParams.optString("raidId");
        session.put("raidId", raidId);
        return "/raid/battle-list";
    }

}
