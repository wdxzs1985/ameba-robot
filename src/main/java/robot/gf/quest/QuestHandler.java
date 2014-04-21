package robot.gf.quest;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class QuestHandler extends GFEventHandler {

    private static final Pattern BOSS_PATTERN = Pattern.compile("/quest/quest-boss\\?questId=([\\d]+)");

    public QuestHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String path = "/quest/quest-stage-list";
        final String html = this.httpGet(path);
        this.resolveJavascriptToken(html);

        final Matcher matcher = QuestHandler.BOSS_PATTERN.matcher(html);
        if (matcher.find()) {
            final String questId = matcher.group(1);
            session.put("questId", questId);
            return "/quest/boss";
        }

        return "/quest/detail";
    }

}
