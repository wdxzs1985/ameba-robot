package robot.tnk47.gacha;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class Gacha3Handler extends Tnk47EventHandler {

    private final static Pattern TICKET_PATTERN = Pattern.compile("/gacha/gacha-ticket-animation\\?gachaId=(\\d+)&gachaUseCount=(\\d+)&token=([a-zA-Z0-9]{6})");
    private final static Pattern BOX_PATTERN = Pattern.compile("<p class=\"decoBtn\"><a href=\"/gacha/box-gacha\\?gachaId=(\\d+)\">");

    public Gacha3Handler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String html = this.httpGet("/gacha?gachaTabId=3");
        this.resolveInputToken(html);
        final Matcher ticketMatcher = Gacha3Handler.TICKET_PATTERN.matcher(html);
        if (ticketMatcher.find()) {
            final String gachaId = ticketMatcher.group(1);
            final String gachaUseCount = ticketMatcher.group(2);
            final String token = ticketMatcher.group(3);

            session.put("gachaId", gachaId);
            session.put("gachaUseCount", gachaUseCount);
            session.put("token", token);
            return "/gacha/ticket-gacha";
        }
        final Matcher boxMatcher = Gacha3Handler.BOX_PATTERN.matcher(html);
        if (boxMatcher.find()) {
            final String gachaId = boxMatcher.group(1);
            session.put("gachaId", gachaId);
            return "/gacha/box-gacha";
        }
        return "/mypage";
    }
}
