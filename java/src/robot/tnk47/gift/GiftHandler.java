package robot.tnk47.gift;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class GiftHandler extends AbstractEventHandler {

    public GiftHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String type = (String) session.get("type");
        final String html = this.httpGet(String.format("/gift?type=%s", type));
        this.resolveInputToken(html);
        final JSONObject jsonPageParams = this.resolvePageParams(html);
        if (jsonPageParams != null) {
            final JSONObject firstPageData = jsonPageParams.getJSONObject("firstPageData");
            final int page = firstPageData.getInt("page");
            if (page > 0) {
                final String giftIds = this.buildGiftIds(firstPageData);
                this.sendReceiveAllGift(giftIds);
                return "/gift";
            } else {
                if (this.log.isInfoEnabled()) {
                    this.log.info("没有礼物");
                }
            }
        }
        return "/mypage";
    }

    private String buildGiftIds(final JSONObject firstPageData) {
        final JSONArray giftDtos = firstPageData.getJSONArray("giftDtos");
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < giftDtos.size(); i++) {
            if (i != 0) {
                builder.append(",");
            }
            final JSONObject giftDto = giftDtos.getJSONObject(i);
            builder.append(giftDto.getString("giftId"));
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
        final JSONArray data = jsonResponse.getJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            final JSONObject reward = data.getJSONObject(i);
            final String rewardName = reward.getString("rewardName");
            if (this.log.isInfoEnabled()) {
                this.log.info(String.format("领取礼物： %s", rewardName));
            }
        }
    }
}
