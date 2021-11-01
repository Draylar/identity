package draylar.identity.fabric.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import draylar.identity.api.platform.PlayerAbilities;
import draylar.identity.fabric.registry.Components;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
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
            if(player instanceof ServerPlayerEntity serverPlayerEntity) {
                PlayerAbilities.sync(serverPlayerEntity);
            }
        }
    }

    public void favorite(EntityType<?> type) {
        Identifier id = Registry.ENTITY_TYPE.getId(type);
        favorite(id);
        Components.FAVORITE_IDENTITIES.sync(player);
    }

    public boolean has(EntityType<?> type) {
        Identifier id = Registry.ENTITY_TYPE.getId(type);
        return type.equals(EntityType.PLAYER) || favorites.contains(id);
    }

    public void unfavorite(EntityType<?> type) {
        Identifier id = Registry.ENTITY_TYPE.getId(type);
        unfavorite(id);
        Components.FAVORITE_IDENTITIES.sync(player);
    }

    public void unfavorite(Identifier id) {
        if(favorites.contains(id)) {
            this.favorites.remove(id);
            if(player instanceof ServerPlayerEntity serverPlayerEntity) {
                PlayerAbilities.sync(serverPlayerEntity);
            }
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        favorites.clear();
        NbtList idList = tag.getList("FavoriteIdentities", NbtType.STRING);
        idList.forEach(idTag -> favorites.add(new Identifier(idTag.asString())));
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList idList = new NbtList();
        favorites.forEach(entityId -> idList.add(NbtString.of(entityId.toString())));
        tag.put("FavoriteIdentities", idList);
    }
}
