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

    private static final Pattern JOB_NAME_PATTERN = Pattern.compile("<h1 id=\"jobName\">(.*?)</h1>");
    private static final Pattern PAYMENT_PATTERN = Pattern.compile("<span class=\"fcwOrange\">時給：</span> (\\d+ガル)");
    private static final Pattern REST_PATTERN = Pattern.compile("<span id=\"restNumber\">(\\d{2}:\\d{2}:\\d{2})</span>");

    public JobStartHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String userCardId = (String) session.get("userCardId");

        String path = "/job/ajax/job-select-card";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("userCardId", userCardId));
        final JSONObject jsonResponse = this.httpPostJSON(path, nvps);

        if (jsonResponse.containsKey("token")) {
            final String token = jsonResponse.optString("token");
            path = String.format("/job?token=%s&userCardId=%s",
                                 token,
                                 userCardId);
            final String html = this.httpGet(path);
            if (this.log.isInfoEnabled()) {
                final JSONObject data = jsonResponse.optJSONObject("data");
                final JSONObject accompanyCard = data.optJSONObject("accompanyCard");
                final String name = accompanyCard.optString("name");
                this.log.info(String.format("和%s妹子一起去打工(*´∀｀*)", name));

                Matcher matcher = null;
                if ((matcher = JobStartHandler.JOB_NAME_PATTERN.matcher(html)).find()) {
                    final String jobName = matcher.group(1);
                    this.log.info(String.format("工作：%s", jobName));
                }
                if ((matcher = JobStartHandler.PAYMENT_PATTERN.matcher(html)).find()) {
                    final String payment = matcher.group(1);
                    this.log.info(String.format("工资：%s", payment));
                }
                if ((matcher = JobStartHandler.REST_PATTERN.matcher(html)).find()) {
                    final String rest = matcher.group(1);
                    this.log.info(String.format("时间：%s", rest));
                }
            }
        }
        return "/mypage";
    }
}
