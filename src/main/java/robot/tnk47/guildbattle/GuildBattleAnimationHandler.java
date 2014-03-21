package robot.tnk47.guildbattle;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class GuildBattleAnimationHandler extends Tnk47EventHandler {

    public GuildBattleAnimationHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String enemyId = (String) session.get("enemyId");
        final String deckId = (String) session.get("deckId");
        final String attackType = (String) session.get("attackType");
        final String powerRegenItemType = (String) session.get("powerRegenItemType");
        final String useRegenItemCount = (String) session.get("useRegenItemCount");
        final String token = (String) session.get("token");
        final String path = "/guildbattle/roundbattle-animation";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("enemyId", enemyId));
        nvps.add(new BasicNameValuePair("deckId", deckId));
        nvps.add(new BasicNameValuePair("attackType", attackType));
        if (StringUtils.isNotBlank(powerRegenItemType)) {
            nvps.add(new BasicNameValuePair("powerRegenItemType",
                                            powerRegenItemType));
            nvps.add(new BasicNameValuePair("useRegenItemCount",
                                            useRegenItemCount));
            if (this.log.isInfoEnabled()) {
                final String itemName = (String) session.get("itemName");
                this.log.info(String.format("不要放弃治疗！使用了%s", itemName));
            }
        }
        nvps.add(new BasicNameValuePair("token", token));
        final String html = this.httpPost(path, nvps);

        this.resolveInputToken(html);
        return "/guildbattle/result";
    }
}
