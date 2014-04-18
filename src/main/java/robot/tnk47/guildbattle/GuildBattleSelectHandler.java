package robot.tnk47.guildbattle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class GuildBattleSelectHandler extends Tnk47EventHandler {

    private static final Pattern USER_DISP_PATTERN = Pattern.compile("<dl class=\"userDispList\">(.*?)</dl>");
    private static final Pattern USER_NAME_PATTERN = Pattern.compile("<span class=\"userName .*?\">(.*?)</span>");
    private static final Pattern BATTLE_DT_PATTERN = Pattern.compile("<span class=\"subLinerText\">防御Pt:</span><span class=\"\">(\\d+)</span>");
    private static final Pattern BATTLE_PT_PATTERN = Pattern.compile("<span class=\"battlePtTxt\">(\\d+)</span>");
    private static final Pattern BATTLE_BTN_PATTERN = Pattern.compile("/guildbattle/roundbattle-check\\?enemyId=(\\d+)");
    private static final Pattern CAUTION_PATTERN = Pattern.compile("<p class=\"statusIcon caution\"></p>");

    public GuildBattleSelectHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final String html = this.httpGet("/guildbattle/roundbattle-select");
        this.resolveInputToken(html);

        final List<JSONObject> enemyList = new ArrayList<>();
        final Matcher matcher = GuildBattleSelectHandler.USER_DISP_PATTERN.matcher(html);
        while (matcher.find()) {
            final String userDispHtml = matcher.group(1);
            final JSONObject userJson = this.parseUser(userDispHtml);
            enemyList.add(userJson);
        }

        final JSONObject enemy = this.selectEnemy(enemyList);
        if (enemy != null) {
            final Map<String, Object> session = this.robot.getSession();
            session.put("enemyId", enemy.optString("enemyId"));
            return "/guildbattle/check";
        }

        return "/guildbattle";
    }

    private JSONObject parseUser(final String userDispHtml) {
        final JSONObject userJson = new JSONObject();

        Matcher matcher = null;
        if ((matcher = GuildBattleSelectHandler.USER_NAME_PATTERN.matcher(userDispHtml)).find()) {
            userJson.put("userName", matcher.group(1));
        }
        if ((matcher = GuildBattleSelectHandler.BATTLE_DT_PATTERN.matcher(userDispHtml)).find()) {
            userJson.put("battleDt", matcher.group(1));
        }
        if ((matcher = GuildBattleSelectHandler.BATTLE_PT_PATTERN.matcher(userDispHtml)).find()) {
            userJson.put("battlePt", matcher.group(1));
        }
        if ((matcher = GuildBattleSelectHandler.BATTLE_BTN_PATTERN.matcher(userDispHtml)).find()) {
            userJson.put("enemyId", matcher.group(1));
        }
        if ((matcher = GuildBattleSelectHandler.CAUTION_PATTERN.matcher(userDispHtml)).find()) {
            userJson.put("caution", true);
        } else {
            userJson.put("caution", false);
        }
        return userJson;
    }

    private JSONObject selectEnemy(final List<JSONObject> enemyList) {
        JSONObject enemy = null;
        int maxRank = 0;
        for (final JSONObject user : enemyList) {
            if (user.optBoolean("caution")) {
                continue;
            }
            final int userDt = 1000 - user.optInt("battleDt");
            final int battlePt = user.optInt("battlePt");

            final int userRank = userDt * battlePt;

            if (maxRank < userRank) {
                maxRank = userRank;
                enemy = user;
            }

            this.log.info(String.format("%s / DP: %d / PT: %d / RANK : %d",
                                        user.optString("userName"),
                                        userDt,
                                        battlePt,
                                        userRank));
        }
        return enemy;
    }

}
