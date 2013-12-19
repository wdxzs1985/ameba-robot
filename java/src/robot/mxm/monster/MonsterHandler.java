package robot.mxm.monster;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public class MonsterHandler extends MxmEventHandler {

	private static final Pattern CHANGE_LEADER_PATTERN = Pattern
			.compile("/user/leader/(\\d+)/change");

	private static final Pattern LEADER_TYPE_PATTERN = Pattern
			.compile("<span class=\"iconAttr type(\\d)\">(.)</span>");

	public MonsterHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		String leaderId = this.findLeader();
		if (leaderId != null) {
			this.changeLeader(leaderId);
		}
		return "/quest";
	}

	private String findLeader() {
		String leaderId = null;
		final String html = this.httpGet("/user/monsters");
		Matcher matcher = CHANGE_LEADER_PATTERN.matcher(html);
		if (matcher.find()) {
			leaderId = matcher.group(1);
		}
		return leaderId;
	}

	private void changeLeader(String leaderId) {
		String path = String.format("/user/leader/%s/change", leaderId);
		final String html = this.httpGet(path);
		Matcher matcher = LEADER_TYPE_PATTERN.matcher(html);
		if (matcher.find()) {
			String leaderType = matcher.group(1);
			final Map<String, Object> session = this.robot.getSession();
			session.put("leaderType", leaderType);
			if (this.log.isInfoEnabled()) {
				if (StringUtils.equals("1", leaderType)) {
					this.log.info("属性：火");
				} else if (StringUtils.equals("2", leaderType)) {
					this.log.info("属性：水");
				} else if (StringUtils.equals("3", leaderType)) {
					this.log.info("属性：木");
				} else if (StringUtils.equals("4", leaderType)) {
					this.log.info("属性：雷");
				} else if (StringUtils.equals("5", leaderType)) {
					this.log.info("属性：風");
				} else if (StringUtils.equals("6", leaderType)) {
					this.log.info("属性：土");
				}
			}
		}
	}
}
