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
        final int apNow = (Integer) session.get("apNow");

        final String path = "/raid/ajax/get-raid-battle-list";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("raidId", raidId));
        nvps.add(new BasicNameValuePair("token", token));
        final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        this.resolveJsonToken(jsonResponse);
        final JSONObject data = jsonResponse.optJSONObject("data");
        if (data != null) {
            final JSONArray raidBossTileDtos = data.optJSONArray("raidBossTileDtos");
            JSONObject raidDto = this.findRaid(raidBossTileDtos);
            if (raidDto != null) {
                this.printRaidDto(raidDto);
                final String raidBattleId = raidDto.optString("raidBattleId");
                final boolean endBattle = raidDto.optBoolean("endBattle");
                session.put("raidBattleId", raidBattleId);
                session.put("currentHp", 0);
                session.put("invite", true);
                if (endBattle) {
                    return "/raid/battle-result";
                } else {
                    return "/raid/battle";
                }
            }

            if (apNow > 0) {
                final JSONArray raidBossEncountTileDtos = data.optJSONArray("raidBossEncountTileDtos");
                if (raidBossEncountTileDtos.size() > 0) {
                    raidDto = raidBossEncountTileDtos.optJSONObject(0);
                    this.printRaidDto(raidDto);
                    final String raidBattleId = raidDto.optString("raidBattleId");
                    final int currentHp = raidDto.optInt("currentHp");
                    session.put("raidBattleId", raidBattleId);
                    session.put("currentHp", currentHp);
                    session.put("invite", false);
                    return "/raid/battle";
                }
            }
            return "/raid/stage";
        }
        return "/mypage";
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

        this.log.info(String.format("HP:%3d/%3d (%2d%%)",
                                    currentHp,
                                    maxHp,
                                    raidBossHpPercent));

        final int memberCount = raidDto.optInt("memberCount");
        final int maxMemberCount = raidDto.optInt("maxMemberCount");
        final int playerHpPercent = raidDto.optInt("playerHpPercent");

        this.log.info(String.format("MEMBER:%2d/%2d TIME:%2d%%",
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
        this.log.info(String.format("%s:(Lv:%2d) %s",
                                    raidBossName,
                                    raidBossLevel,
                                    StringUtils.repeat("★", raidBossRank)));
    }
}
