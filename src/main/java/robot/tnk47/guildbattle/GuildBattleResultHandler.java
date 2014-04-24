package robot.tnk47.guildbattle;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.tnk47.Tnk47Robot;

public class GuildBattleResultHandler extends AbstractGuildBattleHandler {

    private static final Pattern PT_PATTERN = Pattern.compile("<span class=\"totalRoundBattlePt\">(.*?)</span>");

    public GuildBattleResultHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String token = (String) session.get("token");
        final String path = "/guildbattle/roundbattle-result?token=%s";
        final String html = this.httpGet(String.format(path, token));

        if (this.log.isInfoEnabled()) {
            final Matcher matcher = GuildBattleResultHandler.PT_PATTERN.matcher(html);
            if (matcher.find()) {
                final String text = matcher.group(1);
                this.log.info(text);
            }
        }

        return "/guildbattle";
    }
}
