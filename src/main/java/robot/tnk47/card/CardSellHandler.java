package robot.tnk47.card;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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

        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("regionId", ""));
        nvps.add(new BasicNameValuePair("prefectureId", ""));
        nvps.add(new BasicNameValuePair("elementId", ""));
        nvps.add(new BasicNameValuePair("rarity", "2"));
        nvps.add(new BasicNameValuePair("sortCriteria", "cost_ASC"));
        nvps.add(new BasicNameValuePair("materialFlg", "false"));
        nvps.add(new BasicNameValuePair("ownTransition", "true"));
        String html = this.httpPost("/card/card-sell-select", nvps);
        this.resolveInputToken(html);

        final JSONObject jsonPageParams = this.resolvePageParams(html);
        if (jsonPageParams != null) {
            final JSONObject firstPageData = jsonPageParams.optJSONObject("firstPageData");
            int totalPage = firstPageData.optInt("totalPage");
            List<JSONObject> cardList = new ArrayList<JSONObject>();

            for (int i = 1; i <= totalPage; i++) {
                JSONObject data = this.getUserCardData(i);
                this.sleep();
                this.selectCard(data.optJSONArray("userCards"), cardList);
            }

            String sellIds = this.buildCardIds(cardList);
            // sell confirm
            this.sleep();
            this.sellConfirm(sellIds);
            // sell
            this.sleep();
            this.sell(sellIds);
        }
        return "/mypage";
    }

    private JSONObject getUserCardData(int page) {
        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("regionId", ""));
        nvps.add(new BasicNameValuePair("prefectureId", ""));
        nvps.add(new BasicNameValuePair("elementId", ""));
        nvps.add(new BasicNameValuePair("rarity", "2"));
        nvps.add(new BasicNameValuePair("sortCriteria", "cost_ASC"));
        nvps.add(new BasicNameValuePair("materialFlg", "false"));
        nvps.add(new BasicNameValuePair("page", String.valueOf(page)));
        JSONObject response = this.httpPostJSON("/card/ajax/get-card-sell-list",
                                                nvps);
        return response.optJSONObject("data");
    }

    private void selectCard(JSONArray userCards, List<JSONObject> cardList) {
        for (int i = 0; i < userCards.size(); i++) {
            final JSONObject cardDto = userCards.optJSONObject(i);
            int regionId = cardDto.optInt("regionId");
            if (regionId != 9) {
                cardList.add(cardDto);
            }
        }
    }

    private String buildCardIds(final List<JSONObject> cardList) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cardList.size(); i++) {
            final JSONObject cardDto = cardList.get(i);
            if (i != 0) {
                builder.append(",");
            }
            builder.append(cardDto.optString("userCardId"));
        }
        return builder.toString();
    }

    private void sellConfirm(String sellIds) {
        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("checkedUserCardIds", sellIds));
        JSONObject jsonResponse = this.httpPostJSON("/card/ajax/get-card-sell-confirm",
                                                    nvps);
        this.resolveJsonToken(jsonResponse);
    }

    private void sell(String sellIds) {
        final Map<String, Object> session = this.robot.getSession();
        String token = (String) session.get("token");
        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("userCardIds", sellIds));
        nvps.add(new BasicNameValuePair("token", token));
        JSONObject jsonResponse = this.httpPostJSON("/card/ajax/put-card-sell",
                                                    nvps);
        this.resolveJsonToken(jsonResponse);
    }
}
