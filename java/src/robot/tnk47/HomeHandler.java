package robot.tnk47;

import org.apache.commons.lang.StringUtils;

import robot.AbstractEventHandler;

public class HomeHandler extends AbstractEventHandler<Tnk47Robot> {

    public HomeHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final String html = this.httpGet("/");
        final String title = this.getHtmlTitle(html);
        if (StringUtils.equals("天下統一クロニクル", title)) {
            return "/mypage";
        } else {
            return "/login";
        }
    }
}
