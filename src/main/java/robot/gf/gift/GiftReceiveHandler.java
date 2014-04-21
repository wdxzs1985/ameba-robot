package robot.gf.gift;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.message.BasicNameValuePair;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class GiftReceiveHandler extends GFEventHandler {

    private static final Pattern FULL_PATTERN = Pattern.compile("<dt class=\"fcRed sText mgT5\">(.*?の所持上限を超えています)</dt>");

    public GiftReceiveHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String token = (String) session.get("token");

        final String path = "/giftbox/giftbox-system-all-recive";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("token", token));
        nvps.add(new BasicNameValuePair("page", "1"));
        nvps.add(new BasicNameValuePair("selectedGift", "0"));

        final String html = this.httpPost(path, nvps);
        this.resolveJavascriptToken(html);

        final Matcher matcher = GiftReceiveHandler.FULL_PATTERN.matcher(html);
        if (matcher.find()) {
            if (this.log.isInfoEnabled()) {
                this.log.info(matcher.group(1));
            }
            return "/mypage";
        }

        return "/gift";
    }

}
