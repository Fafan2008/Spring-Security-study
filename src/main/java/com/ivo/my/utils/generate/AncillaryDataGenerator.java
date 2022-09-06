package com.ivo.my.utils.generate;

import javax.servlet.http.HttpServletRequest;

public class AncillaryDataGenerator {

    public static String generateApplicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
