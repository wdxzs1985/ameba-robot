package robot.tnk47.duel;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class DuelBattleResultHandler extends Tnk47EventHandler {

    private static final Pattern NEXT_PATTERN = Pattern.compile("<p class=\"nextConditionText\">(.*?)</p>");

    public DuelBattleResultHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String token = (String) session.get("token");
        final String path = "/duel/duel-battle-result?token=%s";
        final String html = this.httpGet(String.format(path, token));

        if (this.log.isInfoEnabled()) {
            Matcher matcher = NEXT_PATTERN.matcher(html);
            if (matcher.find()) {
                String text = matcher.group(1);
                text = StringUtils.remove(text, "<em>");
                text = StringUtils.remove(text, "</em>");

                this.log.info(text);
            }
        }

        return "/duel";
    }
}
