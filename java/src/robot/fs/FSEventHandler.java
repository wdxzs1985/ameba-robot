/**
 * 
 */
package robot.fs;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import robot.AbstractEventHandler;

public abstract class FSEventHandler extends AbstractEventHandler<FSRobot> {

    private static final Pattern INPUT_TOKEN_PATTERN = Pattern.compile("<input id=\"__token\" type=\"hidden\" value=\"([a-zA-Z0-9]{6})\" />");
    private static final Pattern INPUT_USER_ID_PATTERN = Pattern.compile("<input id=\"__userId\" type=\"hidden\" value=\"([a-zA-Z0-9]{6})\" />");

    public FSEventHandler(final FSRobot robot) {
        super(robot);
    }

    protected void resolveInputToken(final String html) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher tokenMatcher = FSEventHandler.INPUT_TOKEN_PATTERN.matcher(html);
        if (tokenMatcher.find()) {
            final String newToken = tokenMatcher.group(1);
            session.put("token", newToken);
        }
    }

    protected void resolveInputUserId(final String html) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher tokenMatcher = FSEventHandler.INPUT_USER_ID_PATTERN.matcher(html);
        if (tokenMatcher.find()) {
            final String userId = tokenMatcher.group(1);
            session.put("userId", userId);
        }
    }

    protected void resolveJsonToken(final JSONObject jsonResponse) {
        final Map<String, Object> session = this.robot.getSession();
        final String newToken = jsonResponse.optString("token");
        session.put("token", newToken);
    }
}
