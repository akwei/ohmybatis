package com.github.akwei.ohmybatis;


class CaseUtils {

    static String lowerCamelToLowerUnderScore(String str) {
        if (str == null || str.trim().length() == 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        boolean lastUnderScore = false;
        for (char c : str.toCharArray()) {
            String tmpc = String.valueOf(c);
            if (tmpc.toLowerCase().equals(tmpc)) {
                sb.append(c);
            } else {
                if (!lastUnderScore) {
                    sb.append('_');
                }
                sb.append(tmpc.toLowerCase());
            }
            lastUnderScore = tmpc.equals("_");
        }
        return sb.toString();
    }
}
