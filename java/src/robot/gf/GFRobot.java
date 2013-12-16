package robot.gf;

import robot.AbstractRobot;
import robot.LoginHandler;
import robot.gf.cupid.CupidExecHandler;
import robot.gf.cupid.CupidHandler;

public class GFRobot extends AbstractRobot {

	public static final String HOST = "http://vcard.ameba.jp";

	public static final String VERSION = "GF自动脚本  0.0.1";

	public void init() {
		this.registerHandler("/", new HomeHandler(this));
		this.registerHandler("/login", new LoginHandler(this));
		this.registerHandler("/mypage", new MypageHandler(this));
		this.registerHandler("/cupid", new CupidHandler(this));
		this.registerHandler("/cupid/exec", new CupidExecHandler(this));
		//
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

}
