package robot.gf.job;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class JobPaymentHandler extends GFEventHandler {

	private static final Pattern PAYMENT_PATTERN = Pattern
			.compile("<p class=\"textArea\">(.*?)<br/>(.*?)</p>");

	public JobPaymentHandler(final GFRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		Map<String, Object> session = this.robot.getSession();
		String token = (String) session.get("token");

		String path = String.format("/job/job-payment?token=%s", token);
		String html = this.httpGet(path);
		if (this.log.isInfoEnabled()) {
			Matcher matcher = PAYMENT_PATTERN.matcher(html);
			if (matcher.find()) {
				String payment1 = matcher.group(1);
				String payment2 = matcher.group(2);
				this.log.info(String.format("収入: %s %s", payment1, payment2));
			}
		}
		return "/job/setting";
	}
}
