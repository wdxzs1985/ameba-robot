package robot.fs.quest;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.fs.FSEventHandler;
import robot.fs.FSRobot;

public class QuestHandler extends FSEventHandler {

    private static final Pattern QUEST_ID_PATTERN = Pattern.compile("<input id=\"questId\" type=\"hidden\" value=\"(\\d+)\"/>");
    private static final Pattern BOSS_PATTERN = Pattern.compile("/quest/boss-detail\\?questId=(\\d+)");

    public QuestHandler(final FSRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final String path = "/quest";
        final String html = this.httpGet(path);
        this.resolveInputToken(html);

        if (this.isBoss(html)) {
            return "/quest/boss";
        }

        if (this.isQuest(html)) {
            return "/quest/search";
        }

        return "/mypage";
    }

    private boolean isBoss(final String html) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher matcher = QuestHandler.BOSS_PATTERN.matcher(html);
        if (matcher.find()) {
            final String questId = matcher.group(1);
            session.put("questId", questId);
            return true;
        }
        return false;
    }

    private boolean isQuest(final String html) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher matcher = QuestHandler.QUEST_ID_PATTERN.matcher(html);
        if (matcher.find()) {
            final String questId = matcher.group(1);
            session.put("questId", questId);
            return true;
        }
        return false;
    }
}
