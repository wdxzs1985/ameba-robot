package robot.mxm.raid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import robot.mxm.MxmRobot;

public class RaidEncountHandler extends AbstractRaidHandler {

    private static final Pattern MONSTER_PATTERN = Pattern.compile("var _monsterData = (\\[.*\\]);");
    private static final Pattern TARGET_PATTERN = Pattern.compile("/raid/\\d+/\\d+/target/(\\d+)/choice");

    public RaidEncountHandler(final MxmRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidId = (String) session.get("raidId");
        final String raidPirtyId = (String) session.get("raidPirtyId");
        final String path = String.format("/raid/%s/%s/encount",
                                          raidId,
                                          raidPirtyId);
        final String html = this.httpGet(path);

        if (this.isRaidWin(html)) {
            return "/raid/win/result";
        }

        final JSONObject monster = this.getMonsterData(html);
        if (monster != null) {
            final String targetMonsterCategoryId = this.chooseTarget(monster);
            if (StringUtils.isNotBlank(targetMonsterCategoryId)) {
                session.put("targetMonsterCategoryId", targetMonsterCategoryId);
                return "/raid/target";
            }
        }

        return "/mypage";
    }

    private JSONObject getMonsterData(final String html) {
        final List<JSONObject> monsterList = new ArrayList<JSONObject>();
        final Matcher matcher = RaidEncountHandler.MONSTER_PATTERN.matcher(html);
        if (matcher.find()) {
            final String jsonString = matcher.group(1);
            final JSONArray monsterData = JSONArray.fromObject(jsonString);
            for (int i = 0; i < monsterData.size(); i++) {
                final JSONObject monster = monsterData.optJSONObject(i);
                final String name = monster.optString("name");
                final int lv = monster.optInt("lv");
                final int HP = monster.optInt("HP");
                final int maxHP = monster.optInt("maxHP");
                if (HP > 0) {
                    monsterList.add(monster);
                    if (this.log.isInfoEnabled()) {
                        if (monster.optBoolean("boss")) {
                            this.log.info(String.format("[Boss] %s(Lv%d) %d/%d",
                                                        name,
                                                        lv,
                                                        HP,
                                                        maxHP));
                        } else {
                            this.log.info(String.format("%s(Lv%d) %d/%d",
                                                        name,
                                                        lv,
                                                        HP,
                                                        maxHP));
                        }
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(monsterList)) {
            Collections.sort(monsterList, new Comparator<JSONObject>() {

                @Override
                public int compare(final JSONObject monster1,
                                   final JSONObject monster2) {
                    final int HP1 = monster1.optInt("HP");
                    final int HP2 = monster2.optInt("HP");

                    return HP2 - HP1;
                }
            });

            return monsterList.get(0);
        }
        return null;
    }

    private String chooseTarget(final JSONObject monster) {
        final String url = monster.optString("url");
        final Matcher matcher = RaidEncountHandler.TARGET_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
