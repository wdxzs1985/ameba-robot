package robot.fs;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class MypageHandler extends FSEventHandler {

    private static final Pattern HTML_USER_NAME_PATTERN = Pattern.compile("<h1 class=\".*?\">(.*?)<span class=\".*?\">さんのステータス</span></h1>");

    public MypageHandler(final FSRobot robot) {
        super(robot);
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
                final String title = this.getHtmlTitle(html);
                if (this.log.isInfoEnabled()) {
                    this.log.info(title);
                }
                if (StringUtils.contains(title, "メンテナンスのお知らせ")) {
                    return "/exit";
                }
                return "/mypage";
            }
        }

        if (this.is("isQuestEnable")) {
            session.put("isQuestEnable", false);
            return "/quest";
        }

        return "/exit";
    }
}
