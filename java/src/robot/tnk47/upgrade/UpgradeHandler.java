package robot.tnk47.upgrade;

import java.util.List;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class UpgradeHandler extends AbstractEventHandler {

    private static final Pattern UPGRADE_PATTERN = Pattern.compile("オススメ強化");

    public UpgradeHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        String html = this.httpGet("/upgrade");
        this.resolveInputToken(html);
        if (!UpgradeHandler.UPGRADE_PATTERN.matcher(html).find()) {
            final JSONObject jsonPageParams = this.resolvePageParams(html);
            if (jsonPageParams != null) {
                final JSONObject firstPageData = jsonPageParams.getJSONObject("firstPageData");
                final JSONArray userCards = firstPageData.getJSONArray("userCards");
                if (userCards.size() > 0) {
                    final JSONObject userCard = userCards.getJSONObject(0);
                    final String userCardId = userCard.getString("userCardId");

                    final List<BasicNameValuePair> nvps = this.createNameValuePairs();
                    nvps.add(new BasicNameValuePair("userCardId", userCardId));
                    html = this.httpPost("/upgrade", nvps);
                    this.resolveInputToken(html);
                }
            } else {
                return "/mypage";
            }
        }

        final JSONObject jsonPageParams = this.resolvePageParams(html);
        if (jsonPageParams != null) {
            final List<BasicNameValuePair> nvps = this.createNameValuePairs();
            nvps.add(new BasicNameValuePair("baseUserCardId", "baseUserCardId"));
            this.httpPostJSON("/upgrade/ajax/get-auto-upgrade-confirm", nvps);
        } else {
            return "/mypage";
        }
        return "/quest";
    }
}
