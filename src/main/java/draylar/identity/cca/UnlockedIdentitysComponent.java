package draylar.identity.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import draylar.identity.registry.Components;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.util.sync.EntitySyncedComponent;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class UnlockedIdentitysComponent implements AutoSyncedComponent {

    private final PlayerEntity player;
    private final List<Identifier> unlocked = new ArrayList<>();

    public UnlockedIdentitysComponent(PlayerEntity player) {
        this.player = player;
    }

    public void unlock(Identifier id) {
        if(!unlocked.contains(id)) {
            this.unlocked.add(id);
            Components.UNLOCKED_IDENTITIES.sync(this.player);
        }
    }

    public void unlock(EntityType<?> type) {
        Identifier id = Registry.ENTITY_TYPE.getId(type);
        unlock(id);
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
            Components.UNLOCKED_IDENTITIES.sync(this.player);
        }
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        unlocked.clear();

        ListTag idList = tag.getList("UnlockedMorphs", NbtType.STRING);

        idList.forEach(idTag -> {
            unlocked.add(new Identifier(idTag.asString()));
        });
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        ListTag idList = new ListTag();

        unlocked.forEach(entityId -> {
            idList.add(StringTag.of(entityId.toString()));
        });

        tag.put("UnlockedMorphs", idList);
    }
}
