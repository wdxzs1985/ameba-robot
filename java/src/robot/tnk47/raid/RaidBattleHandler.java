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

    private final RaidDamageMap raidDamageMap;
    private final boolean useSpecialAttack;

    public RaidBattleHandler(final Tnk47Robot robot, RaidDamageMap raidDamageMap) {
        super(robot);
        this.raidDamageMap = raidDamageMap;
        this.useSpecialAttack = robot.getUseSpecialAttack();
    }

    @Override
    public String handleIt() {
        Map<String, Object> session = this.robot.getSession();
        String raidBattleId = (String) session.get("raidBattleId");
        String token = (String) session.get("token");
        final String path = String.format("/raid/raid-battle?raidBattleId=%s&token=%s",
                                          raidBattleId,
                                          token);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);

        if (this.isRaid(html)) {
            if (HELP_PATTERN.matcher(html).find()) {
                this.sleep();
                this.raidInvite();
            }
            if (FEVER_PATTERN.matcher(html).find()) {
                this.sleep();
                this.raidBattleFever();
            }
            this.sleep();

            JSONObject pageParams = this.resolvePageParams(html);
            String deckId = pageParams.optString("selectedDeckId");
            int apCost = pageParams.optInt("apCost");
            int apNow = pageParams.optInt("apNow");
            boolean isFirst = apCost == 0;

            boolean isFullPower = false;
            boolean isSpecialAttack = false;
            RaidDamageBean raidDamageBean = this.raidDamageMap.get(raidBattleId);
            int fisrtDamage = raidDamageBean.getFirstDamage();
            int minDamage = raidDamageBean.getMinDamage();
            int totalDamage = raidDamageBean.getTotalDamage();
            int delta = minDamage - totalDamage;
            this.log.info(String.format("minDamage: %d/totalDamage: %d",
                                        minDamage,
                                        totalDamage));

            boolean canAttack = delta > 0;
            if (canAttack) {
                int useApSmall = 0;
                int useApFull = 0;
                int usePowerHalf = 0;
                int usePowerFull = 0;
                session.put("deckId", deckId);
                session.put("isFullPower", String.valueOf(isFullPower));
                session.put("isSpecialAttack", String.valueOf(isSpecialAttack));
                session.put("useApSmall", String.valueOf(useApSmall));
                session.put("useApFull", String.valueOf(useApFull));
                session.put("usePowerHalf", String.valueOf(usePowerHalf));
                session.put("usePowerFull", String.valueOf(usePowerFull));
                this.raidAnimation(isFirst);
                return "/raid/battle";
            }
        } else if (this.isRaidResult(html)) {
            return "/raid/battle-result";
        }
        return "/raid";
    }

    private boolean isRaid(String html) {
        String title = this.getHtmlTitle(html);
        return StringUtils.equals(title, "天クロ｜大乱闘 | ボス対戦");
    }

    private boolean isRaidResult(String html) {
        String title = this.getHtmlTitle(html);
        return StringUtils.equals(title, "天クロ｜大乱闘 | 討伐結果");
    }

    private void raidAnimation(boolean isFirst) {
        Map<String, Object> session = this.robot.getSession();
        String deckId = (String) session.get("deckId");
        String raidBattleId = (String) session.get("raidBattleId");
        String isFullPower = (String) session.get("isFullPower");
        String isSpecialAttack = (String) session.get("isSpecialAttack");
        String useApSmall = (String) session.get("useApSmall");
        String useApFull = (String) session.get("useApFull");
        String usePowerHalf = (String) session.get("usePowerHalf");
        String usePowerFull = (String) session.get("usePowerFull");
        String token = (String) session.get("token");

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

        JSONObject raidResultData = this.resolveRaidResultData(html);
        if (raidResultData != null) {
            JSONObject animation = raidResultData.optJSONObject("animation");
            int damagePoint = animation.optInt("damagePoint");
            RaidDamageBean raidDamageBean = this.raidDamageMap.get(raidBattleId);
            if (isFirst) {
                raidDamageBean.setFirstDamage(damagePoint);
            }
            int totalDamage = raidDamageBean.getTotalDamage();
            totalDamage += damagePoint;
            raidDamageBean.setTotalDamage(totalDamage);
        }
    }

    private void raidBattleFever() {
        Map<String, Object> session = this.robot.getSession();
        String raidBattleId = (String) session.get("raidBattleId");
        String token = (String) session.get("token");
        String path = "/raid/ajax/put-raid-battle-fever";
        List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("raidBattleId", raidBattleId));
        nvps.add(new BasicNameValuePair("token", token));

        JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        this.resolveJsonToken(jsonResponse);

        if (this.log.isInfoEnabled()) {
            JSONObject data = jsonResponse.optJSONObject("data");
            JSONObject feverUpDetailDto = data.optJSONObject("feverUpDetailDto");
            int rate = feverUpDetailDto.optInt("rate");
            String displayUpRate = feverUpDetailDto.optString("displayUpRate");
            this.log.info(String.format("3分間全員の攻撃力が%d%%(%s)アップ",
                                        rate,
                                        displayUpRate));
        }
    }

    private void raidInvite() {
        Map<String, Object> session = this.robot.getSession();
        String raidBattleId = (String) session.get("raidBattleId");
        String token = (String) session.get("token");
        String path = "/raid/ajax/put-raid-battle-invite";
        List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("raidBattleId", raidBattleId));
        nvps.add(new BasicNameValuePair("isToNation", String.valueOf(true)));
        nvps.add(new BasicNameValuePair("token", token));

        JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        this.resolveJsonToken(jsonResponse);
        if (this.log.isInfoEnabled()) {
            JSONObject data = jsonResponse.optJSONObject("data");
            int sendCount = data.optInt("sendCount");
            String resultMessage = data.optString("resultMessage");
            this.log.info(String.format("%d名の%s", sendCount, resultMessage));
        }
    }

    @Override
    protected JSONObject resolvePageParams(String html) {
        JSONObject pageParams = super.resolvePageParams(html);
        JSONArray itemList = pageParams.optJSONArray("itemList");
        Matcher matcher = ITEM_PATTERN.matcher(html);
        while (matcher.find()) {
            String itemData = matcher.group(1);
            itemList.add(JSONObject.fromObject(itemData));
        }
        return pageParams;
    }

    private JSONObject resolveRaidResultData(String html) {
        final Matcher matcher = RAID_RESULT_DATA_PATTERN.matcher(html);
        if (matcher.find()) {
            String text = matcher.group(1);
            text = StringEscapeUtils.unescapeJava(text);
            final JSONObject raidResultData = JSONObject.fromObject(text);
            return raidResultData;
        }
        return null;
    }
}
