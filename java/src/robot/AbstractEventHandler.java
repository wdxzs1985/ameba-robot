/**
 * 
 */
package robot;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.message.BasicNameValuePair;

public abstract class AbstractEventHandler<T extends AbstractRobot> implements
		EventHandler {

	protected final Log log;
	protected final T robot;

	public AbstractEventHandler(final T robot) {
		this.robot = robot;
		this.log = LogFactory.getLog(this.getClass());
	}

	@Override
	public final void handle() {
		this.before();
		this.robot.dispatch(this.handleIt());
		this.after();
	}

	protected void before() {
	}

	protected abstract String handleIt();

	protected void after() {
	}

	protected String httpGet(final String path) {
		final String url = this.robot.buildPath(path);
		final String html = this.robot.getHttpClient().getForHtml(url);
		this.robot.getHttpClient().setReferer(url);
		return html.replaceAll("\\r\\n|\\r|\\n", "");
	}

	protected String httpPost(final String path,
			final List<BasicNameValuePair> nvps) {
		final String url = this.robot.buildPath(path);
		final String html = this.robot.getHttpClient().postForHtml(url, nvps);
		this.robot.getHttpClient().setReferer(url);
		return html.replaceAll("\\r\\n|\\r|\\n", "");
	}

	protected JSONObject httpGetJSON(final String path) {
		final String url = this.robot.buildPath(path);
		final String html = this.robot.getHttpClient().getForHtml(url);
		return JSONObject.fromObject(html);
	}

	protected JSONObject httpPostJSON(final String path,
			final List<BasicNameValuePair> nvps) {
		final String url = this.robot.buildPath(path);
		final String html = this.robot.getHttpClient().postForHtml(url, nvps);
		return JSONObject.fromObject(html);
	}

	protected List<BasicNameValuePair> createNameValuePairs() {
		return new LinkedList<BasicNameValuePair>();
	}

	protected boolean is(final String funcName) {
		final Map<String, Object> session = this.robot.getSession();
		final boolean enable = (Boolean) session.get(funcName);
		return enable;
	}
}
