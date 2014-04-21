package robot.gf.raidwar;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class RaidwarQuestTouchHandler extends GFEventHandler {

    private static final Pattern CARD_NAME_PATTERN = Pattern.compile("var RESULT_CARD_NAME = '(.*?)';");
    private static final Pattern CARD_STATUS_PATTERN = Pattern.compile("var RESULT_CARD_STATUS = '(.*?)';");

    public RaidwarQuestTouchHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String userCardId = (String) session.get("userCardId");
        final String eventId = (String) session.get("eventId");
        final String questId = (String) session.get("questId");
        final String stageId = (String) session.get("stageId");
        final String token = (String) session.get("token");
        final String path = String.format("/raidwar/quest/touch-animation?userCardId=%s&eventId=%s&questId=%s&stageId=%s&token=%s",
                                          userCardId,
                                          eventId,
                                          questId,
                                          stageId,
                                          token);
        final String html = this.httpGet(path);
        this.resolveJavascriptToken(html);

        if (this.log.isInfoEnabled()) {
            final String cardName = this.getCardName(html);
            final String cardStatus = this.getCardStatus(html);
            this.log.info(String.format("%s %s", cardName, cardStatus));
        }

        return "/raidwar/quest/detail";
    }

    private String getCardName(final String html) {
        final Matcher matcher = RaidwarQuestTouchHandler.CARD_NAME_PATTERN.matcher(html);
        if (matcher.find()) {
            final String cardName = matcher.group(1);
            return cardName;
        }
        return null;
    }

    private String getCardStatus(final String html) {
        final Matcher matcher = RaidwarQuestTouchHandler.CARD_STATUS_PATTERN.matcher(html);
        if (matcher.find()) {
            final String cardStatus = matcher.group(1);
            return cardStatus;
        }
        return null;
    }

}
