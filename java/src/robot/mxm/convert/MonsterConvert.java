package robot.mxm.convert;

import org.apache.commons.lang.StringUtils;

public class MonsterConvert {

	public static String convertElement(String elementId) {
		if (StringUtils.equals(elementId, "1")) {
			return ("火");
		} else if (StringUtils.equals(elementId, "2")) {
			return ("水");
		} else if (StringUtils.equals(elementId, "3")) {
			return ("木");
		} else if (StringUtils.equals(elementId, "4")) {
			return ("雷");
		} else if (StringUtils.equals(elementId, "5")) {
			return ("風");
		} else if (StringUtils.equals(elementId, "6")) {
			return ("土");
		}
		return "???";
	}

	public static String convertRarity(String rarityId) {
		if (StringUtils.equals(rarityId, "1")) {
			return ("精霊");
		} else if (StringUtils.equals(rarityId, "2")) {
			// 幻獣の場合
			return ("幻獣");
		} else if (StringUtils.equals(rarityId, "3")) {
			// 神獣の場合
			return ("神獣");
		}
		return "???";
	}
}
