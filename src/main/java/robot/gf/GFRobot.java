package robot.gf;

import java.util.Map;

import robot.AbstractRobot;
import robot.LoginHandler;
import robot.gf.cupid.CupidExecHandler;
import robot.gf.cupid.CupidHandler;
import robot.gf.cupid.CupidResultHandler;
import robot.gf.cupid.CupidStampExecHandler;
import robot.gf.cupid.CupidStampHandler;
import robot.gf.cupid.CupidStampResultHandler;
import robot.gf.gift.GiftHandler;
import robot.gf.gift.GiftReceiveHandler;
import robot.gf.job.JobPaymentHandler;
import robot.gf.job.JobSettingHandler;
import robot.gf.job.JobStartHandler;
import robot.gf.quest.QuestBossHandler;
import robot.gf.quest.QuestDetailHandler;
import robot.gf.quest.QuestHandler;
import robot.gf.quest.QuestRunHandler;
import robot.gf.raidwar.RaidwarBossHandler;
import robot.gf.raidwar.RaidwarHandler;
import robot.gf.raidwar.RaidwarQuestDetailHandler;
import robot.gf.raidwar.RaidwarQuestRunHandler;
import robot.gf.raidwar.RaidwarQuestTouchHandler;
import robot.gf.upgrade.UpgradeAnimationHandler;
import robot.gf.upgrade.UpgradeConfirmHandler;
import robot.gf.upgrade.UpgradeHandler;

public class GFRobot extends AbstractRobot {

    public static final String HOST = "http://vcard.ameba.jp";

    public static final String VERSION = "GF印花脚本  0.0.4";

    @Override
    public void initHandlers() {
        this.registerHandler("/", new HomeHandler(this));
        this.registerHandler("/login", new LoginHandler(this));
        this.registerHandler("/mypage", new MypageHandler(this));
        // cupid
        this.registerHandler("/cupid", new CupidHandler(this));
        this.registerHandler("/cupid/exec", new CupidExecHandler(this));
        this.registerHandler("/cupid/result", new CupidResultHandler(this));
        // cupid stamp
        this.registerHandler("/cupid/stamp", new CupidStampHandler(this));
        this.registerHandler("/cupid/stamp/exec",
                             new CupidStampExecHandler(this));
        this.registerHandler("/cupid/stamp/result",
                             new CupidStampResultHandler(this));
        // gift
        this.registerHandler("/gift", new GiftHandler(this));
        this.registerHandler("/gift/receive", new GiftReceiveHandler(this));
        // upgrade
        this.registerHandler("/upgrade", new UpgradeHandler(this));
        this.registerHandler("/upgrade/confirm",
                             new UpgradeConfirmHandler(this));
        this.registerHandler("/upgrade/animation",
                             new UpgradeAnimationHandler(this));

        // quest
        this.registerHandler("/quest", new QuestHandler(this));
        this.registerHandler("/quest/detail", new QuestDetailHandler(this));
        this.registerHandler("/quest/run", new QuestRunHandler(this));
        this.registerHandler("/quest/boss", new QuestBossHandler(this));

        // job
        this.registerHandler("/job/setting", new JobSettingHandler(this));
        this.registerHandler("/job/start", new JobStartHandler(this));
        this.registerHandler("/job/payment", new JobPaymentHandler(this));

        // raidwar
        this.registerHandler("/raidwar", new RaidwarHandler(this));
        this.registerHandler("/raidwar/quest/detail",
                             new RaidwarQuestDetailHandler(this));
        this.registerHandler("/raidwar/quest/run",
                             new RaidwarQuestRunHandler(this));
        this.registerHandler("/raidwar/quest/touch",
                             new RaidwarQuestTouchHandler(this));
        this.registerHandler("/raidwar/boss", new RaidwarBossHandler(this));
    }

    @Override
    public void reset() {
        final Map<String, Object> session = this.getSession();
        session.put("isMypage", false);
        session.put("isUpgradeEnable", this.isUpgradeEnable());
        session.put("isCupidEnable", this.isCupidEnable());
        session.put("isCupidStampEnable", this.isCupidStampEnable());
        session.put("isGiftEnable", this.isGiftEnable());
        session.put("isJobEnable", this.isJobEnable());
        session.put("isQuestEnable", this.isQuestEnable());
        session.put("isRaidwarEnable", this.isRaidwarEnable());
    }

    @Override
    public String getHost() {
        return GFRobot.HOST;
    }

    public boolean isCupidEnable() {
        final String key = "GFRobot.cupidEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isCupidStampEnable() {
        final String key = "GFRobot.cupidStampEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isGiftEnable() {
        final String key = "GFRobot.giftEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isQuestEnable() {
        final String key = "GFRobot.questEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isJobEnable() {
        final String key = "GFRobot.jobEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isUpgradeEnable() {
        final String key = "GFRobot.upgradeEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isAutoSelectStage() {
        final String key = "GFRobot.autoSelectStage";
        final String value = this.getConfig().getProperty(key, "true");
        return Boolean.valueOf(value);
    }

    public String getQuestId() {
        final String key = "GFRobot.questId";
        final String value = this.getConfig().getProperty(key, "1");
        return value;
    }

    public String getStageId() {
        final String key = "GFRobot.stageId";
        final String value = this.getConfig().getProperty(key, "1");
        return value;
    }

    public boolean isRaidwarEnable() {
        final String key = "GFRobot.raidwarEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

}
