package robot.gf.job;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class JobStartHandler extends GFEventHandler {

	private static final Pattern JOB_NAME_PATTERN = Pattern
			.compile("<h1 id=\"jobName\">(.*?)</h1>");
	private static final Pattern PAYMENT_PATTERN = Pattern
			.compile("<span class=\"fcwOrange\">時給：</span> (\\d+ガル)");
	private static final Pattern REST_PATTERN = Pattern
			.compile("<span id=\"restNumber\">(\\d{2}:\\d{2}:\\d{2})</span>");

	public JobStartHandler(final GFRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		Map<String, Object> session = this.robot.getSession();
		String userCardId = (String) session.get("userCardId");

		String path = "/job/ajax/job-select-card";
		List<BasicNameValuePair> nvps = this.createNameValuePairs();
		nvps.add(new BasicNameValuePair("userCardId", userCardId));
		JSONObject jsonResponse = this.httpPostJSON(path, nvps);

		if (jsonResponse.containsKey("token")) {
			String token = jsonResponse.getString("token");
			path = String.format("/job?token=%s&userCardId=%s", token,
					userCardId);
			final String html = this.httpGet(path);
			if (this.log.isInfoEnabled()) {
				Matcher matcher = null;
				if ((matcher = JOB_NAME_PATTERN.matcher(html)).find()) {
					String jobName = matcher.group(1);
					this.log.info(String.format("バイト先：%s", jobName));
				}
				if ((matcher = PAYMENT_PATTERN.matcher(html)).find()) {
					String payment = matcher.group(1);
					this.log.info(String.format("時給：%s", payment));
				}
				if ((matcher = REST_PATTERN.matcher(html)).find()) {
					String rest = matcher.group(1);
					this.log.info(String.format("残り時間：%s", rest));
				}
			}
		}
		return "/mypage";
	}
}
