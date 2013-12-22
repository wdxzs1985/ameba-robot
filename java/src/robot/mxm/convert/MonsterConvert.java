package robot.mxm.convert;

import org.apache.commons.lang.StringUtils;

public class MonsterConvert {

    public static final String[] ELEMENTS = { "0", "1", "2", "3", "4", "5", "6" };
    public static final String[] ELEMENTS_NAME = { "???",
                                                  "火",
                                                  "水",
                                                  "水",
                                                  "雷",
                                                  "風",
                                                  "土" };

    public static final String[] RARITIES = { "0", "1", "2", "3" };
    public static final String[] RARITIES_NAME = { "???", "精霊", "幻獣", "神獣" };

    public static String convertElement(final String elementId) {
        for (int i = 1; i < MonsterConvert.ELEMENTS.length; i++) {
            if (StringUtils.equals(elementId, MonsterConvert.ELEMENTS[i])) {
                return MonsterConvert.ELEMENTS_NAME[i];
            }
        }
        return MonsterConvert.ELEMENTS_NAME[0];
    }

    public static String convertRarity(final String rarityId) {
        for (int i = 1; i < MonsterConvert.RARITIES.length; i++) {
            if (StringUtils.equals(rarityId, MonsterConvert.RARITIES[i])) {
                return MonsterConvert.RARITIES_NAME[i];
            }
        }
        return MonsterConvert.RARITIES_NAME[0];
    }
}
