package robot.gf.upgrade;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class UpgradeAnimationHandler extends GFEventHandler {

    private static final Pattern CARD_NAME_PATTERN = Pattern.compile("var cardName = \"(.*?)\";");
    private static final Pattern CARD_LEVEL_PATTERN = Pattern.compile("var cardAfterLevelNumber = (\\d+);");

    public UpgradeAnimationHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String baseUserCardId = (String) session.get("baseUserCardId");
        final String materialUserCardId = (String) session.get("materialUserCardId");
        final String token = (String) session.get("token");
        final String path = String.format("/upgrade/upgrade-animation?baseUserCardId=%s&materialUserCardId=%s&token=%s",
                                          baseUserCardId,
                                          materialUserCardId,
                                          token);
        final String html = this.httpGet(path);

        if (this.log.isInfoEnabled()) {
            String cardName = null;
            String cardLevel = null;
            Matcher matcher = null;
            if ((matcher = UpgradeAnimationHandler.CARD_NAME_PATTERN.matcher(html)).find()) {
                cardName = matcher.group(1);
            }
            if ((matcher = UpgradeAnimationHandler.CARD_LEVEL_PATTERN.matcher(html)).find()) {
                cardLevel = matcher.group(1);
            }
            this.log.info(String.format("%s 升级到了 Lv%s", cardName, cardLevel));
        }
        return "/upgrade";
    }
}
