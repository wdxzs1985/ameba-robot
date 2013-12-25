package robot.tnk47.raid;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class RaidStageForwardHandler extends Tnk47EventHandler {

    private static final Pattern BOSS_ENCOUNT_PATTERN = Pattern.compile("/raid/raid-boss-encount-animation\\?raidBossId=(\\d+)&raidBossLevel=(\\d+)&raidId=(\\d+)&questId=(\\d+)&areaId=(\\d+)&stageId=(\\d+)&token=([a-zA-Z0-9]{6})");

    public RaidStageForwardHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidId = (String) session.get("raidId");
        final String questId = (String) session.get("questId");
        final String areaId = (String) session.get("areaId");
        final String stageId = (String) session.get("stageId");
        final String token = (String) session.get("token");
        final String path = "/raid/ajax/put-raid-stage-forward";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("raidId", raidId));
        nvps.add(new BasicNameValuePair("questId", questId));
        nvps.add(new BasicNameValuePair("areaId", areaId));
        nvps.add(new BasicNameValuePair("stageId", stageId));
        nvps.add(new BasicNameValuePair("token", token));
        final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        this.resolveJsonToken(jsonResponse);

        final JSONObject data = jsonResponse.optJSONObject("data");
        if (data != null) {
            final int needExpForNextLevel = data.optInt("needExpForNextLevel",
                                                        0);
            if (needExpForNextLevel > 0) {
                session.put("needExpForNextLevel", needExpForNextLevel);
                final JSONObject userData = data.optJSONObject("userData");
                final int stamina = userData.optInt("stamina");
                final int maxStamina = userData.optInt("maxStamina");
                if (this.log.isInfoEnabled()) {
                    this.log.info(String.format("体力[%d/%d]，还有[%d]经验升级。",
                                                stamina,
                                                maxStamina,
                                                needExpForNextLevel));
                }
            }

            final String areaEncountType = data.optString("areaEncountType");
            if (!StringUtils.equals(areaEncountType, "NONE")) {
                if (StringUtils.equals(areaEncountType, "ITEM")) {
                    if (this.log.isInfoEnabled()) {
                        final JSONObject encountCardData = data.optJSONObject("encountCardData");
                        final String name = encountCardData.optString("name");
                        this.log.info(String.format("隊士発見: %s", name));
                    }
                } else if (StringUtils.equals(areaEncountType, "RECOVERY_AP")) {
                    if (this.log.isInfoEnabled()) {
                        final String encountMessage = data.optString("encountMessage");
                        this.log.info(encountMessage);
                    }
                    return "/raid";
                } else {
                    if (this.log.isInfoEnabled()) {
                        final String encountMessage = data.optString("encountMessage");
                        this.log.info(encountMessage);
                    }
                }
            }
            // 通关
            if (data.optBoolean("clearStage")) {
                return "/raid/stage";
            }
            //
            final String questMessage = data.optString("questMessage");
            if (!StringUtils.equals(questMessage, "null")) {
                if (StringUtils.equals("行動Ptが足りません", questMessage)) {
                    if (this.log.isInfoEnabled()) {
                        this.log.info("体力不支");
                    }
                    return this.onStaminaOut(data);
                } else if (StringUtils.equals("隊士発見!!", questMessage)) {
                    // do nothing
                }
            }
            return "/raid/stage-forward";
        } else {
            return this.battleEncountAnimation(jsonResponse);
        }
    }

    private String battleEncountAnimation(final JSONObject jsonResponse) {
        final Map<String, Object> session = this.robot.getSession();
        final String url = jsonResponse.optString("url");
        final Matcher matcher = RaidStageForwardHandler.BOSS_ENCOUNT_PATTERN.matcher(url);
        if (matcher.find()) {
            final String raidBossId = matcher.group(1);
            final String raidBossLevel = matcher.group(2);
            final String raidId = matcher.group(3);
            final String questId = matcher.group(4);
            final String areaId = matcher.group(5);
            final String stageId = matcher.group(6);
            final String token = matcher.group(7);

            session.put("raidBossId", raidBossId);
            session.put("raidBossLevel", raidBossLevel);
            session.put("raidId", raidId);
            session.put("questId", questId);
            session.put("areaId", areaId);
            session.put("stageId", stageId);
            session.put("token", token);
            return "/raid/battle-encount";
        }
        return "/mypage";
    }

    private String onStaminaOut(final JSONObject data) {
        if (this.isUseItem(data)) {
            return "/use-item";
        } else {
            return "/mypage";
        }
    }

    private boolean isUseItem(final JSONObject data) {
        final JSONArray regenStaminaItems = data.optJSONArray("regenStaminaItems");
        if (regenStaminaItems != null) {
            final Map<String, Object> session = this.robot.getSession();
            final JSONObject userData = data.optJSONObject("userData");
            final int maxStamina = userData.optInt("maxStamina");
            final int needExpForNextLevel = (Integer) session.get("needExpForNextLevel");
            for (int i = 0; i < regenStaminaItems.size(); i++) {
                final JSONObject regenStamina = (JSONObject) regenStaminaItems.get(i);
                final String code = regenStamina.optString("code");
                final String name = regenStamina.optString("name");
                final String itemId = regenStamina.optString("itemId");
                if (this.robot.isUseStaminaToday() && StringUtils.contains(name,
                                                                           "当日")) {
                    session.put("itemId", itemId);
                    session.put("name", name);
                    session.put("callback", "/raid/stage");
                    return true;
                }
                if (this.robot.isUseStamina50() && StringUtils.contains(code,
                                                                        "stamina50")
                        && needExpForNextLevel > maxStamina / 2) {
                    session.put("itemId", itemId);
                    session.put("name", name);
                    session.put("callback", "/raid/stage");
                    return true;
                }
                if (this.robot.isUseStamina100() && StringUtils.contains(code,
                                                                         "stamina100")
                        && needExpForNextLevel > maxStamina) {
                    session.put("itemId", itemId);
                    session.put("name", name);
                    session.put("callback", "/raid/stage");
                    return true;
                }
            }
        }
        return false;
    }
}
