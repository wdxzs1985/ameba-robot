package robot.tnk47.upgrade;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class UpgradeHandler extends Tnk47EventHandler {

    private static final Pattern BASE_CARD_ID_PATTERN = Pattern.compile("<img src=\"http://stat100.ameba.jp/tnk47/ratio10/illustrations/card/thumb/.*?\" data-card-id=\"(.*?)\" data-image=\"/illustrations/card/thumb/.*?\" data-rarity=\".*\" >");

    public UpgradeHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String html = this.httpGet("/upgrade");
        this.resolveInputToken(html);

        Matcher matcher = null;
        if ((matcher = UpgradeHandler.BASE_CARD_ID_PATTERN.matcher(html)).find()) {
            final String baseUserCardId = matcher.group(1);
            session.put("baseUserCardId", baseUserCardId);
            return "/upgrade/auto-upgrade-confirm";
        }

        final JSONObject jsonPageParams = this.resolvePageParams(html);
        if (jsonPageParams != null) {
            final JSONObject firstPageData = jsonPageParams.getJSONObject("firstPageData");
            final JSONArray userCards = firstPageData.getJSONArray("userCards");
            if (userCards.size() > 0) {
                final JSONObject userCard = userCards.getJSONObject(0);
                final String userCardId = userCard.getString("userCardId");
                session.put("userCardId", userCardId);
                if (this.log.isInfoEnabled()) {
                    final String name = userCard.getString("name");
                    this.log.info(String.format("开始强化 %s", name));
                }
                return "/upgrade/select-base";
            }
        }
        return "/mypage";

    }
}
