package robot.tnk47.guildbattle;

import java.util.Map;

import net.sf.json.JSONObject;
import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class GuildBattleSkillHandler extends Tnk47EventHandler {

    public GuildBattleSkillHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        this.skillAnimation();
        return "/guildbattle";
    }

    private void skillAnimation() {
        final Map<String, Object> session = this.robot.getSession();
        final String skillId = (String) session.get("skillId");
        final String token = (String) session.get("token");
        final String path = String.format("/guildbattle/roundbattle-skill-animation?skillId=%s&token=%s",
                                          skillId,
                                          token);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);

        final JSONObject pageParams = this.resolvePageParams(html);
        if (pageParams != null) {
            this.skillComplete();
        }
    }

    private void skillComplete() {
        final Map<String, Object> session = this.robot.getSession();
        final String token = (String) session.get("token");
        final String path = String.format("/guildbattle/roundbattle-skill-comp?eventId=&token=%s",
                                          token);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);
    }

}
