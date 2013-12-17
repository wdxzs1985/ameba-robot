package robot.mxm;

import robot.AbstractRobot;
import robot.LoginHandler;
import robot.mxm.quest.QuestSummonHandler;
import robot.mxm.quest.QuestUserRoomHandler;
import robot.mxm.quest.QuestUserlistHandler;

public class MxmRobot extends AbstractRobot {

	public static final String HOST = "http://mxm.ameba.jp";

	public static final String VERSION = "MXM 0.0.1";

	@Override
	public void init() {
		this.registerHandler("/", new HomeHandler(this));
		this.registerHandler("/login", new LoginHandler(this));
		this.registerHandler("/mypage", new MypageHandler(this));
		// quest
		this.registerHandler("/quest/user/list", new QuestUserlistHandler(this));
		this.registerHandler("/quest/user/room", new QuestUserRoomHandler(this));
		this.registerHandler("/quest/summon", new QuestSummonHandler(this));
	}

	@Override
	public String getHost() {
		return MxmRobot.HOST;
	}

	public boolean isQuestEnable() {
		final String key = "MxmRobot.questEnable";
		final String value = this.getConfig().getProperty(key, "false");
		return Boolean.valueOf(value);
	}
}
