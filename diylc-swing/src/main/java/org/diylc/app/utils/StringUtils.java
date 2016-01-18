package org.diylc.app.utils;

import java.util.List;

public class StringUtils {

    public static String toCommaString(List<?> list) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                if (i == list.size() - 1) {
                    builder.append(" and ");
                } else {
                    builder.append(", ");
                }
            }
            builder.append(list.get(i));
        }
        return builder.toString();
    }
}
