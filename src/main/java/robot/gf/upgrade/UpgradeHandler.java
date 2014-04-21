package robot.gf.upgrade;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class UpgradeHandler extends GFEventHandler {

    private static final Pattern MAX_PAGE_PATTERN = Pattern.compile("var maxPage = (\\d+);");

    public UpgradeHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final int maxPage = this.getMaxPage();
        final JSONObject baseCard = this.searchBase(maxPage);
        if (baseCard != null) {
            final String baseUserCardId = baseCard.optString("userCardId");
            session.put("baseUserCardId", baseUserCardId);
            return "/upgrade/confirm";
        }
        return "/mypage";
    }

    private int getMaxPage() {
        final String html = this.httpGet("/upgrade");
        final Matcher matcher = UpgradeHandler.MAX_PAGE_PATTERN.matcher(html);
        if (matcher.find()) {
            final String maxPage = matcher.group(1);
            return Integer.valueOf(maxPage);
        }
        return 0;
    }

    private JSONObject searchBase(final int maxPage) {
        int page = 1;
        while (page <= maxPage) {
            final String path = "/upgrade/ajax/upgrade-card-search";
            final List<BasicNameValuePair> nvps = this.createNameValuePairs();
            nvps.add(new BasicNameValuePair("cond", "base"));
            nvps.add(new BasicNameValuePair("sphere", "ALL"));
            nvps.add(new BasicNameValuePair("sortType", "rarity_power"));
            nvps.add(new BasicNameValuePair("sort", "desc"));
            nvps.add(new BasicNameValuePair("page", String.valueOf(page)));
            nvps.add(new BasicNameValuePair("rarity", "0"));
            nvps.add(new BasicNameValuePair("skill", "0"));
            nvps.add(new BasicNameValuePair("status", "0"));

            final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
            final JSONObject data = jsonResponse.optJSONObject("data");
            final JSONArray searchList = data.optJSONArray("searchList");
            if (searchList != null) {
                for (int i = 0; i < searchList.size(); i++) {
                    final JSONObject card = searchList.optJSONObject(i);
                    final int level = card.optInt("level");
                    final int maxLevel = card.optInt("maxLevel");
                    if (level < maxLevel) {
                        if (this.log.isInfoEnabled()) {
                            final String cardName = card.optString("cardName");
                            this.log.info(String.format("升级 %s", cardName));
                        }
                        return card;
                    }
                }
            }
            page++;
        }
        return null;
    }
}
