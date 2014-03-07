package robot.tnk47.quest;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class QuestStatusUpHandler extends Tnk47EventHandler {

    final int staminaUpLimit;
    final int powerUpLimit;

    public QuestStatusUpHandler(final Tnk47Robot robot) {
        super(robot);
        this.staminaUpLimit = this.robot.getStaminaUpLimit();
        this.powerUpLimit = this.robot.getPowerUpLimit();
    }

    @Override
    public String handleIt() {

        final Map<String, Object> session = this.robot.getSession();
        final int maxStamina = (Integer) session.get("maxStamina");
        final int maxPower = (Integer) session.get("maxPower");
        int attrPoints = (Integer) session.get("attrPoints");
        final String token = (String) session.get("token");

        int attrStaminaP = 0;
        int attrPowerP = 0;
        while (attrPoints > 0) {
            if (this.staminaUpLimit > maxStamina + attrStaminaP) {
                attrStaminaP++;
                attrPoints--;
            } else {
                break;
            }
        }
        while (attrPoints > 0) {
            if (this.powerUpLimit > maxPower + attrPowerP) {
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

            final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
            this.resolveJsonToken(jsonResponse);

            if (this.log.isInfoEnabled()) {
                this.log.info(String.format("增加了%d体力，增加了%d攻防",
                                            attrStaminaP,
                                            attrPowerP));
            }
        }

        final String callback = (String) session.get("callback");
        return callback;
    }

}
