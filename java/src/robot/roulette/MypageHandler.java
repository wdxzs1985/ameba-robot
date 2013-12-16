package robot.roulette;

import java.util.Map;

import net.sf.json.JSONObject;
import robot.AbstractEventHandler;

public class MypageHandler extends AbstractEventHandler<RouletteRobot> {

	public MypageHandler(final RouletteRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		Map<String, Object> session = this.robot.getSession();
		String path = "/api/roulette/check?device=ios&gender=male&age=28";
		JSONObject jsonResponse = this.httpGetJSON(path);
		if (!jsonResponse.getBoolean("played")) {
			String code = jsonResponse.getString("code");
			session.put("code", code);
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
