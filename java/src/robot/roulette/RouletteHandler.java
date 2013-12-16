package robot.roulette;

import net.sf.json.JSONObject;
import robot.AbstractEventHandler;

public class RouletteHandler extends AbstractEventHandler<RouletteRobot> {

	public RouletteHandler(final RouletteRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		String path = "/api/roulette/check?device=ios&gender=male&age=28";
		JSONObject jsonResponse = this.httpGetJSON(path);
		if (!jsonResponse.getBoolean("played")) {
			String name = jsonResponse.getString("name");
			if (this.log.isInfoEnabled()) {
				this.log.info(String.format("hello, %s", name));
			}
			return "/roulette";
		}
		this.sleep();
		return "/mypage";
	}

	private void sleep() {
		final int delay = this.robot.getDelay();
		this.log.info(String.format("休息 %d min _(:3_", delay));
		try {
			Thread.sleep(delay * 60 * 1000);
		} catch (final InterruptedException e) {
		}
	}
}
