package robot.mxm.raid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public class RaidHistoryHandler extends MxmEventHandler {

	private static final Pattern RAID_WIN_PATTERN = Pattern
			.compile("/raid/(\\d+)/(\\d+)/win/animation");

	public RaidHistoryHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		final String html = this.httpGet("/raid/join_histories");
		Matcher matcher = RAID_WIN_PATTERN.matcher(html);
		if (matcher.find()) {
			String raidId = matcher.group(1);
			String raidPirtyId = matcher.group(2);
			session.put("raidId", raidId);
			session.put("raidPirtyId", raidPirtyId);
			return "/raid/win/animation";
		}
		return "/mypage";
	}

}
