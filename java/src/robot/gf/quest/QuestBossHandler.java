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
		this.questBoss();
		this.questBossAnimation();
		this.questMasterAnimation();
		this.questMaster();
		if (this.log.isInfoEnabled()) {
			this.log.info("ボス撃破");
		}
		return "/quest";
	}

	private void questBoss() {
		Map<String, Object> session = this.robot.getSession();
		String questId = (String) session.get("questId");
		String path = String.format("/quest/quest-boss?questId=%s", questId);
		String html = this.httpGet(path);
		this.resolveJavascriptToken(html);
	}

	private void questBossAnimation() {
		Map<String, Object> session = this.robot.getSession();
		String token = (String) session.get("token");
		String path = String.format("/quest/boss-animation?token=%s", token);
		String html = this.httpGet(path);
		this.resolveJavascriptToken(html);
	}

	private void questMasterAnimation() {
		Map<String, Object> session = this.robot.getSession();
		String questId = (String) session.get("questId");
		String token = (String) session.get("token");
		String path = String
				.format("/quest/quest-master-animation?questId=%s&token=%s&player=false",
						questId, token);
		String html = this.httpGet(path);
		this.resolveJavascriptToken(html);
	}

	private void questMaster() {
		Map<String, Object> session = this.robot.getSession();
		String questId = (String) session.get("questId");
		String token = (String) session.get("token");
		String path = String.format("/quest/quest-master?questId=%s&token=%s",
				questId, token);
		String html = this.httpGet(path);
		this.resolveJavascriptToken(html);
	}

}
