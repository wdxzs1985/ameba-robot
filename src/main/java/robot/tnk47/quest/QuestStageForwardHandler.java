package robot.tnk47.quest;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class QuestStageForwardHandler extends Tnk47EventHandler {

    public QuestStageForwardHandler(final Tnk47Robot robot) {
        super(robot);
        final Map<String, Object> session = robot.getSession();
        session.put("needExpForNextLevel", 0);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();

        final String questId = (String) session.get("questId");
        final String areaId = (String) session.get("areaId");
        final String stageId = (String) session.get("stageId");
        final String token = (String) session.get("token");
        final String path = "/quest/ajax/put-stage-forward";
        final List<BasicNameValuePair> nvps = new LinkedList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("questId", questId));
        nvps.add(new BasicNameValuePair("areaId", areaId));
        nvps.add(new BasicNameValuePair("stageId", stageId));
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

        this.printAreaEncount(data);
        this.printStageRewardFindStatuses(data);
        // 通关
        if (data.optBoolean("clearStage")) {
            return "/quest";
        }

        // 升级
        if (data.optBoolean("levelUp")) {
            return this.onLevelUp(data);
        }
        //
        if (!this.is("isConquest") && this.isMaxPower(data)) {
            session.put("isBattlePowerOut", false);
            if (!this.is("isBattlePointEnough")) {
                return "/battle";
            }
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
                    session.put("callback", "/quest");
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
        } else {
            if (this.log.isInfoEnabled()) {
                this.log.info("什么都没有发现");
            }
        }
        return "/quest/stage/forward";
    }

    private void printStageRewardFindStatuses(final JSONObject data) {
        if (this.log.isInfoEnabled()) {
            if (!this.is("isQuestFindAll")) {
                final JSONArray stageRewardFindStatuses = data.optJSONArray("stageRewardFindStatuses");
                if (stageRewardFindStatuses != null) {
                    boolean findAll = true;
                    for (int i = 0; i < stageRewardFindStatuses.size(); i++) {
                        final JSONObject findStatus = stageRewardFindStatuses.optJSONObject(i);
                        findAll = findAll && findStatus.optBoolean("rewardGet");
                    }
                    if (findAll) {
                        this.log.info("这张地图的卡片已经集齐");
                        final Map<String, Object> session = this.robot.getSession();
                        session.put("isQuestFindAll", true);
                    }
                }
            }
        }
    }

    private void printAreaEncount(final JSONObject data) {
        if (this.log.isInfoEnabled()) {
            if (data.containsKey("areaEncountType")) {

                final String areaEncountType = data.optString("areaEncountType");
                if (!StringUtils.equals(areaEncountType, "null")) {
                    if (StringUtils.equals(areaEncountType, "ITEM")) {
                        final JSONObject encountCardData = data.optJSONObject("encountCardData");
                        final String name = encountCardData.optString("name");
                        this.log.info(String.format("隊士発見: %s", name));
                    } else if (StringUtils.equals(areaEncountType, "EVENT")) {
                        if (this.log.isInfoEnabled()) {
                            final String encountMessage = data.optString("encountMessage");
                            this.log.info(encountMessage);
                        }
                    }
                }
            }
        }
    }

    private boolean isMaxPower(final JSONObject data) {
        final JSONObject userData = data.optJSONObject("userData");
        final int maxPower = userData.optInt("maxPower");
        final int attackPower = userData.optInt("attackPower");
        return maxPower == attackPower;
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
        session.put("callback", "/quest/stage/forward");
        return "/status-up";
    }

    private String onStaminaOut(final JSONObject data) {
        if (this.isUseItem(data)) {
            return "/use-item";
        } else {
            this.robot.getSession();
            return "/mypage";
        }
    }

    private boolean isUseItem(final JSONObject data) {
        final JSONArray regenStaminaItems = data.optJSONArray("regenStaminaItems");
        if (regenStaminaItems != null) {
            final Map<String, Object> session = this.robot.getSession();
            final JSONObject userData = data.optJSONObject("userData");
            final int maxStamina = userData.optInt("maxStamina") * this.robot.getUseStaminaRatio()
                    / 100;
            final int needExpForNextLevel = (Integer) session.get("needExpForNextLevel");
            for (int i = 0; i < regenStaminaItems.size(); i++) {
                final JSONObject regenStamina = (JSONObject) regenStaminaItems.get(i);
                final String code = regenStamina.optString("code");
                final String name = regenStamina.optString("name");
                final String itemId = regenStamina.optString("itemId");
                if (StringUtils.contains(name, "当日")) {
                    if (StringUtils.contains(code, "stamina50") && needExpForNextLevel > maxStamina / 2) {
                        session.put("itemId", itemId);
                        session.put("name", name);
                        session.put("callback", "/quest/stage/forward");
                        return true;
                    }
                    if (StringUtils.contains(code, "stamina100") && needExpForNextLevel > maxStamina) {
                        session.put("itemId", itemId);
                        session.put("name", name);
                        session.put("callback", "/quest/stage/forward");
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
