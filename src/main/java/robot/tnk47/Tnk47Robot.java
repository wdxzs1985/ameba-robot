package robot.tnk47;

import java.util.Map;

import robot.AbstractRobot;
import robot.LoginHandler;
import robot.tnk47.battle.BattleAnimationHandler;
import robot.tnk47.battle.BattleCheckHandler;
import robot.tnk47.battle.BattleDetailHandler;
import robot.tnk47.battle.BattleHandler;
import robot.tnk47.battle.BattleResultHandler;
import robot.tnk47.battle.PrefectureBattleListHandler;
import robot.tnk47.battle.PrefectureBattleResultHandler;
import robot.tnk47.duel.DuelBattleAnimationHandler;
import robot.tnk47.duel.DuelBattleCheckHandler;
import robot.tnk47.duel.DuelBattleResultHandler;
import robot.tnk47.duel.DuelBattleSelectHandler;
import robot.tnk47.duel.DuelHandler;
import robot.tnk47.gacha.BattleGachaHandler;
import robot.tnk47.gacha.BoxGachaHandler;
import robot.tnk47.gacha.GachaHandler;
import robot.tnk47.gacha.StampGachaHandler;
import robot.tnk47.gacha.TicketGachaHandler;
import robot.tnk47.guildbattle.GuildBattleAnimationHandler;
import robot.tnk47.guildbattle.GuildBattleChargeHandler;
import robot.tnk47.guildbattle.GuildBattleCheckHandler;
import robot.tnk47.guildbattle.GuildBattleHandler;
import robot.tnk47.guildbattle.GuildBattleResultHandler;
import robot.tnk47.guildbattle.GuildBattleSelectHandler;
import robot.tnk47.guildbattle.GuildBattleSkillHandler;
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
import robot.tnk47.raid.RaidBattleAnimationHandler;
import robot.tnk47.raid.RaidBattleEncountHandler;
import robot.tnk47.raid.RaidBattleHandler;
import robot.tnk47.raid.RaidBattleResultHandler;
import robot.tnk47.raid.RaidHandler;
import robot.tnk47.raid.RaidStageForwardHandler;
import robot.tnk47.raid.RaidStageHandler;
import robot.tnk47.raid.model.RaidBattleDamageMap;
import robot.tnk47.upgrade.UpgradeAnimationHandler;
import robot.tnk47.upgrade.UpgradeAutoConfirmHandler;
import robot.tnk47.upgrade.UpgradeHandler;
import robot.tnk47.upgrade.UpgradeSelectBaseHandler;

public class Tnk47Robot extends AbstractRobot {

    public static final String HOST = "http://tnk47.ameba.jp";

    public static final String VERSION = "天下自动脚本  10.3";

    @Override
    public void initHandlers() {
        this.registerHandler("/", new HomeHandler(this));
        this.registerHandler("/login", new LoginHandler(this));
        this.registerHandler("/mypage", new MypageHandler(this));
        this.registerHandler("/event-infomation",
                             new EventInfomationHandler(this));
        this.registerHandler("/gacha", new GachaHandler(this));
        this.registerHandler("/gacha/ticket-gacha",
                             new TicketGachaHandler(this));
        this.registerHandler("/gacha/box-gacha", new BoxGachaHandler(this));
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
        final RaidBattleDamageMap damageMap = new RaidBattleDamageMap();
        this.registerHandler("/raid", new RaidHandler(this, damageMap));
        this.registerHandler("/raid/battle", new RaidBattleHandler(this,
                                                                   damageMap));
        this.registerHandler("/raid/battle-animation",
                             new RaidBattleAnimationHandler(this, damageMap));
        this.registerHandler("/raid/battle-result",
                             new RaidBattleResultHandler(this, damageMap));
        this.registerHandler("/raid/battle-encount",
                             new RaidBattleEncountHandler(this, damageMap));
        this.registerHandler("/raid/stage", new RaidStageHandler(this));
        this.registerHandler("/raid/stage-forward",
                             new RaidStageForwardHandler(this));

        // 控制器：戦神リーグ
        this.registerHandler("/duel", new DuelHandler(this));
        this.registerHandler("/duel/duel-battle-select",
                             new DuelBattleSelectHandler(this));
        this.registerHandler("/duel/duel-battle-check",
                             new DuelBattleCheckHandler(this));
        this.registerHandler("/duel/duel-battle-animation",
                             new DuelBattleAnimationHandler(this));
        this.registerHandler("/duel/duel-battle-result",
                             new DuelBattleResultHandler(this));

        // 控制器：同盟戦
        this.registerHandler("/guildbattle", new GuildBattleHandler(this));
        this.registerHandler("/guildbattle/charge",
                             new GuildBattleChargeHandler(this));
        this.registerHandler("/guildbattle/skill",
                             new GuildBattleSkillHandler(this));
        this.registerHandler("/guildbattle/select",
                             new GuildBattleSelectHandler(this));
        this.registerHandler("/guildbattle/check",
                             new GuildBattleCheckHandler(this));
        this.registerHandler("/guildbattle/animation",
                             new GuildBattleAnimationHandler(this));
        this.registerHandler("/guildbattle/result",
                             new GuildBattleResultHandler(this));

        // 控制器：天下統一戦
        // this.registerHandler("/conquest", new ConquestHandler(this));
        // this.registerHandler("/conquest/battle-list",
        // new ConquestBattleListHandler(this));
        // this.registerHandler("/conquest/battle-check",
        // new ConquestBattleCheckHandler(this));
        // this.registerHandler("/conquest/battle-animation",
        // new ConquestAnimationHandler(this));
        // this.registerHandler("/conquest/conquest-result",
        // new ConquestResultHandler(this));
    }

    @Override
    public void reset() {
        final Map<String, Object> session = this.getSession();
        session.put("isMypage", false);
        session.put("isStampGachaEnable", this.isStampGachaEnable());
        session.put("isGachaEnable", this.isGachaEnable());
        session.put("isGiftEnable", this.isGiftEnable());
        session.put("isQuestEnable", this.isQuestEnable());
        session.put("isBattleEnable", this.isBattleEnable());
        session.put("isUpgradeEnable", this.isUpgradeEnable());
        session.put("isDuelEnable", this.isDuelEnable());

        session.put("isEventEnable", true);
        session.put("isMarathonEnable", this.isMarathonEnable());
        session.put("isPointRaceEnable", this.isPointRaceEnable());
        session.put("isRaidEnable", this.isRaidEnable());
        session.put("isGuildBattleEnable", this.isGuildBattleEnable());
        session.put("isConquestEnable", this.isConquestEnable());

        session.put("isQuestCardFull", false);
        session.put("isQuestFindAll", false);
        session.put("isPointRace", false);
        session.put("isBattlePowerFull", false);
        session.put("isBattlePowerOut", false);
        session.put("isBattlePointEnough", false);
        session.put("isLimitedOpen", false);
    }

    @Override
    protected String getHost() {
        return Tnk47Robot.HOST;
    }

    public boolean isStampGachaEnable() {
        final String key = "Tnk47Robot.stampGachaEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isMarathonEnable() {
        final String key = "Tnk47Robot.marathonEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isRaidEnable() {
        final String key = "Tnk47Robot.raidEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isPointRaceEnable() {
        final String key = "Tnk47Robot.pointRaceEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isGuildBattleEnable() {
        final String key = "Tnk47Robot.guildBattleEnable";
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

    public int getMinDamageRatio() {
        final String key = "Tnk47Robot.minDamageRatio";
        final String value = this.getConfig().getProperty(key, "4");
        return Integer.valueOf(value);
    }

    public boolean isEcoMode() {
        final String key = "Tnk47Robot.ecoMode";
        final String value = this.getConfig().getProperty(key, "true");
        return Boolean.valueOf(value);
    }

    public boolean isGachaEnable() {
        final String key = "Tnk47Robot.gachaEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isDuelEnable() {
        final String key = "Tnk47Robot.duelEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isConquestEnable() {
        final String key = "Tnk47Robot.conquestEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }
}
