package robot.tnk47;

import robot.AbstractRobot;
import robot.LoginHandler;
import robot.tnk47.battle.BattleAnimationHandler;
import robot.tnk47.battle.BattleCheckHandler;
import robot.tnk47.battle.BattleDetailHandler;
import robot.tnk47.battle.BattleHandler;
import robot.tnk47.battle.BattleResultHandler;
import robot.tnk47.battle.PrefectureBattleListHandler;
import robot.tnk47.battle.PrefectureBattleResultHandler;
import robot.tnk47.gacha.BattleGachaHandler;
import robot.tnk47.gacha.StampGachaHandler;
import robot.tnk47.gift.GiftHandler;
import robot.tnk47.quest.QuestBossHandler;
import robot.tnk47.quest.QuestHandler;
import robot.tnk47.quest.QuestStageDetailHandler;
import robot.tnk47.quest.QuestStageForwardHandler;
import robot.tnk47.quest.QuestStatusUpHandler;
import robot.tnk47.upgrade.UpgradeAnimationHandler;
import robot.tnk47.upgrade.UpgradeAutoConfirmHandler;
import robot.tnk47.upgrade.UpgradeHandler;
import robot.tnk47.upgrade.UpgradeSelectBaseHandler;

public class Tnk47Robot extends AbstractRobot {

	public static final String HOST = "http://tnk47.ameba.jp";

	public static final String VERSION = "天下自动脚本  0.5.0";

	public void init() {
		this.registerHandler("/", new HomeHandler(this));
		this.registerHandler("/login", new LoginHandler(this));
		this.registerHandler("/mypage", new MypageHandler(this));
		this.registerHandler("/event-infomation", new EvnetInfomationHandler(
				this));
		this.registerHandler("/gacha/stamp-gacha", new StampGachaHandler(this));
		this.registerHandler("/gacha/battle-gacha",
				new BattleGachaHandler(this));
		//
		this.registerHandler("/gift", new GiftHandler(this));
		// 活动控制器：合战
		this.registerHandler("/event/pointrace", new BattleHandler(this));
		// 控制器：合战
		this.registerHandler("/battle", new BattleHandler(this));
		this.registerHandler("/battle/detail", new BattleDetailHandler(this));
		this.registerHandler("/battle/battle-check", new BattleCheckHandler(
				this));
		this.registerHandler("/battle/battle-animation",
				new BattleAnimationHandler(this));
		this.registerHandler("/battle/battle-result", new BattleResultHandler(
				this));
		this.registerHandler("/battle/prefecture-battle-list",
				new PrefectureBattleListHandler(this));
		this.registerHandler("/battle/prefecture-battle-result",
				new PrefectureBattleResultHandler(this));
		// 控制器：冒险
		this.registerHandler("/quest", new QuestHandler(this));
		this.registerHandler("/quest/stage/detail",
				new QuestStageDetailHandler(this));
		this.registerHandler("/quest/stage/forward",
				new QuestStageForwardHandler(this));
		this.registerHandler("/quest/boss-animation",
				new QuestBossHandler(this));
		this.registerHandler("/use-item", new UseItemHandler(this));
		this.registerHandler("/status-up", new QuestStatusUpHandler(this));
		// 强化
		this.registerHandler("/upgrade", new UpgradeHandler(this));
		this.registerHandler("/upgrade/select-base",
				new UpgradeSelectBaseHandler(this));
		this.registerHandler("/upgrade/auto-upgrade-confirm",
				new UpgradeAutoConfirmHandler(this));
		this.registerHandler("/upgrade/upgrade-animation",
				new UpgradeAnimationHandler(this));
	}

	@Override
	public String getHost() {
		return HOST;
	}

	public int getDelay() {
		String key = "Tnk47Robot.delay";
		String value = this.getConfig().getProperty(key, "5");
		return Integer.valueOf(value);
	}

	public boolean isStampGachaEnable() {
		String key = "Tnk47Robot.stampGachaEnable";
		String value = this.getConfig().getProperty(key, "false");
		return Boolean.valueOf(value);
	}

	public boolean isEventEnable() {
		String key = "Tnk47Robot.eventEnable";
		String value = this.getConfig().getProperty(key, "false");
		return Boolean.valueOf(value);
	}

	public boolean isGiftEnable() {
		String key = "Tnk47Robot.giftEnable";
		String value = this.getConfig().getProperty(key, "false");
		return Boolean.valueOf(value);
	}

	public boolean isQuestEnable() {
		String key = "Tnk47Robot.questEnable";
		String value = this.getConfig().getProperty(key, "false");
		return Boolean.valueOf(value);
	}

	public boolean isBattleEnable() {
		String key = "Tnk47Robot.battleEnable";
		String value = this.getConfig().getProperty(key, "false");
		return Boolean.valueOf(value);
	}

	public boolean isUpgradeEnable() {
		String key = "Tnk47Robot.upgradeEnable";
		String value = this.getConfig().getProperty(key, "false");
		return Boolean.valueOf(value);
	}

	public boolean isUseTodayPowerRegenItem() {
		String key = "Tnk47Robot.useTodayPowerRegenItem";
		String value = this.getConfig().getProperty(key, "false");
		return Boolean.valueOf(value);
	}

	public boolean isUseHalfPowerRegenItem() {
		String key = "Tnk47Robot.useHalfPowerRegenItem";
		String value = this.getConfig().getProperty(key, "false");
		return Boolean.valueOf(value);
	}

	public boolean isUseFullPowerRegenItem() {
		String key = "Tnk47Robot.useFullPowerRegenItem";
		String value = this.getConfig().getProperty(key, "false");
		return Boolean.valueOf(value);
	}

	public boolean isAutoSelectStage() {
		String key = "Tnk47Robot.autoSelectStage";
		String value = this.getConfig().getProperty(key, "true");
		return Boolean.valueOf(value);
	}

	public String getQuestId() {
		String key = "Tnk47Robot.questId";
		String value = this.getConfig().getProperty(key, "1");
		return value;
	}

	public String getareaId() {
		String key = "Tnk47Robot.areaId";
		String value = this.getConfig().getProperty(key, "1");
		return value;
	}

	public String getStageId() {
		String key = "Tnk47Robot.stageId";
		String value = this.getConfig().getProperty(key, "1");
		return value;
	}

	public boolean isUseStaminaToday() {
		String key = "Tnk47Robot.useStaminaToday";
		String value = this.getConfig().getProperty(key, "false");
		return Boolean.valueOf(value);
	}

	public boolean isUseStamina50() {
		String key = "Tnk47Robot.useStamina50";
		String value = this.getConfig().getProperty(key, "false");
		return Boolean.valueOf(value);
	}

	public boolean isUseStamina100() {
		String key = "Tnk47Robot.useStamina100";
		String value = this.getConfig().getProperty(key, "false");
		return Boolean.valueOf(value);
	}

	public int getStaminaUpLimit() {
		String key = "Tnk47Robot.staminaUpLimit";
		String value = this.getConfig().getProperty(key, "0");
		return Integer.valueOf(value);
	}

	public int getPowerUpLimit() {
		String key = "Tnk47Robot.powerUpLimit";
		String value = this.getConfig().getProperty(key, "0");
		return Integer.valueOf(value);
	}

	public int getMinBattlePoint() {
		String key = "Tnk47Robot.minBattlePoint";
		String value = this.getConfig().getProperty(key, "0");
		return Integer.valueOf(value);
	}

}
