package robot.mxm.raid;

import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import robot.mxm.MxmRobot;

public class RaidAttackHandler extends AbstractRaidHandler {

    public RaidAttackHandler(final MxmRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidId = (String) session.get("raidId");
        final String targetMonsterCategoryId = (String) session.get("targetMonsterCategoryId");
        final String spendBp = (String) session.get("spendBp");
        final String token = (String) session.get("token");

        final String path = String.format("/raid/%s/attack", raidId);
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("spendBp", spendBp));
        nvps.add(new BasicNameValuePair("targetMonsterCategoryId",
                                        targetMonsterCategoryId));
        nvps.add(new BasicNameValuePair("token", token));
        this.httpPost(path, nvps);
        return "/raid/top";
    }

}
