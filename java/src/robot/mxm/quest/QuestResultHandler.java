package robot.mxm.quest;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;
import robot.mxm.convert.EventPointPrinter;

public class QuestResultHandler extends MxmEventHandler {

    public QuestResultHandler(final MxmRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final String path = "/touch/after_animation/result";
        final String html = this.httpGet(path);
        if (this.log.isInfoEnabled()) {
            EventPointPrinter.printPoint(this.log, html);
            EventPointPrinter.printRanking(this.log, html);
            EventPointPrinter.printTreature(this.log, html);
        }
        return "/mypage";
    }

}
