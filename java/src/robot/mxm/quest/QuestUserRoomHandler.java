package robot.mxm.quest;

import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;
import robot.mxm.convert.MonsterConvert;

public class QuestUserRoomHandler extends MxmEventHandler {

    private static final Pattern USER_ROOM_DATA_PATTERN = Pattern.compile("new mxm.UserRoom\\((.*?)\\);");

    public QuestUserRoomHandler(final MxmRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String userId = (String) session.get("userId");
        final String element = (String) session.get("element");
        final String path = String.format("/user/%s/room", userId);
        final String html = this.httpGet(path);
        this.resolveMxmToken(html);
        final JSONObject userRoom = this.resloveUserRoomData(html);
        if (userRoom != null && userRoom.optBoolean("hasTableMonster")) {
            final JSONObject tableData = userRoom.optJSONObject("tableData");
            final JSONObject monster = this.findSummon(tableData, element);
            if (monster != null) {
                final String summonId = monster.optString("summonId");
                session.put("summonId", summonId);
                if (this.log.isInfoEnabled()) {
                    final String name = monster.optString("name");
                    this.log.info(String.format("take %s", name));
                }
                return "/quest/summon";
            }
        }
        return "/quest";
    }

    private JSONObject resloveUserRoomData(final String html) {
        final Matcher matcher = QuestUserRoomHandler.USER_ROOM_DATA_PATTERN.matcher(html);
        if (matcher.find()) {
            final String data = matcher.group(1);
            return JSONObject.fromObject(data);
        }
        return null;
    }

    private JSONObject findSummon(final JSONObject tableData,
                                  final String element) {
        final LinkedList<JSONObject> summonList = new LinkedList<JSONObject>();
        for (int i = 1; i <= 5; i++) {
            final JSONObject monster = tableData.optJSONObject(String.valueOf(i));
            if (monster != null) {
                final String elementId = monster.optString("elementId");
                final String rarityId = monster.optString("rarityId");
                if (this.log.isInfoEnabled()) {
                    final String name = monster.optString("name");
                    final String elementName = MonsterConvert.convertElement(elementId);
                    final String rarityName = MonsterConvert.convertRarity(rarityId);
                    this.log.info(String.format("%s, %s, %s",
                                                name,
                                                elementName,
                                                rarityName));
                }

                if (StringUtils.equals(element, "0")) {
                    if (StringUtils.equals(rarityId, "1")) {
                        // 精霊の場合
                        summonList.addFirst(monster);
                    }
                } else {
                    if (StringUtils.equals(elementId, element)) {
                        if (StringUtils.equals(rarityId, "3")) {
                            // 神獣の場合
                            summonList.addFirst(monster);
                        } else if (StringUtils.equals(rarityId, "2")) {
                            // 幻獣の場合
                            summonList.addLast(monster);
                        }
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(summonList)) {
            return summonList.getFirst();
        }
        return null;
    }
}
