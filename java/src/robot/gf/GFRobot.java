package robot.gf;

import robot.AbstractRobot;
import robot.LoginHandler;
import robot.gf.cupid.CupidExecHandler;
import robot.gf.cupid.CupidHandler;
import robot.gf.cupid.CupidResultHandler;
import robot.gf.gift.GiftHandler;
import robot.gf.gift.GiftReceiveHandler;
import robot.gf.upgrade.UpgradeAnimationHandler;
import robot.gf.upgrade.UpgradeConfirmHandler;
import robot.gf.upgrade.UpgradeHandler;

public class GFRobot extends AbstractRobot {

	public static final String HOST = "http://vcard.ameba.jp";

	public static final String VERSION = "GF自动脚本  0.0.1";

	public void init() {
		this.registerHandler("/", new HomeHandler(this));
		this.registerHandler("/login", new LoginHandler(this));
		this.registerHandler("/mypage", new MypageHandler(this));
		// cupid
		this.registerHandler("/cupid", new CupidHandler(this));
		this.registerHandler("/cupid/exec", new CupidExecHandler(this));
		this.registerHandler("/cupid/result", new CupidResultHandler(this));
		// gift
		this.registerHandler("/gift", new GiftHandler(this));
		this.registerHandler("/gift/receive", new GiftReceiveHandler(this));
		// upgrade
		this.registerHandler("/upgrade", new UpgradeHandler(this));
		this.registerHandler("/upgrade/confirm",
				new UpgradeConfirmHandler(this));
		this.registerHandler("/upgrade/animation", new UpgradeAnimationHandler(
				this));

	}

	@Override
	public String getHost() {
		return GFRobot.HOST;
	}

	public int getDelay() {
		final String key = "GFRobot.delay";
		final String value = this.getConfig().getProperty(key, "5");
		return Integer.valueOf(value);
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

	public boolean isBattleEnable() {
		final String key = "GFRobot.battleEnable";
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

}
