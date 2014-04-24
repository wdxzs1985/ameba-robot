package robot.tnk47.conquest;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class ConquestResultHandler extends Tnk47EventHandler {

    private static final Pattern POINT_PATTERN = Pattern.compile("<p class=\"totalPoint\">(.*?)</p>");

    public ConquestResultHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String token = (String) session.get("token");
        final String path = "/conquest/conquest-battle-result?token=%s";
        final String html = this.httpGet(String.format(path, token));

        if (this.log.isInfoEnabled()) {
            Matcher matcher = POINT_PATTERN.matcher(html);
            if (matcher.find()) {
                String text = matcher.group(1);
                this.log.info(text);
            }
        }

        return "/conquest";
    }
}
