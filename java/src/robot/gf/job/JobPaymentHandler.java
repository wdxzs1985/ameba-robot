package robot.gf.job;

import java.util.Map;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class JobPaymentHandler extends GFEventHandler {

	public JobPaymentHandler(final GFRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		Map<String, Object> session = this.robot.getSession();
		String token = (String) session.get("token");

		String path = String.format("/job/job-payment?token=%s", token);
		String html = this.httpGet(path);
		this.resolveJavascriptToken(html);

		return "/job/setting";
	}
}
