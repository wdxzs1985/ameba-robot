package robot.mxm.raid;

import java.util.Map;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;
import robot.mxm.PointPrinter;

public class RaidWinResultHandler extends MxmEventHandler {

	private final PointPrinter printer = new PointPrinter();

	public RaidWinResultHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		String eventId = (String) session.get("eventId");
		String raidId = (String) session.get("raidId");
		String path = String.format("/raid/%s/%s/win/result", eventId, raidId);
		final String html = this.httpGet(path);
		if (this.log.isInfoEnabled()) {
			this.printer.printPoint(this.log, html);
			this.printer.printRanking(this.log, html);
			this.printer.printTreature(this.log, html);
		}
		return "/raid/history";
	}
}
