package robot.mxm;

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

import robot.mxm.convert.MonsterConvert;

public class MypageHandler extends MxmEventHandler {

    private static final Pattern HTML_USER_NAME_PATTERN = Pattern.compile("<div class=\"fsLarge marginRight10\">(.*?)</div>");

    private static final Pattern HELP_PATTERN = Pattern.compile("/raid/(\\d+)/help/list");
    private static final Pattern RAID_PATTERN = Pattern.compile("/raid/(\\d+)/(\\d+)/top");

    private static final Pattern DAILY_ELEMENT_PATTERN = Pattern.compile("http://stat100.ameba.jp/mxm/ver01/page/img/orgn/daily_mission/icon_daily_element([\\d]).png");
    private static final Pattern DAILY_RARE_PATTERN = Pattern.compile("http://stat100.ameba.jp/mxm/ver01/page/img/orgn/daily_mission/icon_daily_rare([\\d]).png");
    private static final Pattern DAILY_CLEAR_PATTERN = Pattern.compile("クリアまであと<span class=\"colorDeepOrange\">([1-5])回！</span>");
    private static final Pattern TABLE_DATA_PATTERN = Pattern.compile("mxm.tableData\\[\"(\\d)\"\\] = (\\{.*?\\})");
    private static final Pattern SUMMON_PATTERN = Pattern.compile("<li data-btn=\"push\" data-summon-point=\"(\\d)\" data-max-sumon-point=\"\\d\" data-iusermonster-id=\"\\d+\" data-index=\"\\d\" data-recipe-id=\"(\\d+)\" data-mst-name=\"(.*?)\" data-mst-path=\".*?\" data-rarity-id=\"(\\d)\" data-element-id=\"(\\d)\">");
    private static final Pattern STAMINA_PATTERN = Pattern.compile("<li>元気: <span class=\"colorWhite\">(\\d{1,2})</span>/(\\d{1,2})</li>");

    private final boolean raidEnable;
    private final boolean usePotion;

    public MypageHandler(final MxmRobot robot) {
        super(robot);
        this.raidEnable = robot.isRaidEnable();
        this.usePotion = robot.isUsePotion();
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String html = this.httpGet("/mypage");
        if (!this.is("isMypage")) {
            final Matcher userNameMatcher = MypageHandler.HTML_USER_NAME_PATTERN.matcher(html);
            if (userNameMatcher.find()) {
                final String userName = userNameMatcher.group(1);
                this.log.info(String.format("角色： %s", userName));
                session.put("isMypage", true);
            } else {
                String title = this.getHtmlTitle(html);
                if (this.log.isInfoEnabled()) {
                    this.log.info(title);
                }
                if (StringUtils.contains(title, "メンテナンスのお知らせ")) {
                    return "/exit";
                }
                return "/mypage";
            }
        }
        this.resolveMxmToken(html);

        if (!this.hasMonsterType()) {
            return "/monster";
        }
        this.setDailyElement(html);
        this.summon(html);

        if (this.is("isRaidHistoryEnable")) {
            session.put("isRaidHistoryEnable", false);
            return "/raid/history";
        }

        if (this.raidEnable) {
            if (this.isRaiding(html)) {
                if (this.getBpCount(html) > 0) {
                    return "/raid/top";
                }
            } else if (this.isHelpComing(html)) {
                if (this.log.isInfoEnabled()) {
                    this.log.info("收到小伙伴的救援请求！");
                }
                return "/raid/help/list";
            }
        }

        if (this.isStaminaOut(html)) {
            session.put("isQuestEnable", false);
            if (this.usePotion) {
                session.put("potionId", "1");
                return "/item/potion";
            }
        } else if (this.is("isQuestEnable")) {
            return "/quest";
        }
        return "/exit";
    }

    private boolean isStaminaOut(String html) {
        final Matcher matcher = MypageHandler.STAMINA_PATTERN.matcher(html);
        if (matcher.find()) {
            final int stamina = Integer.valueOf(matcher.group(1));
            return stamina == 0;
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

        final Matcher matcher = MypageHandler.TABLE_DATA_PATTERN.matcher(html);
        while (matcher.find()) {
            final String position = matcher.group(1);
            final String tableDataString = matcher.group(2);
            final JSONObject monster = JSONObject.fromObject(tableDataString);
            final String stateId = monster.optString("stateId");
            if (StringUtils.equals(stateId, "1")) {
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
        final Matcher matcher = MypageHandler.SUMMON_PATTERN.matcher(html);
        while (matcher.find()) {
            final int summonPoint = Integer.valueOf(matcher.group(1));
            final String recipeId = matcher.group(2);
            final String mstName = matcher.group(3);
            final String rarity = matcher.group(4);
            final String element = matcher.group(5);
            for (int i = 1; i < summonPoint; i++) {
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
                this.log.info(String.format("收获水晶: %d > %d",
                                            beforeCrystal,
                                            afterCrystal));
            }
            final JSONObject getTreasureDto = jsonResponse.optJSONObject("getTreasureDto");
            if (getTreasureDto != null) {
                final JSONObject treasureDto = getTreasureDto.optJSONObject("treasureDto");
                if (treasureDto != null) {
                    final String name = treasureDto.optString("name");
                    this.log.info(String.format("获得: %s", name));
                }
                final JSONObject userExplorerDto = getTreasureDto.optJSONObject("userExplorerDto");
                if (userExplorerDto != null) {
                    final int beforePoint = userExplorerDto.optInt("beforePoint");
                    final int currentPoint = userExplorerDto.optInt("currentPoint");
                    this.log.info(String.format("积分: %d > %d",
                                                beforePoint,
                                                currentPoint));

                    final int beforeRank = userExplorerDto.optInt("beforeRank");
                    final int currentRank = userExplorerDto.optInt("currentRank");
                    this.log.info(String.format("排名: %d > %d",
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
        final Matcher matcher = MypageHandler.DAILY_CLEAR_PATTERN.matcher(html);
        if (matcher.find()) {
            final String times = matcher.group(1);
            if (this.log.isInfoEnabled()) {
                this.log.info(String.format("离今天的召唤兽完成还剩%s回！", times));
            }
            return false;
        } else {
            if (this.log.isInfoEnabled()) {
                this.log.info("今天的召唤兽已经完成了！");
            }
            return true;
        }
    }

    private String findDailyElement(final String html) {
        String element = null;
        final Matcher elementMatcher = MypageHandler.DAILY_ELEMENT_PATTERN.matcher(html);
        if (elementMatcher.find()) {
            element = elementMatcher.group(1);
            if (this.log.isInfoEnabled()) {
                final String elementName = MonsterConvert.convertElement(element);
                this.log.info(String.format("今天的召唤兽是%s系。", elementName));
            }
        }
        final Matcher rareMatcher = MypageHandler.DAILY_RARE_PATTERN.matcher(html);
        if (rareMatcher.find()) {
            element = "0";
            final String rareId = rareMatcher.group(1);
            if (this.log.isInfoEnabled()) {
                final String rarityName = MonsterConvert.convertRarity(rareId);
                this.log.info(String.format("今天的召唤兽是%s。", rarityName));
            }
        }
        return element;
    }

    private boolean isRaiding(final String html) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher matcher = MypageHandler.RAID_PATTERN.matcher(html);
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
        final Matcher matcher = MypageHandler.HELP_PATTERN.matcher(html);
        if (matcher.find()) {
            final String raidId = matcher.group(1);
            session.put("raidId", raidId);
            return true;
        }
        return false;
    }
}
