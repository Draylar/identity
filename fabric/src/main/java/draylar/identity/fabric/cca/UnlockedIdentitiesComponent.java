package draylar.identity.fabric.cca;

import dev.architectury.event.EventResult;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import draylar.identity.api.event.UnlockIdentityCallback;
import draylar.identity.api.platform.PlayerAbilities;
import draylar.identity.api.platform.PlayerUnlocks;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class UnlockedIdentitiesComponent implements AutoSyncedComponent {

    private final PlayerEntity player;
    private final List<Identifier> unlocked = new ArrayList<>();

    public UnlockedIdentitiesComponent(PlayerEntity player) {
        this.player = player;
    }

    public boolean unlock(Identifier id) {
        EventResult unlock = UnlockIdentityCallback.EVENT.invoker().unlock((ServerPlayerEntity) player, id);

        if(unlock.asMinecraft() != ActionResult.FAIL && !unlocked.contains(id)) {
            this.unlocked.add(id);
            if(player instanceof ServerPlayerEntity serverPlayerEntity) {
                PlayerUnlocks.sync(serverPlayerEntity);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean unlock(EntityType<?> type) {
        Identifier id = Registry.ENTITY_TYPE.getId(type);
        return unlock(id);
    }

    public boolean has(EntityType<?> type) {
        Identifier id = Registry.ENTITY_TYPE.getId(type);
        return type.equals(EntityType.PLAYER) || unlocked.contains(id);
    }

    public void revoke(EntityType<?> type) {
        Identifier id = Registry.ENTITY_TYPE.getId(type);
        revoke(id);
    }

    public void revoke(Identifier id) {
        if(unlocked.contains(id)) {
            this.unlocked.remove(id);
            if(player instanceof ServerPlayerEntity serverPlayerEntity) {
                PlayerUnlocks.sync(serverPlayerEntity);
            }
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        unlocked.clear();

        // reminder: do not change this tag
        NbtList idList = tag.getList("UnlockedMorphs", NbtType.STRING);

        idList.forEach(idTag -> {
            unlocked.add(new Identifier(idTag.asString()));
        });
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList idList = new NbtList();

        unlocked.forEach(entityId -> {
            idList.add(NbtString.of(entityId.toString()));
        });

        // reminder: do not change this tag
        tag.put("UnlockedMorphs", idList);
    }
}
