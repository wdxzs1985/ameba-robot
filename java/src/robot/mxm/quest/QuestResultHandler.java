package robot.mxm.quest;

import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;
import robot.mxm.PointPrinter;

public class QuestResultHandler extends MxmEventHandler {

	private final PointPrinter printer = new PointPrinter();

	public QuestResultHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		String userId = (String) session.get("userId");
		String token = (String) session.get("token");
		String path = String.format("/touch/%s/result", userId);
		List<BasicNameValuePair> nvps = this.createNameValuePairs();
		nvps.add(new BasicNameValuePair("commentFlg", "false"));
		nvps.add(new BasicNameValuePair("redirectType", "TOUCH_RESULT"));
		nvps.add(new BasicNameValuePair("token", token));
		final String html = this.httpPost(path, nvps);
		if (this.log.isInfoEnabled()) {
			this.printer.printPoint(this.log, html);
			this.printer.printRanking(this.log, html);
			this.printer.printTreature(this.log, html);
		}
		return "/quest";
	}

}
