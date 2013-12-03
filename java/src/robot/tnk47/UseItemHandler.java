package robot.tnk47;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class UseItemHandler extends AbstractEventHandler {

	public UseItemHandler(final Robot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		final String itemId = (String) session.get("itemId");
		final String token = (String) session.get("token");
		final String name = (String) session.get("name");

		if (this.log.isInfoEnabled()) {
			this.log.info("药不能停！吃了一个" + name);
		}
		final String input = this.robot.buildPath("/item/ajax/put-item-use");
		final List<BasicNameValuePair> nvps = new LinkedList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("itemId", itemId));
		nvps.add(new BasicNameValuePair("token", token));

		final String html = this.robot.getHttpClient().post(input, nvps);

		final JSONObject jsonResponse = JSONObject.fromObject(html);
		this.resolveJsonToken(jsonResponse);

		final String callback = (String) session.get("callback");
		return (callback);
	}

}
