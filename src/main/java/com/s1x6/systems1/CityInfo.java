package com.s1x6.systems1;

import java.util.HashMap;
import java.util.Map;

public class CityInfo {
    private final Map<String, Integer> edits = new HashMap<>();
    private final Map<String, Integer> keys = new HashMap<>();

    public Map<String, Integer> getEdits() {
        return edits;
    }

    public Map<String, Integer> getKeys() {
        return keys;
    }
}
