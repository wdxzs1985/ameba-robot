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
import robot.tnk47.raid.model.RaidBattleDamageMap;
import robot.tnk47.raid.model.RaidModel;

public class RaidHandler extends Tnk47EventHandler {

    private static final Pattern RAID_AP_NUM_PATTERN = Pattern.compile("<span id=\"jsiRaidApNum\">(\\d)/5</span>");

    private final RaidBattleDamageMap damageMap;
    private final int minDamageRatio;

    public RaidHandler(final Tnk47Robot robot,
            final RaidBattleDamageMap damageMap) {
        super(robot);
        this.minDamageRatio = robot.getMinDamageRatio();
        this.damageMap = damageMap;
    }

    @Override
    public String handleIt() {
        final RaidModel model = new RaidModel();
        this.initBattleList(model);
        final JSONObject data = this.getRaidBattleList(model);
        if (data != null) {
            if (model.hasAp()) {
                final JSONArray raidBossEncountTileDtos = data.optJSONArray("raidBossEncountTileDtos");
                final JSONObject raidDto = this.findRaid(model,
                                                         raidBossEncountTileDtos);
                if (raidDto != null) {
                    return this.raidBattle(model, raidDto);
                }
            }
            if (!this.is("limited")) {
                final JSONArray raidBossTileDtos = data.optJSONArray("raidBossTileDtos");
                final JSONObject raidDto = this.findRaid(model,
                                                         raidBossTileDtos);
                if (raidDto != null) {
                    return this.raidBattle(model, raidDto);
                }
            }
            return "/raid/stage";
        }
        return "/mypage";
    }

    private void initBattleList(final RaidModel model) {
        final String path = "/raid/raid-battle-list";
        final String html = this.httpGet(path);
        this.resolveInputToken(html);
        final JSONObject pageParams = this.resolvePageParams(html);
        final String raidId = pageParams.optString("raidId");
        model.setRaidId(raidId);

        final int apNow = this.findApNum(html);
        model.setApNow(apNow);
    }

    private int findApNum(final String html) {
        final Matcher matcher = RaidHandler.RAID_AP_NUM_PATTERN.matcher(html);
        if (matcher.find()) {
            final String apNum = matcher.group(1);
            return Integer.valueOf(apNum);
        }
        return 0;
    }

    private JSONObject getRaidBattleList(final RaidModel model) {
        final String raidId = model.getRaidId();

        final Map<String, Object> session = this.robot.getSession();
        final String token = (String) session.get("token");

        final String path = "/raid/ajax/get-raid-battle-list";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("raidId", raidId));
        nvps.add(new BasicNameValuePair("token", token));
        final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        this.resolveJsonToken(jsonResponse);
        return jsonResponse.optJSONObject("data");
    }

    private String raidBattle(final RaidModel model, final JSONObject raidDto) {
        final Map<String, Object> session = this.robot.getSession();
        this.printRaidDto(raidDto);
        final String raidBattleId = raidDto.optString("raidBattleId");
        final long maxHp = raidDto.optLong("maxHp");
        final boolean endBattle = raidDto.optBoolean("endBattle");
        final JSONObject bossDto = raidDto.optJSONObject("raidBossDto");
        final int raidBossType = bossDto.optInt("raidBossType");
        final int raidBossRank = bossDto.optInt("raidBossRank");
        final long minDamage = maxHp * this.minDamageRatio / 100;
        session.put("raidBattleId", raidBattleId);
        session.put("raidBossType", raidBossType);
        session.put("raidBossRank", raidBossRank);
        session.put("maxHp", maxHp);
        session.put("minDamage", minDamage);
        this.damageMap.setMinDamage(raidBattleId, minDamage);
        if (endBattle) {
            return "/raid/battle-result";
        } else {
            return "/raid/battle";
        }
    }

    private JSONObject findRaid(final RaidModel model,
                                final JSONArray raidBossTileDtos) {
        int maxFever = 0;
        JSONObject selectedRaid = null;
        for (int i = 0; i < raidBossTileDtos.size(); i++) {
            final JSONObject raidDto = raidBossTileDtos.optJSONObject(i);
            final String raidBattleId = raidDto.optString("raidBattleId");
            final long maxHp = raidDto.optLong("maxHp");
            final boolean entry = raidDto.optBoolean("entry");
            final boolean endBattle = raidDto.optBoolean("endBattle");
            final int currentHp = raidDto.optInt("currentHp");
            final long minDamage = maxHp * this.minDamageRatio / 100;
            if (endBattle) {
                selectedRaid = raidDto;
                break;
            }
            if (!entry) {
                selectedRaid = raidDto;
                break;
            }

            final boolean hasAp = model.hasAp();
            final boolean isHpEnough = currentHp > minDamage;
            final boolean isDamageNotEnough = !this.damageMap.isDamageEnough(raidBattleId);
            if (hasAp && isHpEnough && isDamageNotEnough) {
                final int feverRate = raidDto.optInt("feverRate");
                if (selectedRaid == null || maxFever < feverRate) {
                    maxFever = feverRate;
                    selectedRaid = raidDto;
                }
            }
        }
        return selectedRaid;
    }

    private void printRaidDto(final JSONObject raidDto) {
        final JSONObject bossDto = raidDto.optJSONObject("raidBossDto");
        this.printBossDto(bossDto);
        // -----------------------
        final long currentHp = raidDto.optLong("currentHp");
        final long maxHp = raidDto.optLong("maxHp");
        final int raidBossHpPercent = raidDto.optInt("raidBossHpPercent");
        final long minDamage = maxHp * this.minDamageRatio / 100;

        this.log.info(String.format("HP:%d/%d (%d%%)",
                                    currentHp,
                                    maxHp,
                                    raidBossHpPercent));
        this.log.info(String.format("最小伤害:%d", minDamage));

        final int memberCount = raidDto.optInt("memberCount");
        final int maxMemberCount = raidDto.optInt("maxMemberCount");
        final int playerHpPercent = raidDto.optInt("playerHpPercent");

        this.log.info(String.format("MEMBER:%d/%d TIME:%d%%",
                                    memberCount,
                                    maxMemberCount,
                                    playerHpPercent));

        final boolean endBattle = raidDto.optBoolean("endBattle");
        final boolean entry = raidDto.optBoolean("entry");
        this.log.info(String.format("%s %s",
                                    endBattle ? "終了" : "進行中",
                                    entry ? "参加中" : "未参加"));

        final boolean fever = raidDto.optBoolean("fever");
        if (fever) {
            final int feverRate = raidDto.optInt("feverRate");
            final String feverRestTime = raidDto.optString("feverRestTime");
            this.log.info(String.format("%d%% FEVER %s",
                                        feverRate,
                                        feverRestTime));
        }
    }

    private void printBossDto(final JSONObject bossDto) {
        final String raidBossName = bossDto.optString("raidBossName");
        final int raidBossLevel = bossDto.optInt("raidBossLevel");
        final int raidBossRank = bossDto.optInt("raidBossRank");
        bossDto.optInt("raidBossType");
        if (this.log.isInfoEnabled()) {
            this.log.info(String.format("%s (Lv%d) %s",
                                        raidBossName,
                                        raidBossLevel,
                                        StringUtils.repeat("★", raidBossRank)));
        }
    }
}
