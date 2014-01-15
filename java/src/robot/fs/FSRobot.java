package robot.fs;

import java.util.Map;

import robot.AbstractRobot;
import robot.LoginHandler;
import robot.fs.quest.QuestAnimationHandler;
import robot.fs.quest.QuestBossHandler;
import robot.fs.quest.QuestHandler;
import robot.fs.quest.QuestSearchHandler;

public class FSRobot extends AbstractRobot {

    public static final String HOST = "http://fs.ameba.jp";

    public static final String VERSION = "天空印花脚本  0.0.1";

    @Override
    public void init() {
        this.registerHandler("/", new HomeHandler(this));
        this.registerHandler("/login", new LoginHandler(this));
        this.registerHandler("/mypage", new MypageHandler(this));

        this.registerHandler("/quest", new QuestHandler(this));
        this.registerHandler("/quest/search", new QuestSearchHandler(this));
        this.registerHandler("/quest/animation",
                             new QuestAnimationHandler(this));
        this.registerHandler("/quest/boss", new QuestBossHandler(this));
    }

    @Override
    public void reset() {
        final Map<String, Object> session = this.getSession();
        session.put("isMypage", false);
        session.put("isGiftEnable", this.isGiftEnable());
        session.put("isQuestEnable", this.isQuestEnable());
        session.put("isBattleEnable", this.isBattleEnable());
        session.put("isUpgradeEnable", this.isUpgradeEnable());
    }

    @Override
    public String getHost() {
        return FSRobot.HOST;
    }

    public boolean isGiftEnable() {
        final String key = "FSRobot.giftEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isQuestEnable() {
        final String key = "FSRobot.questEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isBattleEnable() {
        final String key = "FSRobot.battleEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isUpgradeEnable() {
        final String key = "FSRobot.upgradeEnable";
        final String value = this.getConfig().getProperty(key, "false");
        return Boolean.valueOf(value);
    }

    public boolean isAutoSelectStage() {
        final String key = "FSRobot.autoSelectStage";
        final String value = this.getConfig().getProperty(key, "true");
        return Boolean.valueOf(value);
    }

    public String getQuestId() {
        final String key = "FSRobot.questId";
        final String value = this.getConfig().getProperty(key, "1");
        return value;
    }

    public String getStageId() {
        final String key = "FSRobot.stageId";
        final String value = this.getConfig().getProperty(key, "1");
        return value;
    }

}
