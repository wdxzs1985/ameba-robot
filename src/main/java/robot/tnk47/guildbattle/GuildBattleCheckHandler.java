package robot.tnk47.guildbattle;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class GuildBattleCheckHandler extends Tnk47EventHandler {

    private static final Pattern BATTLE_POINT_UP = Pattern.compile("<span class=\"battlePointUp\">決戦Pt\\+(\\d+)%</span>");
    private static final String FULL_ATTACK = "全力で戦うと大ダメージ";

    public GuildBattleCheckHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String enemyId = (String) session.get("enemyId");
        final String path = String.format("/guildbattle/roundbattle-check?enemyId=%s",
                                          enemyId);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);

        final JSONObject jsonPageParams = this.resolvePageParams(html);
        if (jsonPageParams != null) {
            final int maxPower = jsonPageParams.optInt("maxPower");
            final int curPower = jsonPageParams.optInt("curPower");
            final String deckId = jsonPageParams.optString("selectedDeckId");
            String attackType = "";
            String powerRegenItemType = "";
            String useRegenItemCount = "";

            if (this.canFullAttack(html)) {
                attackType = "2";
                if (curPower < maxPower) {
                    final JSONObject powerRegenItems = jsonPageParams.optJSONObject("powerRegenItems");
                    final JSONObject powerRegenItemDto = powerRegenItems.optJSONObject("powerRegenItemDto");
                    if (powerRegenItems.optBoolean("hasGuildbattleItem")) {
                        powerRegenItemType = "1";
                        useRegenItemCount = "1";
                    } else if (powerRegenItemDto.optBoolean("hasHalfRegenItem")) {
                        powerRegenItemType = "0";
                        if (curPower < maxPower / 2) {
                            useRegenItemCount = "2";
                        } else {
                            useRegenItemCount = "1";
                        }
                    } else if (powerRegenItemDto.optBoolean("hasFullRegenItem")) {
                        powerRegenItemType = "1";
                        useRegenItemCount = "1";
                    } else {
                        return "/guildbattle";
                    }
                }
            } else {
                attackType = "1";
                final JSONObject selectedDeckData = jsonPageParams.optJSONObject("selectedDeckData");
                final int spendAttackPower = selectedDeckData.optInt("spendAttackPower");
                if (curPower < spendAttackPower) {
                    final JSONObject powerRegenItems = jsonPageParams.optJSONObject("powerRegenItems");
                    if (powerRegenItems.optBoolean("hasGuildbattleItem")) {
                        powerRegenItemType = "1";
                        useRegenItemCount = "1";
                    } else {
                        return "/guildbattle";
                    }
                }
            }

            session.put("enemyId", enemyId);
            session.put("deckId", deckId);
            session.put("attackType", attackType);
            session.put("powerRegenItemType", powerRegenItemType);
            session.put("useRegenItemCount", useRegenItemCount);
            return "/guildbattle/animation";
        }
        return "/guildbattle";
    }

    private boolean canFullAttack(final String html) {
        if (StringUtils.contains(html, GuildBattleCheckHandler.FULL_ATTACK)) {
            final Matcher matcher = GuildBattleCheckHandler.BATTLE_POINT_UP.matcher(html);
            if (matcher.find()) {
                final int pointUp = Integer.valueOf(matcher.group(1));
                if (pointUp > 80) {
                    return true;
                }
            }
        }
        return false;
    }
}
