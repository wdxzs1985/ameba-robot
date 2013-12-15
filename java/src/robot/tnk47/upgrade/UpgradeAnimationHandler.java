package robot.tnk47.upgrade;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class UpgradeAnimationHandler extends Tnk47EventHandler {

    public UpgradeAnimationHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String baseUserCardId = (String) session.get("baseUserCardId");
        final String materialUserCardIds = (String) session.get("materialUserCardIds");
        final String token = (String) session.get("token");
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("baseUserCardId", baseUserCardId));
        nvps.add(new BasicNameValuePair("materialUserCardIds",
                                        materialUserCardIds));
        nvps.add(new BasicNameValuePair("token", token));
        final String html = this.httpPost("/upgrade/upgrade-animation", nvps);
        this.resolveInputToken(html);
        if (this.log.isInfoEnabled()) {
            final JSONObject jsonPageParams = this.resolvePageParams(html);
            final int getExperience = jsonPageParams.getInt("getExperience");
            this.log.info(String.format("获得%d经验", getExperience));
            final boolean isLevelUp = jsonPageParams.getBoolean("isLevelUp");
            if (isLevelUp) {
                final int levelBeforeUpgrade = jsonPageParams.getInt("levelBeforeUpgrade");
                final int levelAfterUpgrade = jsonPageParams.getInt("levelAfterUpgrade");
                this.log.info(String.format("等级 %d → %d",
                                            levelBeforeUpgrade,
                                            levelAfterUpgrade));
            } else {
                this.log.info("没有升级。。。");
            }
            session.put("isQuestCardFull", false);
        }
        return "/mypage";
    }
}
