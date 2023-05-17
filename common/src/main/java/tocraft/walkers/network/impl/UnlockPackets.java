package tocraft.walkers.network.impl;

import dev.architectury.networking.NetworkManager;
import tocraft.walkers.api.variant.WalkersType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class UnlockPackets {

    private static final String UNLOCK_KEY = "UnlockedIdentities";

    public static void handleUnlockSyncPacket(PacketByteBuf packet, NetworkManager.PacketContext context) {
        NbtCompound nbt = packet.readNbt();
        if(nbt != null) {
            NbtList list = nbt.getList(UNLOCK_KEY, NbtElement.COMPOUND_TYPE);

            ClientNetworking.runOrQueue(context, player -> {
                ((PlayerDataProvider) player).getUnlocked().clear();
                list.forEach(idTag -> ((PlayerDataProvider) player).getUnlocked().add(WalkersType.from((NbtCompound) idTag)));
            });
        }
    }

    public static void sendSyncPacket(ServerPlayerEntity player) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());

        // Serialize unlocked to tag
        NbtCompound compound = new NbtCompound();
        NbtList idList = new NbtList();
        ((PlayerDataProvider) player).getUnlocked().forEach(type -> idList.add(type.writeCompound()));
        compound.put(UNLOCK_KEY, idList);
        packet.writeNbt(compound);

        // Send to client
        NetworkManager.sendToPlayer(player, NetworkHandler.UNLOCK_SYNC, packet);
    }
}
