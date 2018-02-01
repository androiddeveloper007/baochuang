package library.floatingview;

/**
 * 用户行为下滑,上滑
 */
public interface ScrollDirectionListener {

    void onScrollDown();

    void onScrollUp();

    void onScrollLeft();

    void onScrollRight();

    /**
     * View反馈的显示和隐藏行为
     */
    public interface ScrollViewListener{
        void hide();
        void show();
    }
}
