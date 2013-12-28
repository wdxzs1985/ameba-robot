package robot.mxm.item;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.message.BasicNameValuePair;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public class ItemPotionHandler extends MxmEventHandler {

    private static final Pattern POTION_PATTERN = Pattern.compile("<div class=\"fsLarge\">元気のポーション</div><div class=\"padTop2\"><span class=\"colorDeepOrange\">所持数：</span>(\\d+)個</div>");
    private static final Pattern RESULT_PATTERN = Pattern.compile("<ul class=\"potConfTxt\\d\"><li class=\"colorDeepOrange\">(.*?)</li><li>(.*?)</li><li><span class=\"mxmfont countTriangle\">→</span></li><li class=\"colorRed\">(.*?)</li></ul>");

    public ItemPotionHandler(final MxmRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final int potion = this.findPotion();
        if (potion > 0) {
            this.sleep();
            this.itemPotionConfirm();
            this.sleep();
            this.itemPotionResult();
        }
        return "/mypage";
    }

    private int findPotion() {
        final String path = "/item/item_box";
        final String html = this.httpGet(path);
        final Matcher matcher = ItemPotionHandler.POTION_PATTERN.matcher(html);
        if (matcher.find()) {
            final int num = Integer.valueOf(matcher.group(1));
            return num;
        }
        return 0;
    }

    private void itemPotionConfirm() {
        final Map<String, Object> session = this.robot.getSession();
        final String potionId = (String) session.get("potionId");
        final String path = "/item/potion/confirm";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("potionId", potionId));
        final String html = this.httpPost(path, nvps);
        this.resolveInputToken(html);
    }

    private void itemPotionResult() {
        final Map<String, Object> session = this.robot.getSession();
        final String potionId = (String) session.get("potionId");
        final String token = (String) session.get("token");
        final String path = "/item/potion/result";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("potionId", potionId));
        nvps.add(new BasicNameValuePair("token", token));
        final String html = this.httpPost(path, nvps);
        final Matcher matcher = ItemPotionHandler.RESULT_PATTERN.matcher(html);
        while (matcher.find()) {
            final String name = matcher.group(1);
            final String before = matcher.group(2);
            final String after = matcher.group(3);
            this.log.info(String.format("%s %s → %s", name, before, after));
        }
    }
}
