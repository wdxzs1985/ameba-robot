package robot.tnk47;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class MypageHandler extends Tnk47EventHandler {

    private static final Pattern HTML_USER_STATUS_PATTERN = Pattern.compile("<div class=\"userStatusParams\">(.*?)</div>");
    private static final Pattern HTML_USER_NAME_PATTERN = Pattern.compile("<p class=\"userName\">(.*?)</p>");
    private static final Pattern HTML_USER_LEVEL_PATTERN = Pattern.compile("<dl class=\"userLevel\"><dt>Lv</dt><dd>(.*?)</dd></dl>");

    private static final Pattern HTML_STATUS_MATER_PATTERN = Pattern.compile("<dt>([\\d]*?)/([\\d]*?)</dt>");

    public MypageHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String html = this.httpGet("/mypage");
        this.resolveInputToken(html);
        if (!this.is("isMypage")) {
            if (this.isMypage(html)) {
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

        this.processStatusMater(html, session);

        if (this.is("isStampGachaEnable")) {
            session.put("isStampGachaEnable", false);
            return "/gacha/stamp-gacha";
        }

        if (this.is("isGachaEnable")) {
            session.put("isGachaEnable", false);
            return "/gacha";
        }

        if (this.is("isGiftEnable")) {
            session.put("isGiftEnable", false);
            return "/gift";
        }

        if (this.is("isEventEnable")) {
            session.put("isEventEnable", false);
            return "/event-infomation";
        }

        if (this.is("isDuelEnable")) {
            session.put("isDuelEnable", false);
            return "/duel";
        }

        if (!this.is("isConquest") && this.is("isBattleEnable")) {
            session.put("isBattleEnable", false);
            return "/battle";
        }

        if (this.is("isQuestEnable")) {
            session.put("isQuestEnable", false);
            return "/quest";
        }

        return "/exit";
    }

    private boolean isMypage(final String html) {
        final Matcher userStatusMatcher = MypageHandler.HTML_USER_STATUS_PATTERN.matcher(html);
        if (userStatusMatcher.find()) {
            final String userStatusHtml = userStatusMatcher.group(1);
            this.printMyInfo(userStatusHtml);
            return true;
        }
        return false;
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

    private void processStatusMater(final String html,
                                    final Map<String, Object> session) {
        final Matcher matcher = MypageHandler.HTML_STATUS_MATER_PATTERN.matcher(html);
        if (matcher.find()) {
            final String current = matcher.group(1);
            final String max = matcher.group(2);

            if (!StringUtils.equals(current, max)) {
                // session.put("isQuestEnable", false);
            }
        }
        if (matcher.find()) {
            final String current = matcher.group(1);
            final String max = matcher.group(2);

            if (!StringUtils.equals(current, max)) {
                session.put("isBattlePowerFull", true);
            }
        }
    }
}
