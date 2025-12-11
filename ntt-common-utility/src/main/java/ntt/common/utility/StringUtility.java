package ntt.common.utility;

import org.apache.commons.lang.StringUtils;

import java.util.UUID;

public class StringUtility {

    public static boolean isNullOrEmpty(String uuid) {
        if (StringUtils.isEmpty(uuid)) return true;
        if (uuid.equals(new UUID(0, 0).toString())) return true;
        return false;
    }

    public static String toSnakeCase(String camelCase){
        if(StringUtils.isEmpty(camelCase)) return null;
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return camelCase.replaceAll(regex, replacement).toLowerCase();
    }
}
