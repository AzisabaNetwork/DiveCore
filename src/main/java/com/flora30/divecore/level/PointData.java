package com.flora30.divecore.level;

public class PointData implements Cloneable{
    // そのまま適用
    public double stamina = 0;
    public double health = 0;

    // 割合で適用
    public double exp = 0;
    public double weapon = 0;
    public double artifact = 0;
    public double lucky = 0;
    public double gatherMonster = 0;
    public double gatherRelic = 0;

    @Override
    public PointData clone() {
        try {
            return (PointData)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new AssertionError();
        }
    }
}
