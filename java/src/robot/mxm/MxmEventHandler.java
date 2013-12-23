/**
 * 
 */
package robot.mxm;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import robot.AbstractEventHandler;

public abstract class MxmEventHandler extends AbstractEventHandler<MxmRobot> {

    private static final Pattern MXM_TOKEN_PATTERN = Pattern.compile("mxm.token = \"([a-zA-Z0-9]{6})\";");
    private static final Pattern RAID_TOP_PATTERN = Pattern.compile("/raid/(\\d+)/(\\d+)/top");
    private static final Pattern RAID_WIN_RESULT_PATTERN = Pattern.compile("/raid/(\\d+)/(\\d+)/win/result");
    private static final Pattern RAID_LOSE_RESULT_PATTERN = Pattern.compile("/raid/(\\d+)/(\\d+)/lose/result");
    private static final Pattern BP_PATTERN = Pattern.compile("<ul class=\"battlePointAreaS bp(\\d)\">(<li></li>){3}</ul>");

    public MxmEventHandler(final MxmRobot robot) {
        super(robot);
    }

    protected void resolveMxmToken(final String html) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher tokenMatcher = MxmEventHandler.MXM_TOKEN_PATTERN.matcher(html);
        if (tokenMatcher.find()) {
            final String newToken = tokenMatcher.group(1);
            session.put("token", newToken);
        }
    }

    protected String resolveNextUrl(final String url) {
        if (this.isStageClear(url)) {
            return "/quest/stageClear";
        } else if (this.isGetRing(url)) {
            return "/getRing";
        } else if (this.isRaidTop(url)) {
            return "/raid/top";
        } else if (this.isRaidWinResult(url)) {
            return "/raid/win/result";
        } else if (this.isRaidLoseResult(url)) {
            return "/raid/lose/result";
        } else if (this.isTouchResult(url)) {
            return "/quest/result";
        } else {
            this.log.debug(url);
        }
        return "/mypage";
    }

    protected void resolveJsonToken(final JSONObject jsonResponse) {
        final Map<String, Object> session = this.robot.getSession();
        final String newToken = jsonResponse.getString("token");
        session.put("token", newToken);
    }

    protected boolean isStageClear(final String url) {
        return StringUtils.equals(url, "/stage/stage_clear_animation");
    }

    protected boolean isTouchResult(final String url) {
        return StringUtils.equals(url, "/touch/after_animation/result");
    }

    protected boolean isGetRing(final String url) {
        return StringUtils.equals(url, "/ring/ring_get_animation");
    }

    protected boolean isRaidTop(final String url) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher matcher = MxmEventHandler.RAID_TOP_PATTERN.matcher(url);
        if (matcher.find()) {
            final String raidId = matcher.group(1);
            final String raidPirtyId = matcher.group(2);
            session.put("raidId", raidId);
            session.put("raidPirtyId", raidPirtyId);
            return true;
        }
        return false;
    }

    protected boolean isRaidWinResult(final String url) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher matcher = MxmEventHandler.RAID_WIN_RESULT_PATTERN.matcher(url);
        if (matcher.find()) {
            final String raidId = matcher.group(1);
            final String raidPirtyId = matcher.group(2);
            session.put("raidId", raidId);
            session.put("raidPirtyId", raidPirtyId);
            return true;
        }
        return false;
    }

    protected boolean isRaidLoseResult(final String url) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher matcher = MxmEventHandler.RAID_LOSE_RESULT_PATTERN.matcher(url);
        if (matcher.find()) {
            final String raidId = matcher.group(1);
            final String raidPirtyId = matcher.group(2);
            session.put("raidId", raidId);
            session.put("raidPirtyId", raidPirtyId);
            return true;
        }
        return false;
    }

    protected int getBpCount(final String html) {
        final Matcher matcher = MxmEventHandler.BP_PATTERN.matcher(html);
        if (matcher.find()) {
            final String bpCount = matcher.group(1);
            return Integer.valueOf(bpCount);
        }
        return 0;
    }
}
