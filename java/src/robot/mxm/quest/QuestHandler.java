package robot.mxm.quest;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;
import robot.mxm.convert.MonsterConvert;

public class QuestHandler extends MxmEventHandler {

    private static final Pattern DAILY_ELEMENT_PATTERN = Pattern.compile("http://stat100.ameba.jp/mxm/ver01/page/img/orgn/daily_mission/icon_daily_element([\\d]).png");
    private static final Pattern DAILY_CLEAR_PATTERN = Pattern.compile("クリアまであと<span class=\"colorDeepOrange\">([1-5])回！</span>");
    private static final Pattern TABLE_DATA_PATTERN = Pattern.compile("mxm.tableData\\[\"(\\d)\"\\] = (\\{.*?\\})");
    private static final Pattern SUMMON_PATTERN = Pattern.compile("<li data-btn=\"push\" data-summon-point=\"(\\d)\" data-max-sumon-point=\"\\d\" data-iusermonster-id=\"\\d+\" data-index=\"\\d\" data-recipe-id=\"(\\d+)\" data-mst-name=\"(.*?)\" data-mst-path=\".*?\" data-rarity-id=\"(\\d)\" data-element-id=\"(\\d)\">");

    private static final Pattern HELP_PATTERN = Pattern.compile("/raid/(\\d+)/help/list");
    private static final Pattern RAID_PATTERN = Pattern.compile("/raid/(\\d+)/(\\d+)/top");

    private final boolean raidEnable;

    public QuestHandler(final MxmRobot robot) {
        super(robot);
        this.raidEnable = robot.isRaidEnable();
    }

    @Override
    public String handleIt() {
        final String html = this.httpGet("/mypage");
        this.resolveMxmToken(html);
        if (!this.hasMonsterType()) {
            return "/monster";
        }
        this.setDailyElement(html);
        this.summon(html);

        if (this.raidEnable && this.getBpCount(html) > 0) {
            if (this.isHelpComing(html)) {
                if (this.log.isInfoEnabled()) {
                    this.log.info("救援依頼が届いています!");
                }
                return "/raid/help/list";
            } else if (this.isRaid(html)) {
                return "/raid/top";
            }
        }
        return "/quest/user/list";
    }

    private boolean isRaid(final String html) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher matcher = QuestHandler.RAID_PATTERN.matcher(html);
        if (matcher.find()) {
            final String raidId = matcher.group(1);
            final String raidPirtyId = matcher.group(2);
            session.put("raidId", raidId);
            session.put("raidPirtyId", raidPirtyId);
            return true;
        }
        return false;
    }

    private boolean isHelpComing(final String html) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher matcher = QuestHandler.HELP_PATTERN.matcher(html);
        if (matcher.find()) {
            final String raidId = matcher.group(1);
            session.put("raidId", raidId);
            return true;
        }
        return false;
    }

    private void summon(final String html) {
        final Collection<String> positionList = this.findPosition(html);
        final LinkedList<JSONObject> summonList = this.findSummonList(html);
        for (final String position : positionList) {
            if (CollectionUtils.isEmpty(summonList)) {
                break;
            }
            final JSONObject summon = summonList.removeFirst();
            this.summon(position, summon);
            this.sleep();
        }
    }

    private void summon(final String position, final JSONObject summon) {
        final String recipeId = summon.optString("recipeId");
        final String name = summon.optString("name");

        final String path = "/summon/summon";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("monsterId", recipeId));
        nvps.add(new BasicNameValuePair("position", position));

        final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        this.resolveJsonToken(jsonResponse);
        if (this.log.isInfoEnabled()) {
            final JSONObject summonParameter = jsonResponse.optJSONObject("summonParameter");
            final int beforeRingExperience = summonParameter.optInt("beforeRingExperience");
            final int afterRingExperience = summonParameter.optInt("afterRingExperience");
            this.log.info(String.format("召唤 : %s", name));
            this.log.info(String.format("戒指的经验 : %d > %d",
                                        beforeRingExperience,
                                        afterRingExperience));
        }
    }

    private Collection<String> findPosition(final String html) {
        final Map<String, Boolean> positionMap = new HashMap<String, Boolean>();
        positionMap.put("1", true);
        positionMap.put("2", true);
        positionMap.put("3", true);
        positionMap.put("4", true);
        positionMap.put("5", true);

        final Matcher matcher = QuestHandler.TABLE_DATA_PATTERN.matcher(html);
        while (matcher.find()) {
            final String position = matcher.group(1);
            final String tableDataString = matcher.group(2);
            final JSONObject monster = JSONObject.fromObject(tableDataString);
            final String treasurePath = monster.optString("treasurePath");
            if (StringUtils.isBlank(treasurePath)) {
                positionMap.remove(position);
            } else {
                final String summonId = monster.optString("summonId");
                this.clearSummon(summonId);
                this.sleep();
            }
        }
        return positionMap.keySet();
    }

    private LinkedList<JSONObject> findSummonList(final String html) {
        final LinkedList<JSONObject> summonList = new LinkedList<JSONObject>();
        final Matcher matcher = QuestHandler.SUMMON_PATTERN.matcher(html);
        while (matcher.find()) {
            final int summonPoint = Integer.valueOf(matcher.group(1));
            final String recipeId = matcher.group(2);
            final String mstName = matcher.group(3);
            final String rarity = matcher.group(4);
            final String element = matcher.group(5);
            for (int i = 0; i < summonPoint; i++) {
                final JSONObject monster = new JSONObject();
                monster.put("recipeId", recipeId);
                monster.put("name", mstName);
                monster.put("rarity", rarity);
                monster.put("element", element);
                summonList.add(monster);
            }
        }

        Collections.sort(summonList, new Comparator<JSONObject>() {

            @Override
            public int compare(final JSONObject o1, final JSONObject o2) {
                final int rarity1 = o1.optInt("rarity");
                final int rarity2 = o2.optInt("rarity");
                return rarity2 - rarity1;
            }
        });
        return summonList;
    }

    private void clearSummon(final String summonId) {
        final String path = "/summon/clear";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("summonId", summonId));
        final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        this.resolveJsonToken(jsonResponse);

        if (this.log.isInfoEnabled()) {
            final JSONObject crystalParameter = jsonResponse.optJSONObject("crystalParameter");
            if (crystalParameter != null) {
                final int beforeCrystal = crystalParameter.optInt("beforeCrystal");
                final int afterCrystal = crystalParameter.optInt("afterCrystal");
                this.log.info(String.format("get crystal: %d > %d",
                                            beforeCrystal,
                                            afterCrystal));
            }
            final JSONObject getTreasureDto = jsonResponse.optJSONObject("getTreasureDto");
            if (getTreasureDto != null) {
                final JSONObject treasureDto = getTreasureDto.optJSONObject("treasureDto");
                if (treasureDto != null) {
                    final String name = treasureDto.optString("name");
                    this.log.info(String.format("get: %s", name));
                }
                final JSONObject userExplorerDto = getTreasureDto.optJSONObject("userExplorerDto");
                if (userExplorerDto != null) {
                    final int beforePoint = userExplorerDto.optInt("beforePoint");
                    final int currentPoint = userExplorerDto.optInt("currentPoint");
                    this.log.info(String.format("point: %d > %d",
                                                beforePoint,
                                                currentPoint));

                    final int beforeRank = userExplorerDto.optInt("beforeRank");
                    final int currentRank = userExplorerDto.optInt("currentRank");
                    this.log.info(String.format("rank: %d > %d",
                                                beforeRank,
                                                currentRank));
                }
            }
        }
    }

    private boolean hasMonsterType() {
        final Map<String, Object> session = this.robot.getSession();
        return session.containsKey("leaderType");
    }

    private void setDailyElement(final String html) {
        final Map<String, Object> session = this.robot.getSession();
        String element = null;
        if (this.isDailyClear(html)) {
            element = (String) session.get("leaderType");
        } else {
            element = this.findDailyElement(html);
        }
        session.put("element", element);
    }

    private boolean isDailyClear(final String html) {
        final Matcher matcher = QuestHandler.DAILY_CLEAR_PATTERN.matcher(html);
        if (matcher.find()) {
            final String times = matcher.group(1);
            if (this.log.isInfoEnabled()) {
                this.log.info(String.format("今日の召喚獣をクリアまであと%s回！", times));
            }
            return false;
        } else {
            if (this.log.isInfoEnabled()) {
                this.log.info("今日の召喚獣はクリア済みです！");
            }
            return true;
        }
    }

    private String findDailyElement(final String html) {
        String elementId = null;
        final Matcher matcher = QuestHandler.DAILY_ELEMENT_PATTERN.matcher(html);
        if (matcher.find()) {
            elementId = matcher.group(1);
            if (this.log.isInfoEnabled()) {
                final String elementName = MonsterConvert.convertElement(elementId);
                this.log.info(String.format("今日の召喚獣は%sです。", elementName));
            }
        }
        return elementId;
    }
}
