package robot.tnk47.conquest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47Robot;

public class ConquestBattleCheckHandler extends AbstractConquestBattleHandler {

    private static final Pattern TENP_PATTERN = Pattern.compile("tnk\\.pageParams\\.itemList\\.push\\(\\{itemId: '2722',itemCount: (\\d+),imgPath: '/illustrations/item/ill_tenpyaku.jpg\\?.*?',apRegenValue: (\\d+),defenceRegenValue: (\\d+)\\}\\);");
    private static final Pattern POWER100_PATTERN = Pattern.compile("tnk\\.pageParams\\.itemList\\.push\\(\\{itemId: '20',itemCount: (\\d+),imgPath: '/illustrations/item/ill_20_oneday_power100.jpg\\?.*?',apRegenValue: (\\d+),defenceRegenValue: (\\d+)\\}\\);");

    public ConquestBattleCheckHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String battleStartType = (String) session.get("battleStartType");
        final String conquestBattleId = (String) session.get("conquestBattleId");
        final String eventId = (String) session.get("eventId");
        final String enemyId = (String) session.get("enemyId");

        final String path = "/conquest/conquest-battle-check";
        final List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("battleStartType", battleStartType));
        if (StringUtils.isNotBlank(eventId)) {
            nvps.add(new BasicNameValuePair("eventId", eventId));
        }
        if (StringUtils.isNotBlank(conquestBattleId)) {
            nvps.add(new BasicNameValuePair("conquestBattleId",
                                            conquestBattleId));
        }
        nvps.add(new BasicNameValuePair("enemyId", enemyId));

        final String html = this.httpPost(path, nvps);
        this.resolveInputToken(html);

        if (this.isBattleResult(html)) {
            return "/conquest/field-result";
        }

        final JSONObject jsonPageParams = this.resolvePageParams(html);
        if (jsonPageParams != null) {
            final String deckId = jsonPageParams.optString("selectedDeckId");
            final boolean enableFullAttack = jsonPageParams.optBoolean("enableFullAttack");
            // jsonPageParams.optBoolean("enableSpecialAttack");
            // jsonPageParams.optInt("useSpecialAttackItemAmount");
            final int tenP = jsonPageParams.optInt("tenP");
            jsonPageParams.optInt("maxTenP");
            final int tenPItem = this.findTenP(html);
            final int power100Item = this.findPower100(html);

            if (this.isHelp(html) || tenP > 0) {
                session.put("useApSmall", "0");
                session.put("useApFull", "0");
                session.put("usePowerHalf", "0");
                session.put("usePowerFull", "0");
                session.put("deckId", deckId);
                session.put("attackType", "1");
                return "/conquest/battle-animation";
            } else if (enableFullAttack) {
                if (power100Item > 0) {
                    session.put("useApSmall", "0");
                    session.put("useApFull", "0");
                    session.put("usePowerHalf", "0");
                    session.put("usePowerFull", "1");
                    session.put("deckId", deckId);
                    session.put("attackType", "2");
                    return "/conquest/battle-animation";
                } else if (tenPItem > 5) {
                    session.put("useApSmall", "5");
                    session.put("useApFull", "0");
                    session.put("usePowerHalf", "0");
                    session.put("usePowerFull", "0");
                    session.put("deckId", deckId);
                    session.put("attackType", "2");
                    return "/conquest/battle-animation";
                }
            }
        }
        return "/mypage";
    }

    private int findTenP(final String html) {
        final Matcher matcher = ConquestBattleCheckHandler.TENP_PATTERN.matcher(html);
        if (matcher.find()) {
            return Integer.valueOf(matcher.group(1));
        }
        return 0;
    }

    private int findPower100(final String html) {
        final Matcher matcher = ConquestBattleCheckHandler.POWER100_PATTERN.matcher(html);
        if (matcher.find()) {
            return Integer.valueOf(matcher.group(1));
        }
        return 0;
    }

    private boolean isHelp(final String html) {
        return StringUtils.contains(html, "救援依頼で攻撃");
    }
}
