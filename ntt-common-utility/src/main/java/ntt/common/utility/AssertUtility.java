package ntt.common.utility;

import org.apache.commons.lang.StringUtils;

import java.util.UUID;

public class AssertUtility {

    public static void notNullOrEmpty(Object obj, String message){
        if(obj == null){
            throw new RuntimeException(message);
        }
        var type = obj.getClass().getTypeName();
        if (type.equals(String.class.getTypeName())) {
            if (StringUtils.isEmpty(obj.toString())) {
                throw new RuntimeException(message);
            }
        } else if (type.equals(UUID.class.getTypeName())) {
            if (obj.toString() == new UUID(0, 0).toString()) {
                throw new RuntimeException(message);
            }
        }
    }

}
