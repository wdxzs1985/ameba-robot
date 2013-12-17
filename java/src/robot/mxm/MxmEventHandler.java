/**
 * 
 */
package robot.mxm;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import robot.AbstractEventHandler;

public abstract class MxmEventHandler extends
        AbstractEventHandler<MxmRobot> {

    private static final Pattern PAGE_PARAMS_PATTERN = Pattern.compile(".*\\.pageParams = (\\{.*\\});");
    private static final Pattern INPUT_TOKEN_PATTERN = Pattern.compile("<input id=\"__token\" type=\"hidden\" value=\"([a-zA-Z0-9]{6})\"( data-page-id=\".*\")?>");

    public MxmEventHandler(final MxmRobot robot) {
        super(robot);
    }

    protected void resolveInputToken(final String html) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher tokenMatcher = MxmEventHandler.INPUT_TOKEN_PATTERN.matcher(html);
        if (tokenMatcher.find()) {
            final String newToken = tokenMatcher.group(1);
            session.put("token", newToken);
        }
    }

    protected JSONObject resolvePageParams(final String html) {
        final Matcher pageParamsMatcher = MxmEventHandler.PAGE_PARAMS_PATTERN.matcher(html);
        if (pageParamsMatcher.find()) {
            final String pageParams = pageParamsMatcher.group(1);
            final JSONObject jsonPageParams = JSONObject.fromObject(pageParams);
            return jsonPageParams;
        }
        return null;
    }

    protected void resolveJsonToken(final JSONObject jsonResponse) {
        final Map<String, Object> session = this.robot.getSession();
        final String newToken = jsonResponse.getString("token");
        session.put("token", newToken);
    }
}
