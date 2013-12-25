package robot.tnk47.raid;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class RaidBattleHandler extends Tnk47EventHandler {

    private static final Pattern ITEM_PATTERN = Pattern.compile("tnk.pageParams.itemList.push\\((.*?)\\);");
    private static final Pattern FEVER_PATTERN = Pattern.compile("みんなでフィーバー！");
    private static final Pattern HELP_PATTERN = Pattern.compile("助けを呼ぶ");

    private static final Pattern RAID_RESULT_DATA_PATTERN = Pattern.compile("raidResultData = '(\\{.*\\})';");
    private static final Pattern TOTAL_POINT_PATTERN = Pattern.compile("<span class=\"totalPoint\">(\\d+)</span>");
    private static final Pattern FEVER_RATE_PATTERN = Pattern.compile("<span class=\"feverRateNum\">(\\d+)</span>");
    private static final Pattern SPECIAL_ATTACK_PATTERN = Pattern.compile("超全力秘薬を1/(\\d+)消費");
    private static final Pattern BOSS_HP_MATER_PATTERN = Pattern.compile("<span>HP</span><span class=\"mater\"><em style=\"width: (\\d{1,3})%;\">");

    private final boolean useRaidRegenItem;
    private final boolean useRaidSpecialAttack;

    public RaidBattleHandler(final Tnk47Robot robot) {
        super(robot);
        this.useRaidRegenItem = robot.isUseRaidRegenItem();
        this.useRaidSpecialAttack = robot.isUseRaidSpecialAttack();
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidBattleId = (String) session.get("raidBattleId");
        final String token = (String) session.get("token");
        final String path = String.format("/raid/raid-battle?raidBattleId=%s&token=%s",
                                          raidBattleId,
                                          token);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);

        if (this.isRaid(html)) {
            if (RaidBattleHandler.HELP_PATTERN.matcher(html).find()) {
                this.sleep();
                this.raidInvite();
            }
            if (RaidBattleHandler.FEVER_PATTERN.matcher(html).find()) {
                this.sleep();
                this.raidBattleFever();
            }
            this.sleep();

            int totalPoint = this.getTotalPoint(html);
            final int feverRate = this.getFeverRate(html);
            totalPoint += totalPoint * feverRate / 100;

            final int specialAttack = this.getSpecialAttack(html);

            final int maxHp = (Integer) session.get("maxHp");
            final int bossHpPercent = this.getBossHpPercent(html);
            final int currentHp = maxHp * bossHpPercent / 100;

            final boolean isMine = (Boolean) session.get("isMine");

            this.log.info(String.format("Hp: %d/%d, Damage: %d(+%d%%)",
                                        currentHp,
                                        maxHp,
                                        totalPoint,
                                        feverRate));
            final JSONObject pageParams = this.resolvePageParams(html);
            // boss data
            final String deckId = pageParams.optString("selectedDeckId");
            final int apCost = pageParams.optInt("apCost");
            final int maxAp = pageParams.optInt("maxAp");
            int apNow = pageParams.optInt("apNow");

            int apSmall = 0;
            int apFull = 0;
            int powerHalf = 0;
            int powerFull = 0;
            if (this.useRaidRegenItem) {
                final JSONArray itemList = pageParams.optJSONArray("itemList");
                if (itemList != null) {
                    for (int i = 0; i < itemList.size(); i++) {
                        final JSONObject item = itemList.optJSONObject(i);
                        final String imgPath = item.optString("imgPath");
                        final boolean isOneDay = StringUtils.contains(imgPath,
                                                                      "oneday");
                        final int itemCount = item.optInt("itemCount");
                        switch (i) {
                        case 0:
                            apSmall = itemCount;
                            break;
                        case 1:
                            apFull = itemCount;
                            break;
                        case 2:
                            powerHalf = isOneDay ? itemCount : 0;
                            break;
                        case 3:
                            powerFull = isOneDay ? itemCount : 0;
                            break;
                        default:
                            break;
                        }
                    }
                }
                this.log.info(String.format("apSmall: %d本", apSmall));
                this.log.info(String.format("apFull: %d本", apFull));
                this.log.info(String.format("powerHalf: %d本", powerHalf));
                this.log.info(String.format("powerFull: %d本", powerFull));
                this.log.info(String.format("SP: %d本", specialAttack));
            }

            int useApSmall = 0;
            int useApFull = 0;
            int usePowerHalf = 0;
            int usePowerFull = 0;
            boolean isFullPower = false;
            boolean isSpecialAttack = false;
            boolean canAttack = false;
            if (apCost == 0) {
                canAttack = true;
            } else if (isMine) {
                final int raidBossType = (Integer) session.get("raidBossType");
                if (raidBossType == 2) {
                    // 大boss
                    if (currentHp > totalPoint * 13 && specialAttack > 0) {
                        isSpecialAttack = this.useRaidSpecialAttack;
                        canAttack = true;
                    } else if (currentHp > totalPoint * 4) {
                        if (apNow == maxAp) {
                            canAttack = true;
                            isFullPower = true;
                        } else {
                            while (apSmall > 0 && apNow < maxAp) {
                                apNow++;
                                useApSmall++;
                                apSmall--;
                            }
                            while (apFull > 0 && apNow == 0) {
                                apNow = maxAp;
                                useApFull++;
                                apFull--;
                            }
                            while (powerHalf > 0 && apNow < maxAp - 2) {
                                apNow += 2;
                                usePowerHalf++;
                                powerHalf--;
                            }
                            while (powerFull > 0 && apNow == 0) {
                                apNow = maxAp;
                                usePowerFull++;
                                powerFull--;
                            }
                            canAttack = apNow >= apCost;
                            isFullPower = apNow == maxAp;
                        }
                    }
                } else {
                    canAttack = apNow >= apCost;
                }
            } else if (feverRate > 50) {
                canAttack = apNow >= apCost;
                isFullPower = apNow == maxAp;
            }

            if (canAttack) {
                if (this.log.isInfoEnabled()) {
                    int useAp = apCost;
                    if (isFullPower) {
                        useAp = 5;
                        this.log.info("全力攻撃");
                    } else if (isSpecialAttack) {
                        useAp = 0;
                        this.log.info("超全力攻撃");
                    }
                    this.log.info(String.format("AP: %d/%d USE: %d",
                                                apNow,
                                                maxAp,
                                                useAp));
                }

                session.put("deckId", deckId);
                session.put("isFullPower", String.valueOf(isFullPower));
                session.put("isSpecialAttack", String.valueOf(isSpecialAttack));
                session.put("useApSmall", String.valueOf(useApSmall));
                session.put("useApFull", String.valueOf(useApFull));
                session.put("usePowerHalf", String.valueOf(usePowerHalf));
                session.put("usePowerFull", String.valueOf(usePowerFull));
                this.raidAnimation();
                return "/raid/battle";
            }
        } else if (this.isRaidResult(html)) {
            return "/raid/battle-result";
        }
        return "/raid";
    }

    private int getSpecialAttack(final String html) {
        final Matcher matcher = RaidBattleHandler.SPECIAL_ATTACK_PATTERN.matcher(html);
        while (matcher.find()) {
            final int specialAttack = Integer.valueOf(matcher.group(1));
            return specialAttack;
        }
        return 0;
    }

    private int getBossHpPercent(final String html) {
        final Matcher matcher = RaidBattleHandler.BOSS_HP_MATER_PATTERN.matcher(html);
        while (matcher.find()) {
            final int bossHpPercent = Integer.valueOf(matcher.group(1));
            return bossHpPercent;
        }
        return 0;
    }

    private int getTotalPoint(final String html) {
        int totalPoint = 0;
        final Matcher matcher = RaidBattleHandler.TOTAL_POINT_PATTERN.matcher(html);
        while (matcher.find()) {
            final int point = Integer.valueOf(matcher.group(1));
            if (totalPoint < point) {
                totalPoint = point;
            }
        }
        return totalPoint;
    }

    private int getFeverRate(final String html) {
        int feverRate = 0;
        final Matcher matcher = RaidBattleHandler.FEVER_RATE_PATTERN.matcher(html);
        while (matcher.find()) {
            feverRate = Integer.valueOf(matcher.group(1));
        }
        return feverRate;
    }

    private boolean isRaid(final String html) {
        final String title = this.getHtmlTitle(html);
        return StringUtils.equals(title, "天クロ｜大乱闘 | ボス対戦");
    }

    private boolean isRaidResult(final String html) {
        final String title = this.getHtmlTitle(html);
        return StringUtils.equals(title, "天クロ｜大乱闘 | 討伐結果");
    }

    private void raidAnimation() {
        final Map<String, Object> session = this.robot.getSession();
        final String deckId = (String) session.get("deckId");
        final String raidBattleId = (String) session.get("raidBattleId");
        final String isFullPower = (String) session.get("isFullPower");
        final String isSpecialAttack = (String) session.get("isSpecialAttack");
        final String useApSmall = (String) session.get("useApSmall");
        final String useApFull = (String) session.get("useApFull");
        final String usePowerHalf = (String) session.get("usePowerHalf");
        final String usePowerFull = (String) session.get("usePowerFull");
        final String token = (String) session.get("token");

        final String path = String.format("/raid/raid-battle-animation?deckId=%s&isFullPower=%s&isSpecialAttack=%s&raidBattleId=%s&useApSmall=%s&useApFull=%s&usePowerHalf=%s&usePowerFull=%s&token=%s",
                                          deckId,
                                          isFullPower,
                                          isSpecialAttack,
                                          raidBattleId,
                                          useApSmall,
                                          useApFull,
                                          usePowerHalf,
                                          usePowerFull,
                                          token);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);

        final JSONObject raidResultData = this.resolveRaidResultData(html);
        if (raidResultData != null) {
            final JSONObject animation = raidResultData.optJSONObject("animation");
            final int damagePoint = animation.optInt("damagePoint");
            this.log.info(String.format("对BOSS造成 %d 的伤害。", damagePoint));
        }
    }

    private void raidBattleFever() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidBattleId = (String) session.get("raidBattleId");
        final String token = (String) session.get("token");
        final String path = "/raid/ajax/put-raid-battle-fever";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("raidBattleId", raidBattleId));
        nvps.add(new BasicNameValuePair("token", token));

        final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        this.resolveJsonToken(jsonResponse);

        if (this.log.isInfoEnabled()) {
            final JSONObject data = jsonResponse.optJSONObject("data");
            if (data != null) {
                final JSONObject feverUpDetailDto = data.optJSONObject("feverUpDetailDto");
                final int rate = feverUpDetailDto.optInt("rate");
                final String displayUpRate = feverUpDetailDto.optString("displayUpRate");
                this.log.info(String.format("3分間全員の攻撃力が%d%%(%s)アップ",
                                            rate,
                                            displayUpRate));
            }
        }
    }

    private void raidInvite() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidBattleId = (String) session.get("raidBattleId");
        final String token = (String) session.get("token");
        final String path = "/raid/ajax/put-raid-battle-invite";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("raidBattleId", raidBattleId));
        nvps.add(new BasicNameValuePair("isToNation", String.valueOf(true)));
        nvps.add(new BasicNameValuePair("token", token));

        final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        this.resolveJsonToken(jsonResponse);
        if (this.log.isInfoEnabled()) {
            final JSONObject data = jsonResponse.optJSONObject("data");
            if (data != null) {
                final String resultMessage = data.optString("resultMessage");
                this.log.info(resultMessage);
            }
        }
    }

    @Override
    protected JSONObject resolvePageParams(final String html) {
        final JSONObject pageParams = super.resolvePageParams(html);
        final JSONArray itemList = pageParams.optJSONArray("itemList");
        final Matcher matcher = RaidBattleHandler.ITEM_PATTERN.matcher(html);
        while (matcher.find()) {
            final String itemData = matcher.group(1);
            itemList.add(JSONObject.fromObject(itemData));
        }
        return pageParams;
    }

    private JSONObject resolveRaidResultData(final String html) {
        final Matcher matcher = RaidBattleHandler.RAID_RESULT_DATA_PATTERN.matcher(html);
        if (matcher.find()) {
            String text = matcher.group(1);
            text = StringEscapeUtils.unescapeJava(text);
            final JSONObject raidResultData = JSONObject.fromObject(text);
            return raidResultData;
        }
        return null;
    }
}
