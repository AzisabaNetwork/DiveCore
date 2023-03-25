package com.flora30.divecore.tools;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.flora30.divecore.DiveCore;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class PacketUtil{

    /**
     * ブロックを1個変更する
     */
    public static void sendBlockChangePacket(Player player, Material material, Location location){
        sendBlockChangePacket(player, material, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static void sendBlockChangePacket(Player player, Material material, int x, int y, int z){
        // パケットコンテナを作成
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer packetContainer = protocolManager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
        // フィールドへ書き込み
        packetContainer.getBlockPositionModifier().write(0, new BlockPosition(x,y,z));
        packetContainer.getBlockData().write(0, WrappedBlockData.createData(material));
        // 送信
        try{
            protocolManager.sendServerPacket(player, packetContainer);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
