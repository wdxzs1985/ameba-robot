package robot.gf.raidwar;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class RaidwarBossHandler extends GFEventHandler {

    private final static Pattern DECK_ID_PATTERN = Pattern.compile("<input id='deckId' value='([0-9_]+)' type='hidden'>");

    public RaidwarBossHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        this.bossDetail();
        final boolean win = this.bossBattleAnimation();
        if (win) {
            this.bossWin();
        }
        return "/raidwar/detail";
    }

    private void bossDetail() {
        final Map<String, Object> session = this.robot.getSession();
        final String eventId = (String) session.get("eventId");
        final String raidwarId = (String) session.get("raidwarId");
        final String path = String.format("/raidwar/boss/detail?eventId=%s&raidwarId=%s",
                                          eventId,
                                          raidwarId);
        final String html = this.httpGet(path);
        this.resolveJavascriptToken(html);

        this.getDeckId(html);
        // num:3
        // deckId:1495369_1_0
    }

    private void getDeckId(final String html) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher matcher = RaidwarBossHandler.DECK_ID_PATTERN.matcher(html);
        if (matcher.find()) {
            final String deckId = matcher.group(1);
            session.put("deckId", deckId);
        }
    }

    private boolean bossBattleAnimation() {
        final Map<String, Object> session = this.robot.getSession();
        final String eventId = (String) session.get("eventId");
        final String raidwarId = (String) session.get("raidwarId");
        final String num = (String) session.get("num");
        final String deckId = (String) session.get("deckId");
        final String token = (String) session.get("token");
        final String path = String.format("/raidwar/boss/boss/battle-animation?eventId=%s&raidwarId=%s&num=%s&deckId=%s&token=%s",
                                          eventId,
                                          raidwarId,
                                          num,
                                          deckId,
                                          token);
        final String html = this.httpGet(path);
        this.resolveJavascriptToken(html);
        return this.isWin(html);
    }

    private boolean isWin(final String html) {
        // TODO Auto-generated method stub
        return false;
    }

    private void bossWin() {
        // TODO Auto-generated method stub
    }

}
