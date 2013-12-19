package robot.mxm.raid;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public class RaidTopHandler extends MxmEventHandler {

	public RaidTopHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		return "/mypage";
	}

}
