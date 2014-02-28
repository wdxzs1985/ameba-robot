package robot.tnk47.duel;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class DuelHandler extends Tnk47EventHandler {

    private static final Pattern DUELPT_PATTERN = Pattern.compile("<em class=\"duelPt remain\">");

    public DuelHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();

        final String html = this.httpGet("/duel");

        if (this.hasPoint(html)) {
            return "/duel/duel-battle-select";
        }

        return "/mypage";
    }

    private boolean hasPoint(String html) {
        Matcher matcher = DUELPT_PATTERN.matcher(html);
        return matcher.find();
    }

}
