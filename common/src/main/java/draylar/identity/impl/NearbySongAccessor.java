package draylar.identity.impl;

/**
 * Duck interface for accessing information about nearby playing music in {@link draylar.identity.mixin.PlayerEntityMixin}.
 */
public interface NearbySongAccessor {
    boolean identity_isNearbySongPlaying();
}
