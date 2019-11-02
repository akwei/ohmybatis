package com.github.akwei.ohmybatis;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

    @Test
    public void camelToUnderCase() {
        String s = "javaProAkwei_as_Ok";
        String s1 = CaseUtils.lowerCamelToLowerUnderScore(s);
        Assert.assertEquals("java_pro_akwei_as_ok", s1);
    }
}
