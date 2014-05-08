package robot.tnk47;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

public class GiftCardHandler extends Tnk47EventHandler {

    public GiftCardHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final String html = this.httpGet("/gift?type=1");
        this.resolveInputToken(html);

        final JSONObject jsonPageParams = this.resolvePageParams(html);
        if (jsonPageParams != null) {
            final JSONObject firstPageData = jsonPageParams.optJSONObject("firstPageData");
            final int page = firstPageData.optInt("page");
            if (page > 0) {
                final JSONArray giftDtos = firstPageData.optJSONArray("giftDtos");
                if (this.hasWarning(giftDtos)) {
                    return "/mypage";
                } else {
                    final String giftIds = this.buildGiftIds(giftDtos);
                    this.sendReceiveAllGift(giftIds);
                    return "/gift/card";
                }
            } else {
                if (this.log.isInfoEnabled()) {
                    this.log.info("没有礼物");
                }
            }
        }
        return "/mypage";
    }

    private boolean hasWarning(JSONArray giftDtos) {
        for (int i = 0; i < giftDtos.size(); i++) {
            final JSONObject giftDto = giftDtos.optJSONObject(i);
            if (giftDto.containsKey("warningType")) {
                return true;
            }
        }
        return false;
    }

    private String buildGiftIds(final JSONArray giftDtos) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < giftDtos.size(); i++) {
            if (i != 0) {
                builder.append(",");
            }
            final JSONObject giftDto = giftDtos.optJSONObject(i);
            builder.append(giftDto.optString("giftId"));
        }
        return builder.toString();
    }

    private void sendReceiveAllGift(final String giftIds) {
        final Map<String, Object> session = this.robot.getSession();
        final String token = (String) session.get("token");

        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("token", token));
        nvps.add(new BasicNameValuePair("giftIds", giftIds));

        final JSONObject jsonResponse = this.httpPostJSON("/gift/ajax/put-recive-all-gift",
                                                          nvps);
        final JSONArray data = jsonResponse.optJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            final JSONObject reward = data.optJSONObject(i);
            final String rewardName = reward.optString("rewardName");
            if (this.log.isInfoEnabled()) {
                this.log.info(String.format("领取礼物： %s", rewardName));
            }
        }
    }
}
