package robot.fs.quest;

import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import robot.fs.FSEventHandler;
import robot.fs.FSRobot;

public class QuestSearchHandler extends FSEventHandler {

    public QuestSearchHandler(final FSRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String questId = (String) session.get("questId");

        final String path = String.format("/quest/ajax/quest-quest-search?questId=%s",
                                          questId);

        final JSONObject jsonResponse = this.httpGetJSON(path);
        final JSONObject data = jsonResponse.optJSONObject("data");
        final JSONArray questList = data.optJSONArray("questList");
        final JSONObject lastStage = questList.optJSONObject(0);
        final String stageId = lastStage.optString("stageId");

        session.put("stageId", stageId);

        return "/quest/animation";
    }

}
