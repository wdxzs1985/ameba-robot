package robot.fs.quest;

import java.util.Map;

import robot.fs.FSEventHandler;
import robot.fs.FSRobot;

public class QuestBossHandler extends FSEventHandler {

    public QuestBossHandler(final FSRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        if (this.log.isInfoEnabled()) {
            this.log.info("BOSS出现了");
        }
        this.questBoss();
        this.questBossAnimation();
        if (this.log.isInfoEnabled()) {
            this.log.info("BOSS被推倒了");
        }
        this.questMasterAnimation();
        return "/quest";
    }

    private void questBoss() {
        final Map<String, Object> session = this.robot.getSession();
        final String questId = (String) session.get("questId");
        final String path = String.format("/quest/boss-detail?questId=%s",
                                          questId);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);
        this.resolveInputUserId(html);
    }

    private void questBossAnimation() {
        final Map<String, Object> session = this.robot.getSession();
        final String questId = (String) session.get("questId");
        final String token = (String) session.get("token");
        final String userId = (String) session.get("userId");
        final String path = String.format("/quest/boss-animation?questId=%s&token=%s&light=%s",
                                          questId,
                                          token,
                                          userId);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);
    }

    private void questMasterAnimation() {
        final Map<String, Object> session = this.robot.getSession();
        final String token = (String) session.get("token");
        final String path = String.format("/quest/quest-master-animation?token=%s",
                                          token);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);
    }

}
