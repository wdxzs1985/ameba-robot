package robot.tnk47.guildbattle;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import robot.tnk47.Tnk47Robot;

public class GuildBattleHandler extends AbstractGuildBattleHandler {

    private static final Pattern CHARGE_PATTERN = Pattern.compile("/guildbattle/roundbattle-charge-comp\\?token=[a-zA-Z0-9]{6}");
    private static final Pattern SELECT_PATTERN = Pattern.compile("/guildbattle/roundbattle-select");
    private static final Pattern SKILL_PATTERN = Pattern.compile("/guildbattle/roundbattle-skill-animation\\?skillId=(\\d+)&token=[a-zA-Z0-9]{6}");

    public GuildBattleHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final String html = this.httpGet("/guildbattle/roundbattle");
        this.resolveInputToken(html);
        if (this.canCharge(html)) {
            return "/guildbattle/charge";
        } else if (this.canSkill(html)) {
            return "/guildbattle/skill";
        } else if (this.canSelect(html)) {
            return "/guildbattle/select";
        }

        final JSONObject pageParams = this.resolvePageParams(html);
        if (pageParams != null) {
            return "/guildbattle";
        }
        return "/mypage";
    }

    private boolean canCharge(final String html) {
        final Matcher matcher = GuildBattleHandler.CHARGE_PATTERN.matcher(html);
        return matcher.find();
    }

    private boolean canSkill(final String html) {
        final Matcher matcher = GuildBattleHandler.SKILL_PATTERN.matcher(html);
        if (matcher.find()) {
            final Map<String, Object> session = this.robot.getSession();
            final String skillId = matcher.group(1);
            session.put("skillId", skillId);
            return true;
        }
        return false;
    }

    private boolean canSelect(final String html) {
        final Matcher matcher = GuildBattleHandler.SELECT_PATTERN.matcher(html);
        return matcher.find();
    }

}
