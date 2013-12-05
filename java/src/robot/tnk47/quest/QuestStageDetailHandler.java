package robot.tnk47.quest;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.AbstractEventHandler;
import robot.EventHandler;
import robot.Robot;

public class QuestStageDetailHandler extends AbstractEventHandler implements
        EventHandler {

    private static final Pattern STAGE_NAME_PATTERN = Pattern.compile("<p>(\\d+章)&nbsp;(\\d+-\\d+)&nbsp;<span>(.*?)</span></p>");

    public QuestStageDetailHandler(final Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();

        final String questId = (String) session.get("questId");
        final String areaId = (String) session.get("areaId");
        final String stageId = (String) session.get("stageId");
        final String path = String.format("/quest/stage-detail?questId=%s&areaId=%s&stageId=%s",
                                          questId,
                                          areaId,
                                          stageId);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);

        if (this.log.isInfoEnabled()) {
            final Matcher stageNameMatcher = QuestStageDetailHandler.STAGE_NAME_PATTERN.matcher(html);
            if (stageNameMatcher.find()) {
                final String charpter = stageNameMatcher.group(1);
                final String section = stageNameMatcher.group(2);
                final String region = stageNameMatcher.group(3);
                this.log.info(String.format("进入地图: %s %s %s",
                                            charpter,
                                            section,
                                            region));
                session.put("quest-card-full", false);
                session.put("quest-find-all", false);
            }
        }
        return "/quest/stage/forward";
    }

}
