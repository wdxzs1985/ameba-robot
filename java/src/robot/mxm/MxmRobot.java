package robot.mxm;

import robot.AbstractRobot;
import robot.LoginHandler;
import robot.mxm.monster.MonsterHandler;
import robot.mxm.quest.QuestHandler;
import robot.mxm.quest.QuestResultHandler;
import robot.mxm.quest.QuestStageClearHandler;
import robot.mxm.quest.QuestSummonHandler;
import robot.mxm.quest.QuestUserListHandler;
import robot.mxm.quest.QuestUserRoomHandler;
import robot.mxm.raid.RaidAnimationHandler;
import robot.mxm.raid.RaidAttackHandler;
import robot.mxm.raid.RaidEncountHandler;
import robot.mxm.raid.RaidHelpListHandler;
import robot.mxm.raid.RaidHistoryHandler;
import robot.mxm.raid.RaidTargetHandler;
import robot.mxm.raid.RaidTopHandler;
import robot.mxm.raid.RaidWinAnimationHandler;
import robot.mxm.raid.RaidWinResultHandler;

public class MxmRobot extends AbstractRobot {

	public static final String HOST = "http://mxm.ameba.jp";

	public static final String VERSION = "MXM 0.0.1";

	@Override
	public void init() {
		this.registerHandler("/", new HomeHandler(this));
		this.registerHandler("/login", new LoginHandler(this));
		this.registerHandler("/mypage", new MypageHandler(this));
		this.registerHandler("/getRing", new GetRingHandler(this));
		this.registerHandler("/monster", new MonsterHandler(this));
		// quest
		this.registerHandler("/quest", new QuestHandler(this));
		this.registerHandler("/quest/user/list", new QuestUserListHandler(this));
		this.registerHandler("/quest/user/room", new QuestUserRoomHandler(this));
		this.registerHandler("/quest/summon", new QuestSummonHandler(this));
		this.registerHandler("/quest/result", new QuestResultHandler(this));
		this.registerHandler("/quest/stageClear", new QuestStageClearHandler(
				this));
		// raid
		this.registerHandler("/raid/top", new RaidTopHandler(this));
		this.registerHandler("/raid/history", new RaidHistoryHandler(this));
		this.registerHandler("/raid/animation", new RaidAnimationHandler(this));
		this.registerHandler("/raid/win/animation",
				new RaidWinAnimationHandler(this));
		this.registerHandler("/raid/win/result", new RaidWinResultHandler(this));
		this.registerHandler("/raid/help/list", new RaidHelpListHandler(this));
		this.registerHandler("/raid/encount", new RaidEncountHandler(this));
		this.registerHandler("/raid/target", new RaidTargetHandler(this));
		this.registerHandler("/raid/attack", new RaidAttackHandler(this));
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

	public boolean isRaidEnable() {
		final String key = "MxmRobot.raidEnable";
		final String value = this.getConfig().getProperty(key, "false");
		return Boolean.valueOf(value);
	}

	public String getUserRoom() {
		final String key = "MxmRobot.userRoom";
		final String value = this.getConfig().getProperty(key);
		return value;
	}
}
