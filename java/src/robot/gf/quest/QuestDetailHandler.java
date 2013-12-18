package robot.gf.quest;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class QuestDetailHandler extends GFEventHandler {

    private static final Pattern QUEST_ID_PATTERN = Pattern.compile("var questId = ([\\d]+);");
    private static final Pattern STAGE_ID_PATTERN = Pattern.compile("var stageId = ([\\d]+);");

    public QuestDetailHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        String path = null;
        String questId = null;
        String stageId = null;
        if (this.robot.isAutoSelectStage()) {
            path = "/quest/quest-detail";
        } else {
            questId = this.robot.getQuestId();
            stageId = this.robot.getStageId();
            path = String.format("/quest/quest-detail?questId=%s&stageId=%s",
                                 questId,
                                 stageId);
        }

        final String html = this.httpGet(path);
        this.resolveJavascriptToken(html);

        final Matcher questIdMatcher = QuestDetailHandler.QUEST_ID_PATTERN.matcher(html);
        if (questIdMatcher.find()) {
            questId = questIdMatcher.group(1);
            session.put("questId", questId);
        } else {
            return "/mypage";
        }

        final Matcher stageIdMatcher = QuestDetailHandler.STAGE_ID_PATTERN.matcher(html);
        if (stageIdMatcher.find()) {
            stageId = stageIdMatcher.group(1);
            session.put("stageId", stageId);
        } else {
            return "/mypage";
        }

        return "/quest/run";
    }

}
