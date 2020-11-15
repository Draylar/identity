package draylar.identity.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import draylar.identity.registry.Components;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class FavoriteIdentitiesComponent implements AutoSyncedComponent {

    private final PlayerEntity player;
    private final List<Identifier> favorites = new ArrayList<>();

    public FavoriteIdentitiesComponent(PlayerEntity player) {
        this.player = player;
    }

    public void favorite(Identifier id) {
        if(!favorites.contains(id)) {
            this.favorites.add(id);
            Components.FAVORITE_IDENTITIES.sync(this.player);
        }
    }

    public void favorite(EntityType<?> type) {
        Identifier id = Registry.ENTITY_TYPE.getId(type);
        favorite(id);
    }

    public boolean has(EntityType<?> type) {
        Identifier id = Registry.ENTITY_TYPE.getId(type);
        return type.equals(EntityType.PLAYER) || favorites.contains(id);
    }

    public void unfavorite(EntityType<?> type) {
        Identifier id = Registry.ENTITY_TYPE.getId(type);
        unfavorite(id);
    }

    public void unfavorite(Identifier id) {
        if(favorites.contains(id)) {
            this.favorites.remove(id);
            Components.FAVORITE_IDENTITIES.sync(this.player);
        }
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        favorites.clear();
        ListTag idList = tag.getList("FavoriteIdentities", NbtType.STRING);
        idList.forEach(idTag -> favorites.add(new Identifier(idTag.asString())));
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        ListTag idList = new ListTag();
        favorites.forEach(entityId -> idList.add(StringTag.of(entityId.toString())));
        tag.put("FavoriteIdentities", idList);
    }
}
