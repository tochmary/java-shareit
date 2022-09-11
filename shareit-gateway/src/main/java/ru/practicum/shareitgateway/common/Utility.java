package ru.practicum.shareitgateway.common;

import org.apache.commons.lang3.StringUtils;

public class Utility {
    public static String buildPath(Object... args) {
        return StringUtils.join("/", StringUtils.join(args, ""));
    }
}
