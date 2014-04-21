package robot.gf.gift;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class GiftHandler extends GFEventHandler {

    private static final Pattern RESULT_PATTERN = Pattern.compile("<input type=\"hidden\" id=\"result\" value=\"(.*?)\" />");

    public GiftHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final String html = this.httpGet("/giftbox");
        this.resolveJavascriptToken(html);

        final Matcher matcher = GiftHandler.RESULT_PATTERN.matcher(html);
        if (matcher.find()) {
            final String result = matcher.group(1);
            if (!StringUtils.equals(result, "[]")) {
                return "/gift/receive";
            } else {
                if (this.log.isInfoEnabled()) {
                    this.log.info("礼物盒里什么都没有");
                }
            }
        }
        return "/mypage";
    }

}
