package robot.mxm.monster;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;
import robot.mxm.convert.MonsterConvert;

public class MonsterHandler extends MxmEventHandler {

    private static final Pattern LEADER_TYPE_PATTERN = Pattern.compile("<span class=\"iconAttr type(\\d)\">(.)</span>");

    public MonsterHandler(final MxmRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final String html = this.httpGet("/user/monsters");
        final Matcher matcher = MonsterHandler.LEADER_TYPE_PATTERN.matcher(html);
        if (matcher.find()) {
            final String elementId = matcher.group(1);
            final Map<String, Object> session = this.robot.getSession();
            session.put("leaderType", elementId);
            if (this.log.isInfoEnabled()) {
                final String elementName = MonsterConvert.convertElement(elementId);
                this.log.info(String.format("正在使用的召唤兽是%s系。", elementName));
            }
        }
        return "/mypage";
    }
}
