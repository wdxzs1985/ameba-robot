package robot;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.message.BasicNameValuePair;

public class LoginHandler extends AbstractEventHandler<Robot> {

	private final Log log = LogFactory.getLog(LoginHandler.class);

	public LoginHandler(final Robot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final String url = "https://login.user.ameba.jp/web/login";
		final String username = this.robot.getUsername();
		final String password = this.robot.getPassword();

		final List<BasicNameValuePair> nvps = new LinkedList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("username", username));
		nvps.add(new BasicNameValuePair("password", password));
		final String html = this.robot.getHttpClient().post(url, nvps);

		if (StringUtils.isNotBlank(html)) {
			throw new RuntimeException("登录失败");
		}

		if (this.log.isInfoEnabled()) {
			this.log.info("登录成功");
		}
		return "/mypage";
	}
}
