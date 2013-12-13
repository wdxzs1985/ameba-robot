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

        final JSONObject data = jsonResponse.getJSONObject("data");
        this.printAreaEncount(data);
        this.printStageRewardFindStatuses(data);
        // 通关
        if (data.getBoolean("clearStage")) {
            return "/quest";
        }

        // 升级
        if (data.getBoolean("levelUp")) {
            return this.onLevelUp(data);
        }
        //
        if (this.isMaxPower(data)) {
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
                    return "/upgrade";
                }
            }
        }
        //
        final String questMessage = data.getString("questMessage");
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
            final int needExpForNextLevel = data.getInt("needExpForNextLevel");
            session.put("needExpForNextLevel", needExpForNextLevel);
            if (this.log.isInfoEnabled()) {
                final JSONObject userData = data.getJSONObject("userData");
                final int stamina = userData.getInt("stamina");
                final int maxStamina = userData.getInt("maxStamina");
                this.log.info(String.format("什么都没有发现，体力[%d/%d]，还有[%d]经验升级。",
                                            stamina,
                                            maxStamina,
                                            needExpForNextLevel));
            }
        }
        return "/quest/stage/forward";
    }

    private void printStageRewardFindStatuses(final JSONObject data) {
        if (this.log.isInfoEnabled()) {
            if (!this.is("isQuestFindAll")) {
                data.getString("stageRewardFindStatuses");
                if (!StringUtils.equals(data.getString("stageRewardFindStatuses"),
                                        "null")) {
                    boolean findAll = true;
                    final JSONArray stageRewardFindStatuses = data.getJSONArray("stageRewardFindStatuses");
                    for (int i = 0; i < stageRewardFindStatuses.size(); i++) {
                        final JSONObject findStatus = stageRewardFindStatuses.getJSONObject(i);
                        findAll = findAll && findStatus.getBoolean("rewardGet");
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

                final String areaEncountType = data.getString("areaEncountType");
                if (!StringUtils.equals(areaEncountType, "null")) {
                    if (StringUtils.equals(areaEncountType, "ITEM")) {
                        final JSONObject encountCardData = data.getJSONObject("encountCardData");
                        final String name = encountCardData.getString("name");
                        this.log.info(String.format("隊士発見: %s", name));
                    } else if (StringUtils.equals(areaEncountType, "EVENT")) {
                        if (this.log.isInfoEnabled()) {
                            final String encountMessage = data.getString("encountMessage");
                            this.log.info(encountMessage);
                        }
                    }
                }
            }
        }
    }

    private boolean isMaxPower(final JSONObject data) {
        final JSONObject userData = data.getJSONObject("userData");
        final int maxPower = userData.getInt("maxPower");
        final int attackPower = userData.getInt("attackPower");
        return maxPower == attackPower;
    }

    private boolean isCardFull(final JSONObject data) {
        final JSONObject userData = data.getJSONObject("userData");
        final int maxCardCount = userData.getInt("maxCardCount");
        final int cardCount = userData.getInt("cardCount");
        return maxCardCount == cardCount;
    }

    private String onLevelUp(final JSONObject data) {
        final Map<String, Object> session = this.robot.getSession();

        final JSONObject userData = data.getJSONObject("userData");
        final int maxStamina = userData.getInt("maxStamina");
        final int maxPower = userData.getInt("maxPower");
        final int attrPoints = userData.getInt("attrPoints");
        final int level = userData.getInt("level");
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
            return "/mypage";
        }
    }

    private boolean isUseItem(final JSONObject data) {
        final String regenStaminaItemsValue = data.getString("regenStaminaItems");
        if (!StringUtils.equals(regenStaminaItemsValue, "null")) {
            final Map<String, Object> session = this.robot.getSession();
            final JSONObject userData = data.getJSONObject("userData");
            final int maxStamina = userData.getInt("maxStamina");
            final int needExpForNextLevel = (Integer) session.get("needExpForNextLevel");
            final JSONArray regenStaminaItems = data.getJSONArray("regenStaminaItems");
            for (int i = 0; i < regenStaminaItems.size(); i++) {
                final JSONObject regenStamina = (JSONObject) regenStaminaItems.get(i);
                final String code = regenStamina.getString("code");
                final String name = regenStamina.getString("name");
                final String itemId = regenStamina.getString("itemId");
                if (this.robot.isUseStaminaToday() && StringUtils.contains(name,
                                                                           "当日")) {
                    session.put("itemId", itemId);
                    session.put("name", name);
                    session.put("callback", "/quest/stage/forward");
                    return true;
                }
                if (this.robot.isUseStamina50() && StringUtils.contains(code,
                                                                        "stamina50")
                    && needExpForNextLevel > maxStamina / 2) {
                    session.put("itemId", itemId);
                    session.put("name", name);
                    session.put("callback", "/quest/stage/forward");
                    return true;
                }
                if (this.robot.isUseStamina100() && StringUtils.contains(code,
                                                                         "stamina100")
                    && needExpForNextLevel > maxStamina) {
                    session.put("itemId", itemId);
                    session.put("name", name);
                    session.put("callback", "/quest/stage/forward");
                    return true;
                }
            }
        }
        return false;
    }
}
