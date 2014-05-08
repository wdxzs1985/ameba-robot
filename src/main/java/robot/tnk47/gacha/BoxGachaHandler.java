package robot.tnk47.gacha;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.tnk47.Tnk47Robot;

public class BoxGachaHandler extends AbstractGachaHandler {

    private final static Pattern TICKET_PATTERN = Pattern.compile("/gacha/gacha-ticket-animation\\?gachaId=(\\d+)&gachaUseCount=(\\d+)&token=([a-zA-Z0-9]{6})");

    public BoxGachaHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String gachaId = (String) session.get("gachaId");

        final String path = String.format("/gacha/box-gacha?gachaId=%s",
                                          gachaId);
        final String html = this.httpGet(path);
        final Matcher ticketMatcher = BoxGachaHandler.TICKET_PATTERN.matcher(html);
        if (ticketMatcher.find()) {
            final String gachaUseCount = ticketMatcher.group(2);
            final String token = ticketMatcher.group(3);

            session.put("gachaUseCount", gachaUseCount);
            session.put("token", token);
            return "/gacha/ticket-gacha";
        }
        this.boxGachareset(html);
        return "/gacha/gacha-3";
    }

}
