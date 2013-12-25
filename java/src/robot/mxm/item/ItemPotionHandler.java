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
        this.itemBox();
        this.itemPotionConfirm();
        this.itemPotionResult();
        return "/mypage";
    }

    private void itemBox() {
        final Map<String, Object> session = this.robot.getSession();
        String path = "/item/item_box";
        String html = this.httpGet(path);

        int potion = this.findPotion(html);
        session.put("potion", potion);
    }

    private int findPotion(String html) {
        Matcher matcher = POTION_PATTERN.matcher(html);
        if (matcher.find()) {
            int num = Integer.valueOf(matcher.group(1));
            return num;
        }
        return 0;
    }

    private void itemPotionConfirm() {
        final Map<String, Object> session = this.robot.getSession();
        String potionId = (String) session.get("potionId");
        String path = "/item/potion/confirm";
        List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("potionId", potionId));
        String html = this.httpPost(path, nvps);
        this.resolveInputToken(html);
    }

    private void itemPotionResult() {
        final Map<String, Object> session = this.robot.getSession();
        String potionId = (String) session.get("potionId");
        String token = (String) session.get("token");
        String path = "/item/potion/result";
        List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("potionId", potionId));
        nvps.add(new BasicNameValuePair("token", token));
        String html = this.httpPost(path, nvps);
        Matcher matcher = RESULT_PATTERN.matcher(html);
        while (matcher.find()) {
            String name = matcher.group(1);
            String before = matcher.group(2);
            String after = matcher.group(3);
            this.log.info(String.format("%s %s → %s", name, before, after));
        }
    }
}
