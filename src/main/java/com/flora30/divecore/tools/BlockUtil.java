package com.flora30.divecore.tools;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.*;
import me.libraryaddict.disguise.disguisetypes.watchers.ArmorStandWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import me.libraryaddict.disguise.utilities.DisguiseValues;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

public class BlockUtil {
    //無条件に通れるブロック
    public static boolean isIgnoreBlockType(Block block) {
        String type = block.getType().toString();
        //サンゴ
        if(type.endsWith("CORAL")){
            return true;
        }
        //サンゴその２
        if(type.endsWith("FAN")){
            return true;
        }
        //看板
        if(type.endsWith("SIGN")){
            return true;
        }
        //旗
        if(type.endsWith("BANNER")){
            return true;
        }
        //感圧版
        if(type.endsWith("PLATE")){
            return true;
        }
        //ボタン
        if(type.endsWith("BUTTON")){
            return true;
        }
        //松明
        if(type.endsWith("TORCH")){
            return true;
        }
        //レール
        if(type.endsWith("RAIL")){
            return true;
        }
        switch (block.getType()) {
            //空気系
            case AIR,CAVE_AIR,VOID_AIR:
                //草系
            case GRASS,FERN,DEAD_BUSH,TALL_GRASS,LARGE_FERN,VINE:
                //作物系
            case WHEAT:
            case BEETROOTS:
            case CARROTS:
            case POTATOES:
            case SUGAR_CANE:
            case SWEET_BERRY_BUSH:
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case NETHER_WART:
                //苗木系
            case SPRUCE_SAPLING:
            case ACACIA_SAPLING:
            case BAMBOO_SAPLING:
            case BIRCH_SAPLING:
            case DARK_OAK_SAPLING:
            case JUNGLE_SAPLING:
            case OAK_SAPLING:
                //花系
            case DANDELION:
            case POPPY:
            case BLUE_ORCHID:
            case ALLIUM:
            case AZURE_BLUET:
            case RED_TULIP:
            case ORANGE_TULIP:
            case WHITE_TULIP:
            case PINK_TULIP:
            case OXEYE_DAISY:
            case CORNFLOWER:
            case LILY_OF_THE_VALLEY:
            case WITHER_ROSE:
            case SUNFLOWER:
            case ROSE_BUSH:
            case LILAC:
            case PEONY:
                //液体系
            case WATER,LAVA:
                //水草系
            case SEAGRASS:
            case TALL_SEAGRASS:
            case KELP_PLANT:
            case KELP:
                //その他
            case COBWEB:
            case SCAFFOLDING:
            case PAINTING:
            case ITEM_FRAME:
            case ARMOR_STAND:
            case REDSTONE:
            case REDSTONE_WIRE:
            case TRIPWIRE:
            case TRIPWIRE_HOOK:
            case STRING:
                return true;
        }
        return false;
    }

    //判定内にあるか
    public static boolean isInBoundBox(Location loc){
        String type = loc.getBlock().getType().toString();

        //1ドット＝0.0625

        if(type.endsWith("STAIRS")){
            //階段分岐
            Stairs stairs = (Stairs) loc.getBlock().getBlockData();
            double varX3 = Math.abs(loc.getX()-loc.getBlockX());
            double varY3 = Math.abs(loc.getY()-loc.getBlockY());
            double varZ3 = Math.abs(loc.getZ()-loc.getBlockZ());
            //階段の上下分岐
            if(stairs.getHalf().equals(Bisected.Half.TOP)){
                if(varY3 > 0.5){
                    return true;
                }
            }
            else{
                if(varY3 < 0.5){
                    return true;
                }
            }
            //階段の方角分岐
            switch(stairs.getFacing()){
                case SOUTH:
                    //south : 空間はZ- → (Z < 0.5 が正解)
                    //正解だった時にfalse = 範囲外
                    return !(varZ3 < 0.5);
                case NORTH:
                    //north : (Z > 0.5が正解)
                    return !(varZ3 > 0.5);
                case EAST:
                    //east : 空間はx- → (X < 0.5)
                    return !(varX3 < 0.5);
                case WEST:
                    //west : (X > 0.5)
                    return !(varX3 > 0.5);
            }
        }
        if(type.endsWith("FENCE")){
            //木の柵分岐 = +-0.125   0.5 - 0.125 = 0.375
            double varX = Math.abs(loc.getX()-loc.getBlockX());
            double varZ = Math.abs(loc.getZ()-loc.getBlockZ());
            if(0.375 < varX && varX < 0.625){
                return true;
            }
            return 0.375 < varZ && varZ < 0.625;
        }
        if(type.endsWith("PANE") || type.equals("IRON_BARS")){
            //鉄柵分岐 = +-0.0625
            double varX2 = Math.abs(loc.getX()-loc.getBlockX());
            double varZ2 = Math.abs(loc.getZ()-loc.getBlockZ());
            if(0.4375 < varX2 && varX2 < 0.5625){
                return true;
            }
            return 0.4375 < varZ2 && varZ2 < 0.5625;
        }
        BoundingBox box = loc.getBlock().getBoundingBox();
        return box.contains(loc.getX(), loc.getY(), loc.getZ());
    }

    public static BoundingBox getBoundingBox(Entity entity){
        if(DisguiseAPI.isDisguised(entity)){
            //出力
            return getDisguisedBoundingBox(entity);
        }
        else{
            return entity.getBoundingBox();
        }
    }

    private static BoundingBox getDisguisedBoundingBox(Entity entity){
        //ディスガイズを作成
        Disguise disguise = DisguiseAPI.getDisguise(entity);

        //オフセットの初期値
        double offsetX = 0.0;
        double offsetY = 0.0;
        double offsetZ = 0.0;

        //MobDisguiseへキャスト
        if (disguise instanceof MobDisguise){
            MobDisguise mobDisguise = (MobDisguise) disguise;
            DisguiseValues values = DisguiseValues.getDisguiseValues(mobDisguise.getType());

            if (values != null && values.getAdultBox() != null) {

                if (!mobDisguise.isAdult() && values.getBabyBox() != null) {
                    //赤ちゃんの時の代入
                    offsetX = values.getBabyBox().getX();
                    offsetY = values.getBabyBox().getY();
                    offsetZ = values.getBabyBox().getZ();
                }

                else {
                    if (mobDisguise.getWatcher() != null) {

                        if (mobDisguise.getType() == DisguiseType.ARMOR_STAND) {
                            //アーマースタンの時の代入
                            offsetX = (((ArmorStandWatcher)mobDisguise.getWatcher()).isSmall() ? values.getBabyBox() : values.getAdultBox()).getX();
                            offsetY = (((ArmorStandWatcher)mobDisguise.getWatcher()).isSmall() ? values.getBabyBox() : values.getAdultBox()).getY();
                            offsetZ = (((ArmorStandWatcher)mobDisguise.getWatcher()).isSmall() ? values.getBabyBox() : values.getAdultBox()).getZ();
                        }

                        else if (mobDisguise.getType() == DisguiseType.SLIME || mobDisguise.getType() == DisguiseType.MAGMA_CUBE) {
                            //スライムの時の代入
                            offsetX = offsetY = offsetZ = 0.51D * 0.255D * (double)((SlimeWatcher)mobDisguise.getWatcher()).getSize();
                        }

                        else {
                            //その他
                            offsetX = values.getAdultBox().getX();
                            offsetY = values.getAdultBox().getY();
                            offsetZ = values.getAdultBox().getZ();
                        }

                    }
                }
            } else {
                offsetX = offsetY = offsetZ = 0.0D;
            }
        }
        if (disguise instanceof PlayerDisguise){
            //プレイヤーの見た目の時＝ゾンビのプレイヤーDisguise＝当たり判定は一緒
            return entity.getBoundingBox();
        }
        if(disguise instanceof MiscDisguise){
            //ブロック・アイテムの見た目の時＝1ブロック分の判定に統一
            offsetX = 1.0;
            offsetY = 1.0;
            offsetZ = 1.0;
        }


        //オフセットから座標に変換
        Location location = entity.getLocation().clone();
        Location maxLocation = new Location(location.getWorld(), location.getX() + offsetX, location.getY() + offsetY, location.getZ() + offsetZ);
        Location minLocation = new Location(location.getWorld(), location.getX() - offsetX, location.getY(), location.getZ() - offsetZ);

        return new BoundingBox(maxLocation.getX(),maxLocation.getY(),maxLocation.getZ(),minLocation.getX(),minLocation.getY(),minLocation.getZ());
    }
}
