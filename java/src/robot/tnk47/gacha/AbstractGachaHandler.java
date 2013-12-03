package robot.tnk47.gacha;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import robot.AbstractEventHandler;
import robot.Robot;

public abstract class AbstractGachaHandler extends AbstractEventHandler {

    private static final Pattern GACHA_RESULT_PATTHERN = Pattern.compile("var gachaResultObj = (\\{.*\\})");

    public AbstractGachaHandler(final Robot robot) {
        super(robot);
    }

    protected void resolveGachaResult(final String html) {
        this.resolveInputToken(html);
        if (this.log.isInfoEnabled()) {
            final Matcher pageParamsMatcher = AbstractGachaHandler.GACHA_RESULT_PATTHERN.matcher(html);
            if (pageParamsMatcher.find()) {
                final String group = pageParamsMatcher.group(1);
                final JSONObject gachaResultObj = JSONObject.fromObject(group);
                final JSONArray cardList = gachaResultObj.getJSONArray("cardList");
                for (int i = 0; i < cardList.size(); i++) {
                    final JSONObject card = cardList.getJSONObject(i);
                    final String name = card.getString("name");
                    this.log.info(String.format("获得报酬： %s", name));
                }
            } else {
                this.log.info("没有获得报酬");
            }
        }
    }

}
