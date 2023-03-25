package com.flora30.divecore.base;

import java.util.HashMap;
import java.util.Map;

public class BaseRequire {
    //id | 必要数
    private final Map<Integer,Integer> requireMap = new HashMap<>();

    public Map<Integer, Integer> getRequireMap() {
        return requireMap;
    }
}
