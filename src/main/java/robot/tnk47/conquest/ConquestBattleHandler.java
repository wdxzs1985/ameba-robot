package robot.tnk47.conquest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47Robot;

public class ConquestBattleHandler extends AbstractConquestBattleHandler {

    private static final Pattern INVITE_PATTERN = Pattern.compile("<a href=\"javascript:void\\(0\\);\" id=\"jsiBtnInvite\" class=\"actionHelpCallBtn \" ><span>救援依頼を出す</span></a>");
    private static final Pattern CANNON_PATTERN = Pattern.compile("<a id=\"jsiBtnCannonAttack\" href=\"javascript:void(0);\" data-enabled=\"true\" data-attack-type=\"1\">");

    public ConquestBattleHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        String conquestBattleId = (String) session.get("conquestBattleId");
        String html = this.httpGet(String.format("/conquest/conquest-battle?conquestBattleId=%s",
                                                 conquestBattleId));
        this.resolveInputToken(html);

        JSONObject pageParams = this.resolvePageParams(html);
        if (pageParams.has("nextUrl")) {
            return "/conquest";
        }
        if (this.canonAttack(html)) {
            return "/conquest";
        }

        this.sendInvite(html);

        String getEnemyListAjaxUrl = pageParams.optString("getEnemyListAjaxUrl");
        String battleStartType = pageParams.optString("battleStartType");
        if (StringUtils.isBlank(conquestBattleId)) {
            conquestBattleId = pageParams.optString("conquestBattleId");
        }

        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("conquestBattleId", conquestBattleId));
        JSONObject response = this.httpPostJSON(getEnemyListAjaxUrl, nvps);
        JSONObject data = response.optJSONObject("data");
        JSONArray enemyList = data.optJSONArray("enemyList");

        if (enemyList.size() > 0) {
            final JSONObject enemy = this.filterEnemy(enemyList);
            final String userId = enemy.optString("userId");
            session.put("battleStartType", battleStartType);
            session.put("enemyId", userId);
            if (this.log.isInfoEnabled()) {
                final String prefectureName = enemy.optString("prefectureName");
                final String userName = enemy.optString("userName");
                final int userLevel = enemy.optInt("userLevel");
                final int userDefencePoint = enemy.optInt("userDefencePoint");
                final int getBattlePoint = enemy.optInt("getBattlePoint");
                this.log.info(String.format("%s / %s (Lv: %d / 防御P: %d / 功績: %d)",
                                            prefectureName,
                                            userName,
                                            userLevel,
                                            userDefencePoint,
                                            getBattlePoint));
            }
            return "/conquest/battle-check";
        }
        return "/conquest";
    }

    private void sendInvite(final String html) {
        final Matcher matcher = INVITE_PATTERN.matcher(html);
        if (matcher.find()) {
            final Map<String, Object> session = this.robot.getSession();
            final String token = (String) session.get("token");

            JSONObject pageParams = this.resolvePageParams(html);
            String conquestBattleId = pageParams.optString("conquestBattleId");
            String url = pageParams.optString("putConquestBattleInviteAjaxUrl");

            final List<BasicNameValuePair> nvps = this.createNameValuePairs();
            nvps.add(new BasicNameValuePair("conquestBattleId",
                                            conquestBattleId));
            nvps.add(new BasicNameValuePair("token", token));

            final JSONObject jsonResponse = this.httpPostJSON(url, nvps);
            this.resolveJsonToken(jsonResponse);
            if (this.log.isInfoEnabled()) {
                final JSONObject data = jsonResponse.optJSONObject("data");
                final String resultMessage = data.optString("resultMessage");
                this.log.info(resultMessage);
            }
        }
    }

    private boolean canonAttack(final String html) {
        final Matcher matcher = CANNON_PATTERN.matcher(html);
        if (matcher.find()) {
            final Map<String, Object> session = this.robot.getSession();
            final String token = (String) session.get("token");

            JSONObject pageParams = this.resolvePageParams(html);
            String conquestBattleId = pageParams.optString("conquestBattleId");
            String url = pageParams.optString("putConquestBattleCannonAjaxUrl");

            final List<BasicNameValuePair> nvps = this.createNameValuePairs();
            nvps.add(new BasicNameValuePair("conquestBattleId",
                                            conquestBattleId));
            nvps.add(new BasicNameValuePair("token", token));

            final JSONObject jsonResponse = this.httpPostJSON(url, nvps);
            this.resolveJsonToken(jsonResponse);
            return true;
        }
        return false;
    }
}
