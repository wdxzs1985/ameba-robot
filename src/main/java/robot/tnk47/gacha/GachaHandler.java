package robot.tnk47.gacha;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class GachaHandler extends Tnk47EventHandler {

    private final static Pattern TICKET_GACHA_PATTERN = Pattern.compile("/gacha/gacha-ticket-animation\\?gachaId=(\\d+)&gachaUseCount=(\\d+)&token=([a-zA-Z0-9]{6})");

    public GachaHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String html = this.httpGet("/gacha?gachaTabId=2");
        this.resolveInputToken(html);
        final Matcher matcher = GachaHandler.TICKET_GACHA_PATTERN.matcher(html);
        if (matcher.find()) {
            final String gachaId = matcher.group(1);
            final String gachaUseCount = matcher.group(2);
            final String token = matcher.group(3);

            session.put("gachaId", gachaId);
            session.put("gachaUseCount", gachaUseCount);
            session.put("token", token);
            return "/gacha/ticket-gacha";
        }
        return "/mypage";
    }
}
