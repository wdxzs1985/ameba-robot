package robot.tnk47;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.AbstractEventHandler;
import robot.Robot;

public class MypageHandler extends AbstractEventHandler {

    private static final Pattern HTML_TITLE_PATTERN = Pattern.compile("<title>(.*)?</title>");
    private static final Pattern HTML_USER_STATUS_PATTERN = Pattern.compile("<div class=\"userStatusParams\">(.*?)</div>");
    private static final Pattern HTML_USER_NAME_PATTERN = Pattern.compile("<p class=\"userName\">(.*?)</p>");
    private static final Pattern HTML_USER_LEVEL_PATTERN = Pattern.compile("<dl class=\"userLevel\"><dt>Lv</dt><dd>(.*?)</dd></dl>");

    public MypageHandler(final Robot robot) {
        super(robot);
        this.reset();
    }

    @Override
    public String handleIt() {
        final String html = this.httpGet("/mypage");
        final Matcher userStatusMatcher = MypageHandler.HTML_USER_STATUS_PATTERN.matcher(html);
        if (userStatusMatcher.find()) {
            final String userStatusHtml = userStatusMatcher.group(1);
            this.printMyInfo(userStatusHtml);
        } else {
            if (this.log.isInfoEnabled()) {
                final Matcher titleMatcher = MypageHandler.HTML_TITLE_PATTERN.matcher(html);
                if (titleMatcher.find()) {
                    final String title = titleMatcher.group(1);
                    this.log.info(title);
                }
            }
            return "/mypage";
        }

        this.resolveInputToken(html);

        if (this.isEnable("checkStampGachaStatus")) {
            return "/gacha/stamp-gacha";
        }

        if (this.isEnable("checkGift")) {
            return "/gift";
        }

        if (this.isEnable("battle")) {
            return "/battle";
        }

        if (this.isEnable("checkEventInfomation")) {
            return "/event-infomation";
        }

        if (this.isEnable("quest")) {
            return "/quest";
        }

        this.sleep();
        this.reset();
        return "/mypage";
    }

    private void sleep() {
        this.log.info("休息一会 _(:3_ ");
        final Properties config = this.robot.getConfig();
        final long sleepTime = Long.valueOf(config.getProperty("MypageHandler.sleepTime",
                                                               "3600000"));
        try {
            Thread.sleep(sleepTime);
        } catch (final InterruptedException e) {
        }
    }

    private void printMyInfo(final String userStatusHtml) {
        if (this.log.isInfoEnabled()) {
            final Matcher userNameMatcher = MypageHandler.HTML_USER_NAME_PATTERN.matcher(userStatusHtml);
            if (userNameMatcher.find()) {
                final String userName = userNameMatcher.group(1);
                this.log.info(String.format("角色： %s", userName));
            }
            final Matcher useLevelMatcher = MypageHandler.HTML_USER_LEVEL_PATTERN.matcher(userStatusHtml);
            if (useLevelMatcher.find()) {
                final String userLevel = useLevelMatcher.group(1);
                this.log.info(String.format("等级： %s", userLevel));
            }
        }
    }

    private boolean isEnable(final String funcName) {
        final Map<String, Object> session = this.robot.getSession();
        final boolean enable = (Boolean) session.get(funcName);
        if (enable) {
            session.put(funcName, false);
        }
        return enable;
    }

    private void reset() {
        final Properties config = this.robot.getConfig();
        final Map<String, Object> session = this.robot.getSession();
        session.put("checkStampGachaStatus",
                    Boolean.valueOf(config.getProperty("MypageHandler.checkStampGachaStatus")));
        session.put("checkEventInfomation",
                    Boolean.valueOf(config.getProperty("MypageHandler.checkEventInfomation")));
        session.put("checkGift",
                    Boolean.valueOf(config.getProperty("MypageHandler.checkGift")));
        session.put("quest",
                    Boolean.valueOf(config.getProperty("MypageHandler.quest")));
        session.put("battle",
                    Boolean.valueOf(config.getProperty("MypageHandler.battle")));
    }
}
