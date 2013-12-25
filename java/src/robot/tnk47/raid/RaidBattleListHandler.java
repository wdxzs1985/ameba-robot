package robot.tnk47.raid;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class RaidBattleListHandler extends Tnk47EventHandler {

    public RaidBattleListHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidId = (String) session.get("raidId");
        final String token = (String) session.get("token");

        final String path = "/raid/ajax/get-raid-battle-list";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("raidId", raidId));
        nvps.add(new BasicNameValuePair("token", token));
        final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        this.resolveJsonToken(jsonResponse);
        final JSONObject data = jsonResponse.optJSONObject("data");
        if (data != null) {
            final int apNow = (Integer) session.get("apNow");
            if (apNow > 0) {
                final JSONArray raidBossEncountTileDtos = data.optJSONArray("raidBossEncountTileDtos");
                for (int i = 0; i < raidBossEncountTileDtos.size(); i++) {
                    final JSONObject raidDto = raidBossEncountTileDtos.optJSONObject(i);
                    final JSONObject bossDto = raidDto.optJSONObject("raidBossDto");
                    final int raidBossRank = bossDto.optInt("raidBossRank");
                    final boolean endBattle = raidDto.optBoolean("endBattle");
                    if (raidBossRank <= 5 || endBattle) {
                        return this.raidBattle(raidDto, true);
                    }
                }
            }
            final JSONArray raidBossTileDtos = data.optJSONArray("raidBossTileDtos");
            final JSONObject raidDto = this.findRaid(raidBossTileDtos);
            if (raidDto != null) {
                return this.raidBattle(raidDto, false);
            }
            return "/raid/stage";
        }
        return "/mypage";
    }

    private String raidBattle(final JSONObject raidDto, final boolean isMine) {
        final Map<String, Object> session = this.robot.getSession();
        this.printRaidDto(raidDto);
        final String raidBattleId = raidDto.optString("raidBattleId");
        final int maxHp = raidDto.optInt("maxHp");
        final boolean endBattle = raidDto.optBoolean("endBattle");
        final JSONObject bossDto = raidDto.optJSONObject("raidBossDto");
        final int raidBossType = bossDto.optInt("raidBossType");
        session.put("raidBattleId", raidBattleId);
        session.put("raidBossType", raidBossType);
        session.put("maxHp", maxHp);
        session.put("isMine", isMine);
        if (endBattle) {
            return "/raid/battle-result";
        } else {
            return "/raid/battle";
        }
    }

    private JSONObject findRaid(final JSONArray raidBossTileDtos) {
        final JSONObject endRaid = this.findEndBattleRaid(raidBossTileDtos);
        if (endRaid != null) {
            return endRaid;
        }
        final JSONObject noEntryRaid = this.findNotEntryRaid(raidBossTileDtos);
        if (noEntryRaid != null) {
            return noEntryRaid;
        }
        return null;
    }

    private JSONObject findEndBattleRaid(final JSONArray raidBossTileDtos) {
        for (int i = 0; i < raidBossTileDtos.size(); i++) {
            final JSONObject raidDto = raidBossTileDtos.optJSONObject(i);
            final boolean endBattle = raidDto.optBoolean("endBattle");
            if (endBattle) {
                return raidDto;
            }
        }
        return null;
    }

    private JSONObject findNotEntryRaid(final JSONArray raidBossTileDtos) {
        for (int i = 0; i < raidBossTileDtos.size(); i++) {
            final JSONObject raidDto = raidBossTileDtos.optJSONObject(i);
            final boolean entry = raidDto.optBoolean("entry");
            if (!entry) {
                return raidDto;
            }
        }
        return null;
    }

    private void printRaidDto(final JSONObject raidDto) {
        final JSONObject bossDto = raidDto.optJSONObject("raidBossDto");
        this.printBossDto(bossDto);
        // -----------------------
        final int currentHp = raidDto.optInt("currentHp");
        final int maxHp = raidDto.optInt("maxHp");
        final int raidBossHpPercent = raidDto.optInt("raidBossHpPercent");

        this.log.info(String.format("HP:%d/%d (%d%%)",
                                    currentHp,
                                    maxHp,
                                    raidBossHpPercent));

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
        final int raidBossType = bossDto.optInt("raidBossType");
        final int battleServiceMinPoint = bossDto.optInt("battleServiceMinPoint");
        if (this.log.isInfoEnabled()) {
            switch (raidBossType) {
            case 2:
                this.log.info(String.format("【%s】 %s (Lv%d) %s",
                                            "大",
                                            raidBossName,
                                            raidBossLevel,
                                            StringUtils.repeat("★",
                                                               raidBossRank)));
                break;
            case 1:
                this.log.info(String.format("【%s】 %s (Lv%d) %s",
                                            "疾风",
                                            raidBossName,
                                            raidBossLevel,
                                            StringUtils.repeat("★",
                                                               raidBossRank)));
                break;
            default:
                this.log.info(String.format("%s (Lv%d) %s",
                                            raidBossName,
                                            raidBossLevel,
                                            StringUtils.repeat("★",
                                                               raidBossRank)));
                break;
            }
            this.log.info(String.format("MIN DAMAGE: %d", battleServiceMinPoint));
        }
    }
}
