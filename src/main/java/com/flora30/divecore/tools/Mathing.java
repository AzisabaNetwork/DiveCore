package com.flora30.divecore.tools;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mathing {
    private double vectorFromLoc(double a, double b){
        return Math.sqrt(a * a + b * b);
    }

    public double getSimpleXFromYaw(double yaw){
        if(yaw < 0){
            yaw += 360;
        }
        return -Math.sin(Math.toRadians(yaw));
    }

    public double getSimpleZFromYaw(double yaw){
        if(yaw < 0){
            yaw += 360;
        }
        return Math.cos(Math.toRadians(yaw));
    }

    public double getSimpleYFromPitch(double pitch){
        if(pitch < 0){
            pitch += 360;
        }
        return Math.sin(Math.toRadians(pitch));
    }

    public List<Location> getLocationsBetweenTwo(Location a, Location b, double space, double maxLength){
        List<Location> list = new ArrayList<>();
        //aとbの間のベクトル（b-a）
        Vector vector = new Vector(b.getX()-a.getX(),b.getY()-a.getY(),b.getZ()-a.getY());
        vector.normalize();
        vector.multiply(space);
        //aがbを越えた判定・・・長さがmaxを越えた時|長さ＝current
        double current_range = space;
        a.add(vector);
        while(current_range <= maxLength){
            //範囲を登録
            list.add(a);
            //その後ベクトルを足す
            a.add(vector);
            current_range += space;
            //ここで次の判定
        }
        return list;
    }

    public static int getInt(String str){
        int i;
        try{
            i = Integer.parseInt(str);
        } catch (NumberFormatException e){
            e.printStackTrace();
            i = -1;
        }
        return i;
    }

    public static double getDouble(String str){
        double d;
        try{
            d = Double.parseDouble(str);
        } catch (NullPointerException|NumberFormatException e){
            e.printStackTrace();
            d = -1;
        }
        return d;
    }

    //0～maxのランダム
    public static Integer getRandomInt(Integer max) {
        Random ran = new Random();
        return ran.nextInt(max);
    }
}
