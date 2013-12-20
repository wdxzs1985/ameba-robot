package robot.mxm;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MypageHandler extends MxmEventHandler {

    private static final Pattern HTML_TITLE_PATTERN = Pattern.compile("<title>(.*)?</title>");
    private static final Pattern HTML_USER_NAME_PATTERN = Pattern.compile("<div class=\"fsLarge marginRight10\">(.*?)</div>");

    private final boolean questEnable;

    public MypageHandler(final MxmRobot robot) {
        super(robot);
        this.questEnable = robot.isQuestEnable();
        this.reset();
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String html = this.httpGet("/mypage");
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
        this.resolveMxmToken(html);

        if (this.is("isRaidHistoryEnable")) {
            session.put("isRaidHistoryEnable", false);
            return "/raid/history";
        }

        if (this.is("isQuestEnable")) {
            return "/quest";
        }

        this.reset();
        return "/exit";
    }

    private void reset() {
        final Map<String, Object> session = this.robot.getSession();
        session.put("isMypage", false);
        session.put("isRaidHistoryEnable", true);

        session.put("isQuestEnable", this.questEnable);
    }
}
