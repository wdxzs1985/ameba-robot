package robot.gf.upgrade;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class UpgradeConfirmHandler extends GFEventHandler {

    private static final Pattern PARAMS_PATTERN = Pattern.compile("\"/upgrade/upgrade-animation\\?baseUserCardId=.*?&materialUserCardId=(.*?)&token=(.*?)\"");

    public UpgradeConfirmHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String baseUserCardId = (String) session.get("baseUserCardId");
        final String path = String.format("/upgrade/upgrade-confirm?baseUserCardId=%s&auto=true",
                                          baseUserCardId);
        final String html = this.httpGet(path);
        final Matcher matcher = UpgradeConfirmHandler.PARAMS_PATTERN.matcher(html);
        if (matcher.find()) {
            final String materialUserCardId = matcher.group(1);
            final String token = matcher.group(2);
            session.put("materialUserCardId", materialUserCardId);
            session.put("token", token);
            return "/upgrade/animation";
        } else {
            if (this.log.isInfoEnabled()) {
                this.log.info("没有可以用来升级的素材");
            }
        }
        return "/mypage";
    }
}
