package com.kuraserver.popupdamage;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.NukkitRunnable;

import java.util.Map;
import java.util.Random;

public class Main extends PluginBase implements Listener {

    @Override
    public void onEnable() {
        Server.getInstance().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        long eid = Entity.entityCount++;

        Map<Long, Player> targets = event.getEntity().getLevel().getPlayers();

        AddEntityPacket apk = new AddEntityPacket();
        apk.entityUniqueId = eid;
        apk.entityRuntimeId = eid;
        apk.id = "minecraft:item";
        apk.x = (float) event.getEntity().x;
        apk.y = (float) event.getEntity().y;
        apk.z = (float) event.getEntity().z;
        apk.speedX = (1 - new Random().nextInt(2)) * 0.04f;
        apk.speedY = 0.21f;
        apk.speedZ = (1 - new Random().nextInt(2)) * 0.04f;
        apk.yaw = 0;
        apk.headYaw = 0;
        apk.pitch = 0;

        EntityMetadata data = new EntityMetadata();
        data.putString(Entity.DATA_NAMETAG, "§l" + (int) event.getDamage());
        data.putByte(Entity.DATA_ALWAYS_SHOW_NAMETAG, 1);
        data.putFloat(Entity.DATA_SCALE, 0);

        apk.metadata = data;

        targets.forEach((key, player) -> player.dataPacket(apk));

        new NukkitRunnable(){
            @Override
            public void run() {
                RemoveEntityPacket rpk = new RemoveEntityPacket();
                rpk.eid = eid;

                targets.forEach((key, player) -> {if(player.isOnline()) player.dataPacket(rpk);});
            }
        }.runTaskLater(this, 20);
    }

}
