package robot.gf.raidwar;

import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import robot.gf.GFEventHandler;
import robot.gf.GFRobot;

public class RaidwarQuestRunHandler extends GFEventHandler {

    public RaidwarQuestRunHandler(final GFRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String eventId = (String) session.get("eventId");
        final String questId = (String) session.get("questId");
        final String stageId = (String) session.get("stageId");
        final String token = (String) session.get("token");

        final String path = String.format("/raidwar/quest/ajax/run?eventId=%s&questId=%s&stageId=%s&token=%s",
                                          eventId,
                                          questId,
                                          stageId,
                                          token);
        final JSONObject jsonResponse = this.httpGetJSON(path);
        this.resolveJsonToken(jsonResponse);

        final JSONObject data = jsonResponse.optJSONObject("data");

        if (data.containsKey("tiredWord")) {
            if (this.log.isInfoEnabled()) {
                this.log.info("精尽人亡");
            }
            return "/mypage";
        }

        final String questResultType = data.optString("questResultType");
        if (StringUtils.equals(questResultType, "STAGE_CLEAR")) {
            this.onClearStage(data);
        } else {
            this.printStageInfo(data);
        }

        if (StringUtils.equals(questResultType, "TOUCH_BONUS")) {
            this.onTouchBonus(data);
            return "/raidwar/quest/touch";
        } else if (StringUtils.equals(questResultType, "GET_REWARD")) {
            this.onRewardCard(data);
        } else if (StringUtils.equals(questResultType, "RAIDWAR")) {
            final JSONObject iRaidwar = data.optJSONObject("iRaidwar");
            final String raidwarId = iRaidwar.optString("raidwarId");
            session.put("raidwarId", raidwarId);
            return "/raidwar/boss";
        } else if (StringUtils.equals(questResultType, "STEALTH")) {
            this.onStealth(data);
        } else if (StringUtils.equals(questResultType, "DEAR_UP")) {
            this.onDearUp(data);
        }

        if (data.optBoolean("questClear", false)) {
            if (this.log.isInfoEnabled()) {
                this.log.info("Quest Clear");
            }
            return "/raidwar";
        }
        return "/raidwar/quest/run";
    }

    private void onTouchBonus(final JSONObject data) {
        final Map<String, Object> session = this.robot.getSession();
        final JSONObject touchBonusUserCard = data.optJSONObject("touchBonusUserCard");
        final String userCardId = touchBonusUserCard.optString("userCardId");
        session.put("userCardId", userCardId);
        if (this.log.isInfoEnabled()) {
            final String touchCardName = data.optString("touchCardName");
            final String touchBonusMsg = data.optString("touchBonusMsg");
            this.log.info(String.format("%s %s", touchCardName, touchBonusMsg));
        }
    }

    private void printStageInfo(final JSONObject data) {
        if (this.log.isInfoEnabled()) {
            final String stageId = data.optString("stageId");
            final String questName = data.optString("questName");
            final String seasonName = data.optString("seasonName");
            final String afterProgress = data.optString("afterProgress");
            this.log.info(String.format("%s / %s %s (%s%%)",
                                        questName,
                                        seasonName,
                                        stageId,
                                        afterProgress));
        }
    }

    private void onClearStage(final JSONObject data) {
        final Map<String, Object> session = this.robot.getSession();
        final String stageId = data.optString("stageId");
        session.put("stageId", stageId);
        if (this.log.isInfoEnabled()) {
            final String seasonName = data.optString("seasonName");
            this.log.info(String.format("Next > %s %s", seasonName, stageId));
        }
    }

    private void onDearUp(final JSONObject data) {
        if (this.log.isInfoEnabled()) {
            final String dearCardName = data.optString("dearCardName");
            final int beforeDearLevel = data.optInt("beforeDearLevel");
            final int beforeDearPoint = data.optInt("beforeDearPoint");
            final int afterDearLevel = data.optInt("afterDearLevel");
            final int afterDearPoint = data.optInt("afterDearPoint");
            this.log.info(String.format("%s的好感度提升 %d(%d) > %d(%d)",
                                        dearCardName,
                                        beforeDearLevel,
                                        beforeDearPoint,
                                        afterDearLevel,
                                        afterDearPoint));
        }
    }

    private void onStealth(final JSONObject data) {
        if (this.log.isInfoEnabled()) {
            if (data.optBoolean("raidwarPointBounusFlg")) {
                final int raidwarPointGain = data.optInt("raidwarPointGain");
                final int raidwarPointGainMinuts = data.optInt("raidwarPointGainMinuts");
                this.log.info(String.format("Point Up %d%% (%d 分钟)",
                                            raidwarPointGain,
                                            raidwarPointGainMinuts));
            }
        }
    }

    private void onRewardCard(final JSONObject data) {
        final String rewardCardName = data.optString("rewardCardName");
        if (this.log.isInfoEnabled()) {
            this.log.info(String.format("发现新妹纸: %s", rewardCardName));
        }
        if (this.isCardFull(data)) {
            if (this.log.isInfoEnabled()) {
                this.log.info("后宫里的妹子满出来了");
            }
        }
    }

    private boolean isCardFull(final JSONObject data) {
        final int cardCount = data.optInt("cardCount", 0);
        final int maxCardCount = data.optInt("maxCardCount", 0);
        return cardCount == maxCardCount;
    }
}
