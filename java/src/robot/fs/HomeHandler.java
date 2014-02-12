package robot.fs;

import robot.AbstractEventHandler;

public class HomeHandler extends AbstractEventHandler<FSRobot> {

    public HomeHandler(final FSRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final String html = this.httpGet("/");
        final String title = this.getHtmlTitle(html);
        if ("天空のクリスタリア".equals(title)) {
            return "/mypage";
        } else {
            return "/login";
        }
    }
}
