package robot.mxm;

import robot.AbstractRobot;
import robot.LoginHandler;

public class MxmRobot extends AbstractRobot {

	public static final String HOST = "http://mxm.ameba.jp";

	public static final String VERSION = "MXM 0.0.1";

	@Override
	public void init() {
		this.registerHandler("/", new HomeHandler(this));
		this.registerHandler("/login", new LoginHandler(this));
		this.registerHandler("/mypage", new MypageHandler(this));

	}

	@Override
	public String getHost() {
		return MxmRobot.HOST;
	}

}
