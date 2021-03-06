package robot.tnk47.gacha;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public abstract class AbstractGachaHandler extends Tnk47EventHandler {

    private static final Pattern GACHA_RESULT_PATTHERN = Pattern.compile("gachaInfo = (\\{.*\\})");
    private final static Pattern RESET_PATTERN = Pattern.compile("/gacha/box-gacha-reset\\?gachaId=(\\d+)&token=([a-zA-Z0-9]{6})");

    public AbstractGachaHandler(final Tnk47Robot robot) {
        super(robot);
    }

    protected void resolveGachaResult(final String html) {
        this.resolveInputToken(html);
        if (this.log.isInfoEnabled()) {
            final Matcher pageParamsMatcher = AbstractGachaHandler.GACHA_RESULT_PATTHERN.matcher(html);
            if (pageParamsMatcher.find()) {
                final String group = pageParamsMatcher.group(1);
                final JSONObject gachaResultObj = JSONObject.fromObject(group);
                final JSONArray cardList = gachaResultObj.optJSONArray("cardList");
                for (int i = 0; i < cardList.size(); i++) {
                    final JSONObject card = cardList.optJSONObject(i);
                    final String name = card.optString("name");
                    this.log.info(String.format("获得报酬： %s", name));
                }
            } else {
                this.log.info("没有获得报酬");
            }
        }
    }

    protected void boxGachareset(final String html) {
        final Matcher matcher = AbstractGachaHandler.RESET_PATTERN.matcher(html);
        if (matcher.find()) {
            final String gachaId = matcher.group(1);
            final String token = matcher.group(2);
            final String path = String.format("/gacha/box-gacha-reset?gachaId=%s&token=%s",
                                              gachaId,
                                              token);
            this.httpGet(path);
        }

    }
}
