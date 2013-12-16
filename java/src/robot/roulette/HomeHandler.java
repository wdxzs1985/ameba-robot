package robot.roulette;

import net.sf.json.JSONObject;
import robot.AbstractEventHandler;

public class HomeHandler extends AbstractEventHandler<RouletteRobot> {

	public HomeHandler(final RouletteRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		JSONObject jsonResponse = this.httpGetJSON("/api/user/me");
		if (jsonResponse.containsKey("name")) {
			String name = jsonResponse.getString("name");
			if (this.log.isInfoEnabled()) {
				this.log.info(String.format("hello, %s", name));
			}
			return "/mypage";
		}
		return "/login";
	}
}
