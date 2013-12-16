package robot;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicNameValuePair;

import common.CommonHttpClient;

public class LoginHandler extends AbstractEventHandler<Robot> {

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
		CommonHttpClient httpClient = this.robot.getHttpClient();
		final HttpResponse webLoginResponse = httpClient.post(url, nvps);

		if (webLoginResponse.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
			return "/mypage";
		}

		throw new RuntimeException("登录失败");
	}

}
