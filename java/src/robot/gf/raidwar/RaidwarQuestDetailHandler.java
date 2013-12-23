package robot.gf.raidwar;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class RaidwarQuestDetailHandler extends GFEventHandler {

    private static final Pattern QUEST_ID_PATTERN = Pattern.compile("var questId = (\\d+);");
    private static final Pattern STAGE_ID_PATTERN = Pattern.compile("var stageId = (\\d+);");

    public RaidwarQuestDetailHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String eventId = (String) session.get("eventId");
        final String path = String.format("/raidwar/quest/detail?eventId=%s",
                                          eventId);
        final String html = this.httpGet(path);
        this.resolveJavascriptToken(html);

        this.getQuestId(html);
        this.getStageId(html);

        return "/raidwar/quest/run";
    }

    private void getQuestId(final String html) {
        final Matcher matcher = RaidwarQuestDetailHandler.QUEST_ID_PATTERN.matcher(html);
        if (matcher.find()) {
            final String questId = matcher.group(1);
            final Map<String, Object> session = this.robot.getSession();
            session.put("questId", questId);
        }

    }

    private void getStageId(final String html) {
        final Matcher matcher = RaidwarQuestDetailHandler.STAGE_ID_PATTERN.matcher(html);
        if (matcher.find()) {
            final String stageId = matcher.group(1);
            final Map<String, Object> session = this.robot.getSession();
            session.put("stageId", stageId);
        }
    }

}
