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

	private static final Pattern MXM_TOKEN_PATTERN = Pattern
			.compile("mxm.token = \"([a-zA-Z0-9]{6})\";");
	private static final Pattern RAID_TOP_PATTERN = Pattern
			.compile("/raid/(\\d+)/(\\d+)/top");
	private static final Pattern RAID_WIN_RESULT_PATTERN = Pattern
			.compile("/raid/(\\d+)/(\\d+)/win/result");

	public MxmEventHandler(final MxmRobot robot) {
		super(robot);
	}

	protected void resolveMxmToken(final String html) {
		final Map<String, Object> session = this.robot.getSession();
		final Matcher tokenMatcher = MxmEventHandler.MXM_TOKEN_PATTERN
				.matcher(html);
		if (tokenMatcher.find()) {
			final String newToken = tokenMatcher.group(1);
			session.put("token", newToken);
		}
	}

	protected String resolveNextUrl(final String url) {
		this.log.debug(url);
		if (this.isStageClear(url)) {
			return "/quest/stageClear";
		} else if (this.isGetRing(url)) {
			return "/quest/getRing";
		} else if (this.isRaidTop(url)) {
			return "/raid/top";
		} else if (this.isRaidWinResult(url)) {
			return "/raid/win/result";
		}
		return "/mypage";
	}

	protected void resolveJsonToken(final JSONObject jsonResponse) {
		final Map<String, Object> session = this.robot.getSession();
		final String newToken = jsonResponse.getString("token");
		session.put("token", newToken);
	}

	protected boolean isStageClear(String url) {
		return StringUtils.equals(url, "/stage/stage_clear_animation");
	}

	protected boolean isGetRing(String url) {
		return StringUtils.equals(url, "/ring/ring_get_animation");
	}

	protected boolean isRaidTop(String url) {
		final Map<String, Object> session = this.robot.getSession();
		Matcher matcher = RAID_TOP_PATTERN.matcher(url);
		if (matcher.find()) {
			String eventId = matcher.group(1);
			String raidId = matcher.group(2);
			session.put("eventId", eventId);
			session.put("raidId", raidId);
			return true;
		}
		return false;
	}

	protected boolean isRaidWinResult(String url) {
		final Map<String, Object> session = this.robot.getSession();
		Matcher matcher = RAID_WIN_RESULT_PATTERN.matcher(url);
		if (matcher.find()) {
			String eventId = matcher.group(1);
			String raidId = matcher.group(2);
			session.put("eventId", eventId);
			session.put("raidId", raidId);
			return true;
		}
		return false;
	}
}
