package robot.gf;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MypageHandler extends GFEventHandler {

    private static final Pattern HTML_TITLE_PATTERN = Pattern.compile("<title>(.*)?</title>");
    private static final Pattern HTML_USER_NAME_PATTERN = Pattern.compile("<h1><a href=\"/profile\">(.*?)</a></h1>");

    public MypageHandler(final GFRobot robot) {
        super(robot);
        this.reset();
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String html = this.httpGet("/mypage");
        this.resolveInputToken(html);

        if (!this.is("isMypage")) {
            final Matcher userNameMatcher = MypageHandler.HTML_USER_NAME_PATTERN.matcher(html);
            if (userNameMatcher.find()) {
                final String userName = userNameMatcher.group(1);
                this.log.info(String.format("角色： %s", userName));
                session.put("isMypage", true);
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

        if (this.is("isCupidEnable")) {
            session.put("isCupidEnable", false);
            return "/cupid";
        }

        if (this.is("isGiftEnable")) {
            session.put("isGiftEnable", false);
            return "/gift";
        }

        if (this.is("isQuestEnable")) {
            session.put("isQuestEnable", false);
            return "/quest";
        }

        this.reset();
        this.sleep();
        return "/mypage";
    }

    private void sleep() {
        final int delay = this.robot.getDelay();
        this.log.info(String.format("休息 %d min _(:3_", 5));
        try {
            Thread.sleep(delay * 60 * 1000);
        } catch (final InterruptedException e) {
        }
    }

    private void reset() {
        final Map<String, Object> session = this.robot.getSession();
        session.put("isMypage", false);

    }
}
