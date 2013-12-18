package robot.gf.quest;

import java.util.Map;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class QuestBossHandler extends GFEventHandler {

    public QuestBossHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        if (this.log.isInfoEnabled()) {
            this.log.info("BOSS妹纸出现了");
        }
        this.questBoss();
        this.questBossAnimation();
        this.questMasterAnimation();
        this.questMaster();
        if (this.log.isInfoEnabled()) {
            this.log.info("BOSS妹纸被推倒了");
        }
        return "/quest";
    }

    private void questBoss() {
        final Map<String, Object> session = this.robot.getSession();
        final String questId = (String) session.get("questId");
        final String path = String.format("/quest/quest-boss?questId=%s",
                                          questId);
        final String html = this.httpGet(path);
        this.resolveJavascriptToken(html);
    }

    private void questBossAnimation() {
        final Map<String, Object> session = this.robot.getSession();
        final String token = (String) session.get("token");
        final String path = String.format("/quest/boss-animation?token=%s",
                                          token);
        final String html = this.httpGet(path);
        this.resolveJavascriptToken(html);
    }

    private void questMasterAnimation() {
        final Map<String, Object> session = this.robot.getSession();
        final String questId = (String) session.get("questId");
        final String token = (String) session.get("token");
        final String path = String.format("/quest/quest-master-animation?questId=%s&token=%s&player=false",
                                          questId,
                                          token);
        final String html = this.httpGet(path);
        this.resolveJavascriptToken(html);
    }

    private void questMaster() {
        final Map<String, Object> session = this.robot.getSession();
        final String questId = (String) session.get("questId");
        final String token = (String) session.get("token");
        final String path = String.format("/quest/quest-master?questId=%s&token=%s",
                                          questId,
                                          token);
        final String html = this.httpGet(path);
        this.resolveJavascriptToken(html);
    }

}
