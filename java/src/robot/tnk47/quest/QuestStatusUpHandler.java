package robot.tnk47.quest;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class QuestStatusUpHandler extends AbstractEventHandler {

    public QuestStatusUpHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Properties config = this.robot.getConfig();
        final int staminaLimit = Integer.valueOf(config.getProperty("LevelUpHandler.stamina.limit",
                                                                    "0"));
        final int attackPowerLimit = Integer.valueOf(config.getProperty("LevelUpHandler.power.limit",
                                                                        "0"));

        final Map<String, Object> session = this.robot.getSession();
        final int maxStamina = (Integer) session.get("maxStamina");
        final int maxPower = (Integer) session.get("maxPower");
        int attrPoints = (Integer) session.get("attrPoints");
        final String token = (String) session.get("token");

        int attrStaminaP = 0;
        int attrPowerP = 0;
        while (attrPoints > 0) {
            if (staminaLimit > maxStamina + attrStaminaP) {
                attrStaminaP++;
                attrPoints--;
            } else {
                break;
            }
        }
        while (attrPoints > 0) {
            if (attackPowerLimit > maxPower + attrPowerP) {
                attrPowerP++;
                attrPoints--;
            } else {
                break;
            }
        }

        if (attrStaminaP > 0 || attrPowerP > 0) {
            final String path = "/quest/ajax/put-apportion-attr-ability";
            final List<BasicNameValuePair> nvps = new LinkedList<BasicNameValuePair>();
            nvps.add(new BasicNameValuePair("attrStaminaP",
                                            String.valueOf(attrStaminaP)));
            nvps.add(new BasicNameValuePair("attrPowerP",
                                            String.valueOf(attrPowerP)));
            nvps.add(new BasicNameValuePair("token", token));

            final String html = this.httpPost(path, nvps);

            if (this.log.isInfoEnabled()) {
                this.log.info(html);
                this.log.info(String.format("增加了%d体力，增加了%d攻防",
                                            attrStaminaP,
                                            attrPowerP));
            }
        }

        final String callback = (String) session.get("callback");
        return callback;
    }

}
