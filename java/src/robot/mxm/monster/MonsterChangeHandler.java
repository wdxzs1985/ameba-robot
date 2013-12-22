package robot.mxm.monster;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public class MonsterChangeHandler extends MxmEventHandler {

    private static final Pattern CHANGE_LEADER_PATTERN = Pattern.compile("/user/leader/(\\d+)/change");

    private static final Pattern LEADER_TYPE_PATTERN = Pattern.compile("<span class=\"iconAttr type(\\d)\">(.)</span>");

    public MonsterChangeHandler(final MxmRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final String leaderId = this.findLeader();
        if (leaderId != null) {
            this.changeLeader(leaderId);
        }
        return "/mypage";
    }

    private String findLeader() {
        String leaderId = null;
        final String html = this.httpGet("/user/monsters");
        final Matcher matcher = MonsterChangeHandler.CHANGE_LEADER_PATTERN.matcher(html);
        if (matcher.find()) {
            leaderId = matcher.group(1);
        }
        return leaderId;
    }

    private void changeLeader(final String leaderId) {
        final String path = String.format("/user/leader/%s/change", leaderId);
        final String html = this.httpGet(path);
        final Matcher matcher = MonsterChangeHandler.LEADER_TYPE_PATTERN.matcher(html);
        if (matcher.find()) {
            final String elementId = matcher.group(1);
            final Map<String, Object> session = this.robot.getSession();
            session.put("leaderType", elementId);
        }
    }
}
