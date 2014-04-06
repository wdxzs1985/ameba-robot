package robot.tnk47.battle;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47Robot;

public class BattleCheckHandler extends AbstractBattleHandler {

    public static final String NORMAL_ATTACK = "1";
    public static final String FULL_ATTACK = "2";

    public static final String USE_NO_ITEM = "";
    public static final String USE_HALF_ITEM = "0";
    public static final String USE_FULL_ITEM = "1";

    public BattleCheckHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();

        final String battleStartType = (String) session.get("battleStartType");
        final String enemyId = (String) session.get("enemyId");
        final String prefectureBattleId = (String) session.get("prefectureBattleId");
        final String path = "/battle/battle-check";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("battleStartType", battleStartType));
        nvps.add(new BasicNameValuePair("enemyId", enemyId));
        nvps.add(new BasicNameValuePair("prefectureBattleId",
                                        prefectureBattleId));

        final String html = this.httpPost(path, nvps);

        if (this.isBattleResult(html)) {
            return "/battle/prefecture-battle-result";
        }

        this.resolveInputToken(html);

        final JSONObject jsonPageParams = this.resolvePageParams(html);
        if (jsonPageParams != null) {

            final int curPower = jsonPageParams.optInt("curPower");
            final int maxPower = jsonPageParams.optInt("maxPower");
            final JSONObject selectedDeckData = jsonPageParams.optJSONObject("selectedDeckData");
            final int spendAttackPower = selectedDeckData.optInt("spendAttackPower");
            final String deckId = jsonPageParams.optString("selectedDeckId");

            final JSONObject powerRegenItems = jsonPageParams.optJSONObject("powerRegenItems");
            final JSONObject halfRegenUserItemDto = powerRegenItems.optJSONObject("halfRegenUserItemDto");
            final JSONObject fullRegenUserItemDto = powerRegenItems.optJSONObject("fullRegenUserItemDto");

            if (spendAttackPower == 0) {
                session.put("deckId", deckId);
                session.put("attackType", NORMAL_ATTACK);
                session.put("powerRegenItemType", USE_NO_ITEM);
                return "/battle/battle-animation";
            } else if (this.is("isPointRace")) {
                int powerUp = this.getPowerUp(html);
                boolean canFullAttack = this.getCanFullAttack(html);
                if (powerUp >= 50 && canFullAttack) {
                    // can full attack
                    if (maxPower == curPower) {
                        session.put("deckId", deckId);
                        session.put("attackType", FULL_ATTACK);
                        session.put("powerRegenItemType", USE_NO_ITEM);
                        return "/battle/battle-animation";
                    }
                    if (this.robot.isUseHalfPowerRegenItem()) {
                        if (curPower < maxPower / 5) {
                            if (halfRegenUserItemDto.optInt("totalCount") >= 2) {
                                session.put("deckId", deckId);
                                session.put("attackType", FULL_ATTACK);
                                session.put("powerRegenItemType", USE_HALF_ITEM);
                                session.put("useRegenItemCount", "2");
                                return "/battle/battle-animation";
                            }

                        } else if ((curPower < maxPower * 3 / 5) && (curPower > maxPower / 2)) {
                            if (halfRegenUserItemDto.optInt("totalCount") >= 1) {
                                session.put("deckId", deckId);
                                session.put("attackType", FULL_ATTACK);
                                session.put("powerRegenItemType", USE_HALF_ITEM);
                                session.put("useRegenItemCount", "1");
                                return "/battle/battle-animation";
                            }
                        }
                    }

                    if (this.robot.isUseFullPowerRegenItem()) {
                        if (curPower < maxPower / 5) {
                            if (fullRegenUserItemDto.optInt("totalCount") >= 1) {
                                session.put("deckId", deckId);
                                session.put("attackType", FULL_ATTACK);
                                session.put("powerRegenItemType", USE_FULL_ITEM);
                                session.put("useRegenItemCount", "1");
                                return "/battle/battle-animation";
                            }
                        }
                    }
                }
            }

            if (curPower >= spendAttackPower) {
                session.put("deckId", deckId);
                session.put("attackType", NORMAL_ATTACK);
                session.put("powerRegenItemType", USE_NO_ITEM);
                return "/battle/battle-animation";
            } else {
                final int halfRegenTodayCount = halfRegenUserItemDto.optInt("todayCount");
                if (this.robot.isUseTodayPowerRegenItem() && halfRegenTodayCount > 0) {
                    final String itemName = halfRegenUserItemDto.optString("itemName");
                    session.put("itemName", itemName);
                    session.put("deckId", deckId);
                    session.put("attackType", NORMAL_ATTACK);
                    session.put("powerRegenItemType", USE_HALF_ITEM);
                    return "/battle/battle-animation";
                }

                final int fullRegenTodayCount = fullRegenUserItemDto.optInt("todayCount");
                if (this.robot.isUseTodayPowerRegenItem() && fullRegenTodayCount > 0) {
                    final String itemName = fullRegenUserItemDto.optString("itemName");
                    session.put("itemName", itemName);
                    session.put("deckId", deckId);
                    session.put("attackType", NORMAL_ATTACK);
                    session.put("powerRegenItemType", USE_FULL_ITEM);
                    return "/battle/battle-animation";
                }

                session.put("isBattlePowerOut", true);
                if (this.log.isInfoEnabled()) {
                    this.log.info("攻pt不足");
                }
            }
        }
        return "/mypage";
    }

    private boolean getCanFullAttack(String html) {
        // TODO Auto-generated method stub
        return false;
    }

    private int getPowerUp(String html) {
        // TODO Auto-generated method stub
        return 0;
    }
}
