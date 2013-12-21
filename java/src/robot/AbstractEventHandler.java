/**
 * 
 */
package robot;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.message.BasicNameValuePair;

public abstract class AbstractEventHandler<T extends AbstractRobot> implements
        EventHandler {

    protected final Log log;
    protected final T robot;
    private final int requestDelay;

    public AbstractEventHandler(final T robot) {
        this.robot = robot;
        this.log = LogFactory.getLog(this.getClass());
        this.requestDelay = robot.getRequestDelay();
    }

    @Override
    public final void handle() {
        this.before();
        try {
            this.robot.dispatch(this.handleIt());
        } catch (final Exception e) {
            final String message = e.getMessage();
            this.log.error("发生异常: " + message, e);
        } finally {
            this.after();
        }
    }

    protected void before() {
    }

    protected abstract String handleIt();

    protected void after() {
        this.sleep();
    }

    protected String httpGet(final String path) {
        final String url = this.robot.buildPath(path);
        final String html = this.robot.getHttpClient().getForHtml(url);
        this.robot.getHttpClient().setReferer(url);
        return html.replaceAll("\\r\\n[\\t\\s]*|\\r[\\t\\s]*|\\n[\\t\\s]*", "");
    }

    protected String httpPost(final String path,
                              final List<BasicNameValuePair> nvps) {
        final String url = this.robot.buildPath(path);
        final String html = this.robot.getHttpClient().postForHtml(url, nvps);
        this.robot.getHttpClient().setReferer(url);
        return html.replaceAll("\\r\\n[\\t\\s]*|\\r[\\t\\s]*|\\n[\\t\\s]*", "");
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

    protected void sleep() {
        final int sleepTime = this.requestDelay + RandomUtils.nextInt(this.requestDelay);
        try {
            Thread.sleep(sleepTime * 1000);
        } catch (final InterruptedException e) {
        }
    }
}
