package robot.roulette;

import robot.AbstractRobot;
import robot.LoginHandler;

public class RouletteRobot extends AbstractRobot {

	public static final String HOST = "https://s.amebame.com";

	public static final String VERSION = "Roulette自动脚本  0.0.1";

	public void init() {
		this.registerHandler("/", new HomeHandler(this));
		this.registerHandler("/login", new LoginHandler(this));
		this.registerHandler("/mypage", new MypageHandler(this));
		this.registerHandler("/roulette", new RouletteHandler(this));
	}

	@Override
	public String getHost() {
		return RouletteRobot.HOST;
	}

	public int getDelay() {
		final String key = "RouletteRobot.delay";
		final String value = this.getConfig().getProperty(key, "60");
		return Integer.valueOf(value);
	}

}
