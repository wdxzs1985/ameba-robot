package robot.tnk47;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

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

public class QuestRobot extends AbstractRobot {

    public static void main(final String[] args) {

        final String setup = args.length > 0 ? args[0] : "setup.properties";
        final Properties config = new Properties();
        InputStream inputConfig = null;
        try {
            inputConfig = FileUtils.openInputStream(new File(setup));
            config.load(inputConfig);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(inputConfig);
        }

        final QuestRobot robot = new QuestRobot(config);
        final Thread thread = new Thread(robot);
        try {
            thread.start();
            thread.join();
        } catch (final InterruptedException e) {
        }
    }

    public static final String HOST = "http://tnk47.ameba.jp";

    public QuestRobot(final Properties config) {
        super(QuestRobot.HOST, config);
        this.registerHandler("/", new HomeHandler(this));
        this.registerHandler("/login", new LoginHandler(this));
        this.registerHandler("/mypage", new MypageHandler(this));
        this.registerHandler("/event-infomation",
                             new EvnetInfomationHandler(this));
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
        // 强化
        this.registerHandler("/upgrade", new UpgradeHandler(this));
        this.registerHandler("/upgrade/select-base",
                             new UpgradeSelectBaseHandler(this));
        this.registerHandler("/upgrade/auto-upgrade-confirm",
                             new UpgradeAutoConfirmHandler(this));
        this.registerHandler("/upgrade/upgrade-animation",
                             new UpgradeAnimationHandler(this));
    }
}
