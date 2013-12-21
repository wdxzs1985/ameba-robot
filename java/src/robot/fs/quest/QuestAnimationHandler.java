package robot.fs.quest;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import robot.fs.FSEventHandler;
import robot.fs.FSRobot;

public class QuestAnimationHandler extends FSEventHandler {

    private static final Pattern URL_PATTERN = Pattern.compile("url=\"(.*?)\";");

    private static final Pattern CARD_NAME_PATTERN = Pattern.compile("<h2 class=\"cardTitle\">(.*?)<span class=\"fsM2\">(.*?)</span></h2>");

    public QuestAnimationHandler(final FSRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String questId = (String) session.get("questId");
        final String stageId = (String) session.get("stageId");
        final String token = (String) session.get("token");

        final String path = String.format("/quest/quest-animation?questId=%s&stageId=%s&token=%s",
                                          questId,
                                          stageId,
                                          token);

        final String html = this.httpGet(path);
        final Matcher matcher = QuestAnimationHandler.URL_PATTERN.matcher(html);
        if (matcher.find()) {
            final String url = matcher.group(1);
            return this.nextPage(url);
        }
        return "/mypage";
    }

    private String nextPage(final String path) {
        this.log.debug(path);
        final String html = this.httpGet(path);
        this.log.debug(html);

        if (StringUtils.startsWith(path, "/quest/reward-card-animation")) {
            if (this.log.isInfoEnabled()) {
                this.printRewardCard(html);
            }
            return "/quest/animation";
        } else if (StringUtils.startsWith(path, "/quest/clear-animation?")) {
            if (this.log.isInfoEnabled()) {
                this.log.info("STAGE CLEAR");
            }
            return "/quest";
        } else if (StringUtils.startsWith(path, "/quest/stamina")) {
            if (this.log.isInfoEnabled()) {
                this.log.info("体力不支");
            }
        }
        return "/mypage";
    }

    private void printRewardCard(final String html) {
        final Matcher matcher = QuestAnimationHandler.CARD_NAME_PATTERN.matcher(html);
        if (matcher.find()) {
            final String cardTitle = matcher.group(1);
            final String cardType = matcher.group(2);
            this.log.info(String.format("获得卡片： %s %s", cardTitle, cardType));
        }
    }
}
