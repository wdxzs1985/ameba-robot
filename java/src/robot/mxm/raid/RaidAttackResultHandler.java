package robot.mxm.raid;

import java.util.Map;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public class RaidAttackResultHandler extends MxmEventHandler {

	public RaidAttackResultHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		String raidId = (String) session.get("raidId");

		String path = String.format("/raid/%s/result", raidId);
		String html = this.httpGet(path);

		this.log.debug(html);

		return "/raid/top";
	}

}
