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

        if (this.log.isInfoEnabled()) {
            final String seasonName = data.optString("seasonName");
            final String questName = data.optString("questName");
            final String stageName = data.optString("stageName");
            final String afterProgress = data.optString("afterProgress");
            this.log.info(String.format("%s / %s / %s (%s%%)",
                                        seasonName,
                                        questName,
                                        stageName,
                                        afterProgress));
        }

        final String questResultType = data.optString("questResultType");
        if (StringUtils.equals(questResultType, "TOUCH_BONUS")) {
            return "/raidwar/quest/touch";
        } else if (StringUtils.equals(questResultType, "GET_REWARD")) {
            final String rewardCardName = data.optString("rewardCardName");
            if (this.log.isInfoEnabled()) {
                this.log.info(String.format("发现新妹纸: %s", rewardCardName));
            }
            if (this.isCardFull(data)) {
                if (this.log.isInfoEnabled()) {
                    this.log.info("后宫里的妹子满出来了");
                }
            }
        } else if (StringUtils.equals(questResultType, "RAIDWAR")) {
            final String raidwarId = data.optString("raidwarId");
            session.put("raidwarId", raidwarId);
            // return "/raidwar/boss";
        } else if (StringUtils.equals(questResultType, "STAGE_CLEAR")) {
            if (this.log.isInfoEnabled()) {
                this.log.info("Stage Clear");
            }
        } else if (StringUtils.equals(questResultType, "STEALTH")) {
            if (this.log.isInfoEnabled()) {
                if (data.optBoolean("raidwarPointBounusFlg")) {
                    final int raidwarPointGain = data.optInt("raidwarPointGain");
                    final int raidwarPointGainMinuts = data.optInt("raidwarPointGainMinuts");
                    this.log.info(String.format("Point Up %d%% (%d 分钟)",
                                                raidwarPointGain,
                                                raidwarPointGainMinuts));
                }
            }
        } else if (StringUtils.equals(questResultType, "DEAR_UP")) {
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

        if (data.optBoolean("questClear", false)) {
            if (this.log.isInfoEnabled()) {
                this.log.info("Quest Clear");
            }
            return "/raidwar";
        }
        return "/raidwar/quest/run";
    }

    private boolean isCardFull(final JSONObject data) {
        final int cardCount = data.optInt("cardCount", 0);
        final int maxCardCount = data.optInt("maxCardCount", 0);
        return cardCount == maxCardCount;
    }

}
