package robot.tnk47.guildbattle;

import java.util.HashMap;
import java.util.Map;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public abstract class AbstractGuildBattleHandler extends Tnk47EventHandler {

    protected static final Map<String, Object> CAUTIONMAP = new HashMap<>();

    public AbstractGuildBattleHandler(final Tnk47Robot robot) {
        super(robot);
    }

}
