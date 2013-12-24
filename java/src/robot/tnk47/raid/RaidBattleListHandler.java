package robot.tnk47.raid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class RaidBattleListHandler extends Tnk47EventHandler {

    private final RaidDamageMap raidDamageMap;

    public RaidBattleListHandler(final Tnk47Robot robot, RaidDamageMap raidDamageMap) {
        super(robot);
        this.raidDamageMap = raidDamageMap;
    }

    @Override
    public String handleIt() {
        Map<String, Object> session = this.robot.getSession();
        String raidId = (String) session.get("raidId");
        String token = (String) session.get("token");

        final String path = "/raid/ajax/get-raid-battle-list";
        List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("raidId", raidId));
        nvps.add(new BasicNameValuePair("token", token));
        JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        this.resolveJsonToken(jsonResponse);
        JSONObject data = jsonResponse.optJSONObject("data");
        if (data != null) {
            // JSONArray raidBossEncountTileDtos = data
            // .optJSONArray("raidBossEncountTileDtos");
            JSONArray raidBossTileDtos = data.optJSONArray("raidBossTileDtos");
            this.raidDamageMap.refresh(raidBossTileDtos);
            JSONObject raidDto = this.findRaid(raidBossTileDtos);
            if (raidDto != null) {
                String raidBattleId = raidDto.optString("raidBattleId");
                session.put("raidBattleId", raidBattleId);
                this.printRaidDto(raidDto);
                return "/raid/battle";
            }
            return "/raid/stage";
        }
        return "/mypage";
    }

    private JSONObject findRaid(JSONArray raidBossTileDtos) {
        JSONObject endRaid = this.findEndBattleRaid(raidBossTileDtos);
        if (endRaid != null) {
            return endRaid;
        }
        List<JSONObject> raidList = this.filterDamageEnough(raidBossTileDtos);
        JSONObject noEntryRaid = this.findNotEntryRaid(raidList);
        if (noEntryRaid != null) {
            return noEntryRaid;
        }
        JSONObject maxFeverRaid = this.findMaxFeverRaid(raidList);
        if (maxFeverRaid != null) {
            return maxFeverRaid;
        }
        JSONObject maxMemberRaid = this.findMaxMemberRaid(raidList);
        if (maxMemberRaid != null) {
            return maxMemberRaid;
        }
        return null;
    }

    private List<JSONObject> filterDamageEnough(JSONArray raidBossTileDtos) {
        List<JSONObject> newList = new ArrayList<JSONObject>();
        for (int i = 0; i < raidBossTileDtos.size(); i++) {
            JSONObject raidDto = raidBossTileDtos.optJSONObject(i);
            String raidBattleId = raidDto.optString("raidBattleId");
            RaidDamageBean raidDamageBean = this.raidDamageMap.get(raidBattleId);
            int minDamage = raidDamageBean.getMinDamage();
            int totalDamage = raidDamageBean.getTotalDamage();
            int delta = minDamage - totalDamage;
            if (delta > 0) {
                newList.add(raidDto);
            }
        }
        return newList;
    }

    private JSONObject findMaxMemberRaid(List<JSONObject> raidCollection) {
        int maxMember = 0;
        JSONObject raid = null;
        for (JSONObject raidDto : raidCollection) {
            int memberCount = raidDto.optInt("memberCount");
            if (memberCount > maxMember) {
                raid = raidDto;
            }
        }
        return raid;
    }

    private JSONObject findEndBattleRaid(JSONArray raidBossTileDtos) {
        for (int i = 0; i < raidBossTileDtos.size(); i++) {
            JSONObject raidDto = raidBossTileDtos.optJSONObject(i);
            boolean endBattle = raidDto.optBoolean("endBattle");
            if (endBattle) {
                return raidDto;
            }
        }
        return null;
    }

    private JSONObject findNotEntryRaid(List<JSONObject> raidCollection) {
        for (JSONObject raidDto : raidCollection) {
            boolean entry = raidDto.optBoolean("entry");
            if (!entry) {
                return raidDto;
            }
        }
        return null;
    }

    private JSONObject findMaxFeverRaid(List<JSONObject> raidCollection) {
        int maxFever = 5;
        JSONObject raid = null;
        for (JSONObject raidDto : raidCollection) {
            int feverRate = raidDto.optInt("feverRate");
            if (feverRate > maxFever) {
                raid = raidDto;
            }
        }
        return raid;
    }

    private void printRaidDto(JSONObject raidDto) {
        JSONObject bossDto = raidDto.optJSONObject("raidBossDto");
        this.printBossDto(bossDto);
        // -----------------------
        int currentHp = raidDto.optInt("currentHp");
        int maxHp = raidDto.optInt("maxHp");
        int raidBossHpPercent = raidDto.optInt("raidBossHpPercent");

        this.log.info(String.format("HP:%3d/%3d (%2d%%)",
                                    currentHp,
                                    maxHp,
                                    raidBossHpPercent));

        int memberCount = raidDto.optInt("memberCount");
        int maxMemberCount = raidDto.optInt("maxMemberCount");
        int playerHpPercent = raidDto.optInt("playerHpPercent");

        this.log.info(String.format("MEMBER:%2d/%2d TIME:%2d%%",
                                    memberCount,
                                    maxMemberCount,
                                    playerHpPercent));

        boolean endBattle = raidDto.optBoolean("endBattle");
        boolean entry = raidDto.optBoolean("entry");
        this.log.info(String.format("%s %s",
                                    endBattle ? "終了" : "進行中",
                                    entry ? "参加中" : "未参加"));

        boolean fever = raidDto.optBoolean("fever");
        if (fever) {
            int feverRate = raidDto.optInt("feverRate");
            String feverRestTime = raidDto.optString("feverRestTime");
            this.log.info(String.format("%d%% FEVER %s",
                                        feverRate,
                                        feverRestTime));
        }
    }

    private void printBossDto(JSONObject bossDto) {
        String raidBossName = bossDto.optString("raidBossName");
        int raidBossLevel = bossDto.optInt("raidBossLevel");
        int raidBossRank = bossDto.optInt("raidBossRank");
        int attackPointRate = bossDto.optInt("attackPointRate");
        int raidBasePoint = bossDto.optInt("raidBasePoint");
        this.log.info(String.format("%s:(Lv:%2d/強さ:%2d)",
                                    raidBossName,
                                    raidBossLevel,
                                    raidBossRank));
        this.log.info(String.format("%s:%d", "attackPointRate", attackPointRate));
        this.log.info(String.format("%s:%d", "raidBasePoint", raidBasePoint));
    }
}
