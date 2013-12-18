package robot.tnk47.quest;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class QuestHandler extends Tnk47EventHandler {

    private static final Pattern STAGE_INTRODUCTION_PATTERN = Pattern.compile("<a href=\"/quest\\?introductionFinish=true\">");
    private static final Pattern STAGE_DETAIL_PATTERN = Pattern.compile("<a href=\"/quest/stage-detail\\?questId=(\\d+)&areaId=(\\d+)&stageId=(\\d+)\">(.*)</a>");
    private static final Pattern BOSS_PATTERN = Pattern.compile("<section class=\"questInfo infoBox boss\">");

    private final boolean autoSelectStage;
    private final String selectedQuestId;
    private final String selectedAreaId;
    private final String selectedStageId;

    public QuestHandler(final Tnk47Robot robot) {
        super(robot);
        this.autoSelectStage = robot.isAutoSelectStage();
        this.selectedQuestId = this.robot.getSelectedQuestId();
        this.selectedAreaId = this.robot.getSelectedAreaId();
        this.selectedStageId = this.robot.getSelectedStageId();
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        String questId = this.selectedQuestId;
        String areaId = this.selectedAreaId;
        String stageId = this.selectedStageId;
        if (this.autoSelectStage) {
            String html = this.httpGet("/quest");
            final Matcher stageIntrodutionMatcher = QuestHandler.STAGE_INTRODUCTION_PATTERN.matcher(html);
            if (stageIntrodutionMatcher.find()) {
                if (this.log.isInfoEnabled()) {
                    this.log.info("进入新关卡");
                }
                html = this.httpGet("/quest?introductionFinish=true");
            }

            this.resolveInputToken(html);

            final Matcher bossMatcher = QuestHandler.BOSS_PATTERN.matcher(html);
            if (bossMatcher.find()) {
                if (this.log.isInfoEnabled()) {
                    this.log.info("BOSS出现");
                }
                return "/quest/boss-animation";
            }

            final Matcher matcher = QuestHandler.STAGE_DETAIL_PATTERN.matcher(html);
            if (matcher.find()) {
                questId = matcher.group(1);
                areaId = matcher.group(2);
                stageId = matcher.group(3);
            }
        }
        session.put("questId", questId);
        session.put("areaId", areaId);
        session.put("stageId", stageId);
        return "/quest/stage/detail";
    }
}
