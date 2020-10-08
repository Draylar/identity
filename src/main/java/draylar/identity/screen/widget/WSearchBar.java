package draylar.identity.screen.widget;

import io.github.cottonmc.cotton.gui.widget.WTextField;


public class WSearchBar extends WTextField {

    private PostPressListener postKeyPressListener;

    public WSearchBar() {

    }

    public void setPostKeyPressListener(PostPressListener postKeyPressListener) {
        this.postKeyPressListener = postKeyPressListener;
    }

    @Override
    public void onCharTyped(char character) {
        super.onCharTyped(character);

        if(postKeyPressListener != null) {
            postKeyPressListener.yeet(this);
        }
    }

    @Override
    public void onKeyPressed(int character, int keyCode, int keyModifier) {
        super.onKeyPressed(character, keyCode, keyModifier);

        if(postKeyPressListener != null) {
            postKeyPressListener.yeet(this);
        }
    }

    public interface PostPressListener {
        void yeet(WSearchBar bar);
    }
}
