package robot.tnk47.quest;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class QuestStageForwardHandler extends AbstractEventHandler {

    public QuestStageForwardHandler(final Robot robot) {
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
        this.resolveJsonToken(jsonResponse);

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

        if (this.isMaxPower(data)) {
            session.put("quest", true);
            return "/battle";
        }
        //
        final String questMessage = data.getString("questMessage");
        if (!StringUtils.equals(questMessage, "null")) {
            if (this.log.isInfoEnabled()) {
                this.log.info(questMessage);
            }
            if (StringUtils.equals("行動Ptが足りません", questMessage)) {
                return this.onStaminaOut(data);
            } else if (StringUtils.equals("隊士発見!!", questMessage)) {
                // do nothing
            }
        } else {
            if (this.log.isInfoEnabled()) {
                final String needExpForNextLevel = data.getString("needExpForNextLevel");
                session.put("needExpForNextLevel", needExpForNextLevel);
                this.log.info(String.format("什么都没有发现，还有[%s]经验升级。",
                                            needExpForNextLevel));
            }
        }
        return "/quest/stage/forward";
    }

    private void printStageRewardFindStatuses(final JSONObject data) {
        if (this.log.isInfoEnabled()) {
            final String stageRewardFindStatusesValue = data.getString("stageRewardFindStatuses");
            if (!StringUtils.equals(stageRewardFindStatusesValue, "null")) {
                boolean findAll = true;
                final JSONArray stageRewardFindStatuses = data.getJSONArray("stageRewardFindStatuses");
                for (int i = 0; i < stageRewardFindStatuses.size(); i++) {
                    final JSONObject findStatus = stageRewardFindStatuses.getJSONObject(i);
                    findAll = findAll && findStatus.getBoolean("rewardGet");
                }
                if (findAll) {
                    this.log.info("这张地图的卡片已经集齐");
                }
            }
        }
    }

    private void printAreaEncount(final JSONObject data) {
        if (this.log.isInfoEnabled()) {
            final String areaEncountType = data.getString("areaEncountType");
            if (!StringUtils.equals(areaEncountType, "null")) {
                if (StringUtils.equals(areaEncountType, "ITEM")) {
                    final JSONObject encountCardData = data.getJSONObject("encountCardData");
                    final String name = encountCardData.getString("name");
                    this.log.info(String.format("隊士発見: %s", name));
                } else if (StringUtils.equals(areaEncountType, "EVENT")) {
                    final String encountMessage = data.getString("encountMessage");
                    this.log.info(encountMessage);
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
            if (this.log.isInfoEnabled()) {
                this.log.info("等回血");
            }
            return "/mypage";
        }
    }

    private boolean isUseItem(final JSONObject data) {
        final String regenStaminaItemsValue = data.getString("stageRewardFindStatuses");
        if (!StringUtils.equals(regenStaminaItemsValue, "null")) {
            final Map<String, Object> session = this.robot.getSession();
            final Properties config = this.robot.getConfig();
            final JSONObject userData = data.getJSONObject("userData");
            final int maxStamina = userData.getInt("maxStamina");
            final boolean useStaminaToday = Boolean.valueOf(config.getProperty("QuestStageForwardHandler.useStaminaToday",
                                                                               "false"));
            final boolean useStamina50 = Boolean.valueOf(config.getProperty("QuestStageForwardHandler.useStamina50",
                                                                            "false"));
            final boolean useStamina100 = Boolean.valueOf(config.getProperty("QuestStageForwardHandler.useStamina100",
                                                                             "false"));
            final int needExpForNextLevel = Integer.valueOf(config.getProperty("needExpForNextLevel",
                                                                               "0"));
            final JSONArray regenStaminaItems = data.getJSONArray("regenStaminaItems");
            for (int i = 0; i < regenStaminaItems.size(); i++) {
                final JSONObject regenStamina = (JSONObject) regenStaminaItems.get(i);
                final String code = regenStamina.getString("code");
                final String name = regenStamina.getString("name");
                final String itemId = regenStamina.getString("itemId");
                if (useStaminaToday && StringUtils.contains(name, "当日")) {
                    session.put("itemId", itemId);
                    session.put("name", name);
                    session.put("callback", "/quest/stage/forward");
                    return true;
                } else if (StringUtils.contains(code, "stamina50") && useStamina50
                           && needExpForNextLevel > maxStamina / 2) {
                    session.put("itemId", itemId);
                    session.put("name", name);
                    session.put("callback", "/quest/stage/forward");
                    return true;
                } else if (StringUtils.contains(code, "stamina100") && useStamina100
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
