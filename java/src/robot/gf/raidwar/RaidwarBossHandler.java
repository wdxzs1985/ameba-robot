package robot.gf.raidwar;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class RaidwarBossHandler extends GFEventHandler {

    private final static Pattern DECK_ID_PATTERN = Pattern.compile("<input id='deckId' value='([0-9_]+)' type='hidden'>");
    private final static Pattern NUM_PATTERN = Pattern.compile("var num = \"(\\d)\";");
    private final static Pattern CTN_PATTERN = Pattern.compile("var ctn = '(\\d)';");
    private final static Pattern IS_WIN_PATTERN = Pattern.compile("var IS_WIN = \"true\";");

    public RaidwarBossHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        this.bossDiscoverAnimation();
        final boolean fight = this.bossDetail();
        if (fight) {
            final boolean win = this.bossBattleAnimation();
            if (win) {
                this.bossWin();
            }
        } else {
            if (this.log.isInfoEnabled()) {
                this.log.info("元气不足");
            }
        }
        return "/raidwar";
    }

    private void bossDiscoverAnimation() {
        final Map<String, Object> session = this.robot.getSession();
        final String eventId = (String) session.get("eventId");
        final String raidwarId = (String) session.get("raidwarId");
        final String token = (String) session.get("token");
        final String path = String.format("/raidwar/quest/boss-discovery-animation?eventId=%s&raidwarId=%s&token=%s",
                                          eventId,
                                          raidwarId,
                                          token);
        final String html = this.httpGet(path);
        this.resolveJavascriptToken(html);
    }

    private boolean bossDetail() {
        final Map<String, Object> session = this.robot.getSession();
        final String eventId = (String) session.get("eventId");
        final String raidwarId = (String) session.get("raidwarId");
        final String path = String.format("/raidwar/boss/detail?eventId=%s&raidwarId=%s",
                                          eventId,
                                          raidwarId);
        final String html = this.httpGet(path);
        this.resolveJavascriptToken(html);

        if (this.getCtn(html) > 0) {
            this.getDeckId(html);
            this.getNum(html);
            return true;
        }
        return false;
    }

    private int getCtn(final String html) {
        int ctn = 0;
        final Matcher matcher = RaidwarBossHandler.CTN_PATTERN.matcher(html);
        if (matcher.find()) {
            ctn = Integer.valueOf(matcher.group(1));
        }
        return ctn;
    }

    private void getNum(final String html) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher matcher = RaidwarBossHandler.NUM_PATTERN.matcher(html);
        if (matcher.find()) {
            final String num = matcher.group(1);
            session.put("num", num);
        }
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
        final String path = String.format("/raidwar/boss/battle-animation?eventId=%s&raidwarId=%s&num=%s&deckId=%s&token=%s",
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
        return RaidwarBossHandler.IS_WIN_PATTERN.matcher(html).find();
    }

    private void bossWin() {
        final Map<String, Object> session = this.robot.getSession();
        final String eventId = (String) session.get("eventId");
        final String raidwarId = (String) session.get("raidwarId");
        final String path = String.format("/raidwar/boss/win?eventId=%s&raidwarId=%s",
                                          eventId,
                                          raidwarId);
        final String html = this.httpGet(path);
        this.resolveJavascriptToken(html);
        if (this.log.isInfoEnabled()) {
            this.log.info("捕獲成功");
        }
    }

}
