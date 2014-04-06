package robot.tnk47.marathon;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class MarathonStageForwardHandler extends Tnk47EventHandler {

    public MarathonStageForwardHandler(final Tnk47Robot robot) {
        super(robot);
        final Map<String, Object> session = robot.getSession();
        session.put("needExpForNextLevel", 0);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String eventId = (String) session.get("eventId");
        final String token = (String) session.get("token");
        final String path = "/event/marathon/ajax/put-event-stage-forward";
        final List<BasicNameValuePair> nvps = new LinkedList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("eventId", eventId));
        nvps.add(new BasicNameValuePair("token", token));

        final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        if (jsonResponse.containsKey("token")) {
            this.resolveJsonToken(jsonResponse);
        }

        final JSONObject data = jsonResponse.optJSONObject("data");
        final int needExpForNextLevel = data.optInt("needExpForNextLevel", 0);
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
        if (!StringUtils.equals(areaEncountType, "null")) {
            if (StringUtils.equals(areaEncountType, "ITEM")) {
                if (this.log.isInfoEnabled()) {
                    final JSONObject encountCardData = data.optJSONObject("encountCardData");
                    final String name = encountCardData.optString("name");
                    this.log.info(String.format("隊士発見: %s", name));
                }
            } else if (StringUtils.equals(areaEncountType, "EVENT")) {
                if (this.log.isInfoEnabled()) {
                    final String encountMessage = data.optString("encountMessage");
                    this.log.info(encountMessage);
                }
            } else if (StringUtils.equals(areaEncountType, "SCORE")) {
                if (this.log.isInfoEnabled()) {
                    final String encountMessage = data.optString("encountMessage");
                    this.log.info(encountMessage);
                }
            } else if (StringUtils.equals(areaEncountType, "MISSION")) {
                final JSONObject marathonMissionInfoDto = data.optJSONObject("marathonMissionInfoDto");
                if (this.log.isInfoEnabled()) {
                    final String name = marathonMissionInfoDto.optString("name");
                    this.log.info(String.format("%s出现了", name));
                }
                final String userMissionId = marathonMissionInfoDto.optString("userMissionId");
                session.put("userMissionId", userMissionId);
                return "/marathon/mission";
            }
        }

        // 通关
        if (data.optBoolean("clearStage")) {
            return "/marathon";
        }

        // 升级
        if (data.optBoolean("levelUp")) {
            return this.onLevelUp(data);
        }
        //
        if (this.isCardFull(data)) {
            if (!this.is("isQuestCardFull")) {
                session.put("isQuestCardFull", true);
                if (this.log.isInfoEnabled()) {
                    this.log.info("你包里的卡满出来了");
                }
                if (this.is("isUpgradeEnable")) {
                    session.put("isUpgradeEnable", false);
                    session.put("callback", "/marathon/stage");
                    return "/upgrade";
                }
            }
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
        return "/marathon/stage/forward";
    }

    private boolean isCardFull(final JSONObject data) {
        final JSONObject userData = data.optJSONObject("userData");
        final int maxCardCount = userData.optInt("maxCardCount");
        final int cardCount = userData.optInt("cardCount");
        return maxCardCount == cardCount;
    }

    private String onLevelUp(final JSONObject data) {
        final Map<String, Object> session = this.robot.getSession();

        final JSONObject userData = data.optJSONObject("userData");
        final int maxStamina = userData.optInt("maxStamina");
        final int maxPower = userData.optInt("maxPower");
        final int attrPoints = userData.optInt("attrPoints");
        final int level = userData.optInt("level");
        if (this.log.isInfoEnabled()) {
            this.log.info(String.format("升到了%d级", level));
        }
        session.put("maxStamina", maxStamina);
        session.put("maxPower", maxPower);
        session.put("attrPoints", attrPoints);
        session.put("callback", "/marathon");
        return "/status-up";
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
                    session.put("callback", "/mypage");
                    return true;
                } else if (this.robot.isUseStamina50() && StringUtils.contains(code,
                                                                               "stamina50")
                        && needExpForNextLevel > maxStamina / 2) {
                    session.put("itemId", itemId);
                    session.put("name", name);
                    session.put("callback", "/mypage");
                    return true;
                } else if (this.robot.isUseStamina100() && StringUtils.contains(code,
                                                                                "stamina100")
                        && needExpForNextLevel > maxStamina) {
                    session.put("itemId", itemId);
                    session.put("name", name);
                    session.put("callback", "/mypage");
                    return true;
                }
            }
        }
        return false;
    }
}
