package draylar.identity.screen.widget;

import spinnery.widget.WTextField;

public class WSearchBar extends WTextField {

    private PostPressListener postKeyPressListener;

    public WSearchBar() {

    }

    public void setPostKeyPressListener(PostPressListener postKeyPressListener) {
        this.postKeyPressListener = postKeyPressListener;
    }

    @Override
    public void onCharTyped(char character, int keyCode) {
        super.onCharTyped(character, keyCode);

        if(postKeyPressListener != null) {
            postKeyPressListener.yeet(this);
        }
    }

    @Override
    public void onKeyPressed(int keyCode, int character, int keyModifier) {
        super.onKeyPressed(keyCode, character, keyModifier);

        if(postKeyPressListener != null) {
            postKeyPressListener.yeet(this);
        }
    }

    public interface PostPressListener {
        void yeet(WSearchBar bar);
    }
}
