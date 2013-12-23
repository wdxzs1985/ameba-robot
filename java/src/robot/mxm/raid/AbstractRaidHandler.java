package robot.mxm.raid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public abstract class AbstractRaidHandler extends MxmEventHandler {

    private static final Pattern WIN_PATTERN = Pattern.compile("<title>レイド 勝利シーン \\| フレンダリアと魔法の指輪</title>");
    private static final Pattern LOSTPATTERN = Pattern.compile("<title>レイド 敗北シーン \\| フレンダリアと魔法の指輪</title>");

    // レイド 敗北シーン | フレンダリアと魔法の指輪

    private static final Pattern MONSTER_DATA_PATTERN = Pattern.compile("var _monsterData = (\\[.*\\]);");
    private static final Pattern TARGET_PATTERN = Pattern.compile("/raid/\\d+/\\d+/target/(\\d+)/choice");

    private static final Pattern RESULT_PATTERN = Pattern.compile("var _json = (.*?);?_json");

    public AbstractRaidHandler(final MxmRobot robot) {
        super(robot);
    }

    public boolean isRaidWin(final String html) {
        final Matcher matcher = AbstractRaidHandler.WIN_PATTERN.matcher(html);
        if (matcher.find()) {
            if (this.log.isInfoEnabled()) {
                this.printRaidPrizes(html);
            }
            return true;
        }
        return false;
    }

    public boolean isRaidLose(final String html) {
        final Matcher matcher = AbstractRaidHandler.LOSTPATTERN.matcher(html);
        if (matcher.find()) {
            if (this.log.isInfoEnabled()) {
                this.log.info("讨伐失败");
            }
            return true;
        }
        return false;
    }

    private void printRaidPrizes(final String html) {
        final Matcher matcher = AbstractRaidHandler.RESULT_PATTERN.matcher(html);
        if (matcher.find()) {
            final String jsonString = matcher.group(1);
            final JSONObject data = JSONObject.fromObject(jsonString);
            final JSONArray prizes = data.optJSONArray("prizes");
            if (prizes != null) {
                for (int i = 0; i < prizes.size(); i++) {
                    final JSONObject prize = prizes.optJSONObject(i);
                    this.printPrizeInfo(prize);
                }
            }
            final JSONObject medalPrize = data.optJSONObject("medalPrize");
            if (medalPrize != null) {
                this.printPrizeInfo(medalPrize);
            }
        }
    }

    private void printPrizeInfo(final JSONObject prize) {
        final String name = prize.optString("name");
        final int amount = prize.optInt("amount");
        this.log.info(String.format("获得 %s %d 个", name, amount));
    }

    protected JSONObject findAttackMonster(final String html) {
        final List<JSONObject> monsterList = new ArrayList<JSONObject>();
        final Matcher matcher = AbstractRaidHandler.MONSTER_DATA_PATTERN.matcher(html);
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
                            this.log.info(String.format("[带头大哥] %s(Lv%d) %d/%d",
                                                        name,
                                                        lv,
                                                        HP,
                                                        maxHP));
                        } else {
                            this.log.info(String.format("[跟班小弟] %s(Lv%d) %d/%d",
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

    protected String chooseTarget(final JSONObject monster) {
        final String url = monster.optString("url");
        final Matcher matcher = AbstractRaidHandler.TARGET_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
