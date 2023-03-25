package com.flora30.divecore.base;

import java.util.HashMap;
import java.util.Map;

public class BaseData {
    private final Map<Integer,BaseRequire> levelMap = new HashMap<>();

    public Map<Integer, BaseRequire> getLevelMap() {
        return levelMap;
    }
}
