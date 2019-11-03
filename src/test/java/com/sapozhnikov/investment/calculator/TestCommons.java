package com.sapozhnikov.investment.calculator;

import java.io.IOException;
import java.text.MessageFormat;

public class TestCommons {

    public static String readStringFromFile(String path) {
        try {
            return new String(TestCommons.class.getResourceAsStream(path).readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("Error reading file {0}", path));
        }
    }
}
