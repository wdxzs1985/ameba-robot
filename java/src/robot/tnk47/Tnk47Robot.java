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
import robot.tnk47.marathon.MarathonHandler;
import robot.tnk47.marathon.MarathonMissionAnimationHandler;
import robot.tnk47.marathon.MarathonMissionHandler;
import robot.tnk47.marathon.MarathonMissionResultHandler;
import robot.tnk47.marathon.MarathonNotificationHandler;
import robot.tnk47.marathon.MarathonStageBossHandler;
import robot.tnk47.marathon.MarathonStageDetailHandler;
import robot.tnk47.marathon.MarathonStageForwardHandler;
import robot.tnk47.marathon.MarathonStageHandler;
import robot.tnk47.quest.QuestBossHandler;
import robot.tnk47.quest.QuestHandler;
import robot.tnk47.quest.QuestStageDetailHandler;
import robot.tnk47.quest.QuestStageForwardHandler;
import robot.tnk47.quest.QuestStatusUpHandler;
import robot.tnk47.raid.RaidBattleEncountHandler;
import robot.tnk47.raid.RaidBattleHandler;
import robot.tnk47.raid.RaidBattleListHandler;
import robot.tnk47.raid.RaidBattleResultHandler;
import robot.tnk47.raid.RaidHandler;
import robot.tnk47.raid.RaidStageForwardHandler;
import robot.tnk47.raid.RaidStageHandler;
import robot.tnk47.upgrade.UpgradeAnimationHandler;
import robot.tnk47.upgrade.UpgradeAutoConfirmHandler;
import robot.tnk47.upgrade.UpgradeHandler;
import robot.tnk47.upgrade.UpgradeSelectBaseHandler;

public class Tnk47Robot extends AbstractRobot {

    public static final String HOST = "http://tnk47.ameba.jp";

    public static final String VERSION = "天下自动脚本  0.7.0";

    @Override
    public void init() {
        this.registerHandler("/", new HomeHandler(this));
        this.registerHandler("/login", new LoginHandler(this));
        this.registerHandler("/mypage", new MypageHandler(this));
        this.registerHandler("/event-infomation",
                             new EventInfomationHandler(this));
        this.registerHandler("/gacha/stamp-gacha", new StampGachaHandler(this));
        this.registerHandler("/gacha/battle-gacha",
                             new BattleGachaHandler(this));
        //
        this.registerHandler("/gift", new GiftHandler(this));
        // 控制器：合战活动
        this.registerHandler("/pointrace", new BattleHandler(this));
        // 控制器：合战
        this.registerHandler("/battle", new BattleHandler(this));
        this.registerHandler("/battle/detail", new BattleDetailHandler(this));
        this.registerHandler("/battle/battle-check",
                             new BattleCheckHandler(this));
        this.registerHandler("/battle/battle-animation",
                             new BattleAnimationHandler(this));
        this.registerHandler("/battle/battle-result",
                             new BattleResultHandler(this));
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
        // 控制器：爬塔活动
        this.registerHandler("/marathon", new MarathonHandler(this));
        this.registerHandler("/marathon/stage", new MarathonStageHandler(this));
        this.registerHandler("/marathon/stage/detail",
                             new MarathonStageDetailHandler(this));
        this.registerHandler("/marathon/stage/forward",
                             new MarathonStageForwardHandler(this));
        this.registerHandler("/marathon/stage/boss",
                             new MarathonStageBossHandler(this));
        this.registerHandler("/marathon/mission",
                             new MarathonMissionHandler(this));
        this.registerHandler("/marathon/mission/animation",
                             new MarathonMissionAnimationHandler(this));
        this.registerHandler("/marathon/mission/result",
                             new MarathonMissionResultHandler(this));
        this.registerHandler("/marathon/notification",
                             new MarathonNotificationHandler(this));
        // 强化
        this.registerHandler("/upgrade", new UpgradeHandler(this));
        this.registerHandler("/upgrade/select-base",
                             new UpgradeSelectBaseHandler(this));
        this.registerHandler("/upgrade/auto-upgrade-confirm",
                             new UpgradeAutoConfirmHandler(this));
        this.registerHandler("/upgrade/upgrade-animation",
                             new UpgradeAnimationHandler(this));

        // 控制器：RAID
        this.registerHandler("/raid", new RaidHandler(this));
        this.registerHandler("/raid/battle-list",
                             new RaidBattleListHandler(this));
        this.registerHandler("/raid/battle", new RaidBattleHandler(this));
        this.registerHandler("/raid/battle-result",
                             new RaidBattleResultHandler(this));
        this.registerHandler("/raid/battle-encount",
                             new RaidBattleEncountHandler(this));
        this.registerHandler("/raid/stage", new RaidStageHandler(this));
        this.registerHandler("/raid/stage-forward",
                             new RaidStageForwardHandler(this));

    }

    @Override
    public String getHost() {
        return Tnk47Robot.HOST;
    }

    public boolean isStampGachaEnable() {
        final String key = "Tnk47Robot.stampGachaEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isEventEnable() {
        final String key = "Tnk47Robot.eventEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isGiftEnable() {
        final String key = "Tnk47Robot.giftEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isQuestEnable() {
        final String key = "Tnk47Robot.questEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isBattleEnable() {
        final String key = "Tnk47Robot.battleEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isUpgradeEnable() {
        final String key = "Tnk47Robot.upgradeEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isUseTodayPowerRegenItem() {
        final String key = "Tnk47Robot.useTodayPowerRegenItem";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isUseHalfPowerRegenItem() {
        final String key = "Tnk47Robot.useHalfPowerRegenItem";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isUseFullPowerRegenItem() {
        final String key = "Tnk47Robot.useFullPowerRegenItem";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isAutoSelectStage() {
        final String key = "Tnk47Robot.autoSelectStage";
        final String value = this.getConfig().getProperty(key, "true");
        return Boolean.valueOf(value);
    }

    public String getSelectedQuestId() {
        final String key = "Tnk47Robot.selectedQuestId";
        final String value = this.getConfig().getProperty(key, "1");
        return value;
    }

    public String getSelectedAreaId() {
        final String key = "Tnk47Robot.selectedAreaId";
        final String value = this.getConfig().getProperty(key, "1");
        return value;
    }

    public String getSelectedStageId() {
        final String key = "Tnk47Robot.selectedStageId";
        final String value = this.getConfig().getProperty(key, "1");
        return value;
    }

    public boolean isUseStaminaToday() {
        final String key = "Tnk47Robot.useStaminaToday";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isUseStamina50() {
        final String key = "Tnk47Robot.useStamina50";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isUseStamina100() {
        final String key = "Tnk47Robot.useStamina100";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public int getStaminaUpLimit() {
        final String key = "Tnk47Robot.staminaUpLimit";
        final String value = this.getConfig().getProperty(key, "0");
        return Integer.valueOf(value);
    }

    public int getPowerUpLimit() {
        final String key = "Tnk47Robot.powerUpLimit";
        final String value = this.getConfig().getProperty(key, "0");
        return Integer.valueOf(value);
    }

    public int getMinBattlePoint() {
        final String key = "Tnk47Robot.minBattlePoint";
        final String value = this.getConfig().getProperty(key, "0");
        return Integer.valueOf(value);
    }

    public int getBattlePointFilter() {
        final String key = "Tnk47Robot.battlePointFilter";
        final String value = this.getConfig().getProperty(key, "0");
        return Integer.valueOf(value);
    }

    public String getNotificationUser() {
        final String key = "Tnk47Robot.notificationUser";
        final String value = this.getConfig().getProperty(key);
        return value;
    }

    public boolean isUseGiveItemToday() {
        final String key = "Tnk47Robot.useGiveItemToday";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isUseGiveItem() {
        final String key = "Tnk47Robot.useGiveItem";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isOnlyGiveOne() {
        final String key = "Tnk47Robot.onlyGiveOne";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public int getUseStaminaRatio() {
        final String key = "Tnk47Robot.useStaminaRatio";
        final String value = this.getConfig().getProperty(key, "75");
        return Integer.valueOf(value);
    }

    public boolean isRaidLimitOpen() {
        final String key = "Tnk47Robot.raidLimitOpen";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isUseRaidRegenItem() {
        final String key = "Tnk47Robot.useRaidRegenItem";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isUseRaidSpecialAttack() {
        final String key = "Tnk47Robot.useRaidSpecialAttack";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }
}
