package tocraft.walkers.impl;

/**
 * Duck interface for accessing information about nearby playing music in {@link tocraft.walkers.mixin.PlayerEntityMixin}.
 */
public interface NearbySongAccessor {
    boolean walkers_isNearbySongPlaying();
}
