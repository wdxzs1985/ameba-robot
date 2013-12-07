package robot.tnk47;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.RandomUtils;

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
        final Map<String, Object> session = this.robot.getSession();
        final String html = this.httpGet("/mypage");
        if (this.isEnable("mypage")) {
            final Matcher userStatusMatcher = MypageHandler.HTML_USER_STATUS_PATTERN.matcher(html);
            if (userStatusMatcher.find()) {
                final String userStatusHtml = userStatusMatcher.group(1);
                this.printMyInfo(userStatusHtml);
                session.put("mypage", false);
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
        }

        this.resolveInputToken(html);

        if (this.isEnable("checkStampGachaStatus")) {
            session.put("checkStampGachaStatus", false);
            return "/gacha/stamp-gacha";
        }

        if (this.isEnable("checkGift")) {
            session.put("checkGift", false);
            return "/gift";
        }

        if (this.isEnable("battle")) {
            session.put("battle", false);
            return "/battle";
        }

        if (this.isEnable("checkEventInfomation")) {
            session.put("checkEventInfomation", false);
            return "/event-infomation";
        }

        if (this.isEnable("quest")) {
            session.put("quest", false);
            return "/quest";
        }

        this.reset();
        this.sleep();
        return "/mypage";
    }

    private void sleep() {
        this.log.info("休息一会 _(:3_ ");
        final Properties config = this.robot.getConfig();
        final String resetTime = config.getProperty("MypageHandler.resetTime",
                                                    "5");
        int sleepTime = Integer.valueOf(resetTime);
        sleepTime = sleepTime + RandomUtils.nextInt(sleepTime);
        try {
            Thread.sleep(sleepTime * 60 * 1000);
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

    private void reset() {
        final Properties config = this.robot.getConfig();
        final boolean checkStampGachaStatus = Boolean.valueOf(config.getProperty("MypageHandler.checkStampGachaStatus",
                                                                                 "false"));
        final boolean checkEventInfomation = Boolean.valueOf(config.getProperty("MypageHandler.checkEventInfomation",
                                                                                "false"));
        final boolean checkGift = Boolean.valueOf(config.getProperty("MypageHandler.checkGift",
                                                                     "false"));
        final boolean quest = Boolean.valueOf(config.getProperty("MypageHandler.quest",
                                                                 "false"));
        final boolean battle = Boolean.valueOf(config.getProperty("MypageHandler.battle",
                                                                  "false"));
        final boolean upgrade = Boolean.valueOf(config.getProperty("MypageHandler.upgrade",
                                                                   "false"));

        final Map<String, Object> session = this.robot.getSession();
        session.put("mypage", true);
        session.put("checkStampGachaStatus", checkStampGachaStatus);
        session.put("checkEventInfomation", checkEventInfomation);
        session.put("checkGift", checkGift);
        session.put("quest", quest);
        session.put("quest-card-full", false);
        session.put("quest-find-all", false);
        session.put("battle", battle);
        session.put("battle-pt-out", false);
        session.put("battle-point", false);
        session.put("upgrade", upgrade);
    }
}
