package robot.tnk47.raid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class RaidStageHandler extends Tnk47EventHandler {

    private static final Pattern STAGE_PATTERN = Pattern.compile("/raid/raid-stage-detail\\?raidId=(\\d+)&questId=(\\d+)&areaId=(\\d+)&stageId=(\\d+)");
    private static final Pattern LIMIT_OPEN_PATTERN = Pattern.compile("/raid/raid-limited-area-open\\?raidId=(\\d+)&questId=(\\d+)?&areaId=(\\d+)?&stageId=(\\d+)?");

    private final boolean raidLimitOpen;

    public RaidStageHandler(final Tnk47Robot robot) {
        super(robot);
        this.raidLimitOpen = robot.isRaidLimitOpen();
    }

    @Override
    public String handleIt() {
        final boolean initialized = this.initStage();
        if (initialized) {
            return this.initStageDetail();
        }
        return "/mypage";
    }

    private boolean initStage() {
        final Map<String, Object> session = this.robot.getSession();
        final String path = "/raid/raid-top";
        final String html = this.httpGet(path);
        final Matcher matcher = RaidStageHandler.STAGE_PATTERN.matcher(html);
        boolean initialized = false;
        while (matcher.find()) {
            initialized = true;
            final String raidId = matcher.group(1);
            final String questId = matcher.group(2);
            final String areaId = matcher.group(3);
            final String stageId = matcher.group(4);
            session.put("raidId", raidId);
            session.put("questId", questId);
            session.put("areaId", areaId);
            session.put("stageId", stageId);
        }
        return initialized;
    }

    private String initStageDetail() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidId = (String) session.get("raidId");
        final String questId = (String) session.get("questId");
        final String areaId = (String) session.get("areaId");
        final String stageId = (String) session.get("stageId");
        final String path = String.format("/raid/raid-stage-detail?raidId=%s&questId=%s&areaId=%s&stageId=%s",
                                          raidId,
                                          questId,
                                          areaId,
                                          stageId);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);

        final Matcher matcher = RaidStageHandler.LIMIT_OPEN_PATTERN.matcher(html);
        if (matcher.find()) {
            if (this.raidLimitOpen) {
                this.limitOpen();
                if (this.log.isInfoEnabled()) {
                    this.log.info("【大BOSS】封印解除");
                }
                return "/raid/stage";
            } else {
                if (this.log.isInfoEnabled()) {
                    this.log.info("【大BOSS】封印解除可能");
                }
            }
        }
        return "/raid/stage-forward";
    }

    private void limitOpen() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidId = (String) session.get("raidId");
        final String path = String.format("/raid/raid-limited-area-open?raidId=%s&questId=&areaId=&stageId=",
                                          raidId);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);
    }
}
