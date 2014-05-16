package robot.tnk47.gacha;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import robot.tnk47.Tnk47Robot;

public class Gacha2Handler extends AbstractGachaHandler {

    private static final Pattern ACTBTN_PATTHERN = Pattern.compile("<a href=\"(/gacha/gacha-free-animation\\?.*?)\" class=\"actBtn jscTouchActive \" ?>");

    public Gacha2Handler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final String html = this.httpGet("/gacha?gachaTabId=2");
        this.resolveInputToken(html);

        this.log.debug(html);

        final Matcher matcher = ACTBTN_PATTHERN.matcher(html);
        if (matcher.find()) {
            String url = matcher.group(1);
            url = StringEscapeUtils.unescapeHtml(url);
            this.openGachaAnimation(url);
        }
        return "/mypage";
    }

    private void openGachaAnimation(final String path) {
        final String html = this.httpGet(path);
        this.resolveGachaResult(html);
    }
}
