package robot.tnk47.card;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class CardSellHandler extends Tnk47EventHandler {

    public CardSellHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        this.httpGet("/card/card-sell-select");
        this.sleep();

        final List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("regionId", ""));
        nvps.add(new BasicNameValuePair("prefectureId", ""));
        nvps.add(new BasicNameValuePair("elementId", ""));
        nvps.add(new BasicNameValuePair("rarity", "2"));
        nvps.add(new BasicNameValuePair("sortCriteria", "cost_ASC"));
        nvps.add(new BasicNameValuePair("materialFlg", "false"));
        nvps.add(new BasicNameValuePair("ownTransition", "true"));
        final String html = this.httpPost("/card/card-sell-select", nvps);
        this.resolveInputToken(html);

        final JSONObject jsonPageParams = this.resolvePageParams(html);
        if (jsonPageParams != null) {
            final JSONObject firstPageData = jsonPageParams.optJSONObject("firstPageData");
            final int totalPage = firstPageData.optInt("totalPage");
            final List<JSONObject> cardList = new ArrayList<JSONObject>();

            for (int i = 1; i <= totalPage; i++) {
                final JSONObject data = this.getUserCardData(i);
                this.sleep();
                this.selectCard(data.optJSONArray("userCards"), cardList);
            }

            if (CollectionUtils.isNotEmpty(cardList)) {
                final String sellIds = this.buildCardIds(cardList);
                // sell confirm
                this.sleep();
                this.sellConfirm(sellIds);
                // sell
                this.sleep();
                this.sell(sellIds);
            }
        }
        return "/mypage";
    }

    private JSONObject getUserCardData(final int page) {
        final List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("regionId", ""));
        nvps.add(new BasicNameValuePair("prefectureId", ""));
        nvps.add(new BasicNameValuePair("elementId", ""));
        nvps.add(new BasicNameValuePair("rarity", "2"));
        nvps.add(new BasicNameValuePair("sortCriteria", "cost_ASC"));
        nvps.add(new BasicNameValuePair("materialFlg", "false"));
        nvps.add(new BasicNameValuePair("page", String.valueOf(page)));
        final JSONObject response = this.httpPostJSON("/card/ajax/get-card-sell-list",
                                                      nvps);
        return response.optJSONObject("data");
    }

    private void selectCard(final JSONArray userCards,
                            final List<JSONObject> cardList) {
        for (int i = 0; i < userCards.size(); i++) {
            final JSONObject cardDto = userCards.optJSONObject(i);
            final int regionId = cardDto.optInt("regionId");
            if (regionId != 9) {
                cardList.add(cardDto);
            }
        }
    }

    private String buildCardIds(final List<JSONObject> cardList) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cardList.size(); i++) {
            final JSONObject cardDto = cardList.get(i);
            if (i != 0) {
                builder.append(",");
            }
            builder.append(cardDto.optString("userCardId"));
        }
        return builder.toString();
    }

    private void sellConfirm(final String sellIds) {
        final List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("checkedUserCardIds", sellIds));
        final JSONObject jsonResponse = this.httpPostJSON("/card/ajax/get-card-sell-confirm",
                                                          nvps);
        this.resolveJsonToken(jsonResponse);
    }

    private void sell(final String sellIds) {
        final Map<String, Object> session = this.robot.getSession();
        final String token = (String) session.get("token");
        final List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("userCardIds", sellIds));
        nvps.add(new BasicNameValuePair("token", token));
        final JSONObject jsonResponse = this.httpPostJSON("/card/ajax/put-card-sell",
                                                          nvps);
        this.resolveJsonToken(jsonResponse);
    }
}
