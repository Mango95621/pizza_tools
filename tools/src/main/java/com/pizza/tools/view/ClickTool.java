package com.pizza.tools.view;

/**
 * Description:
 *
 * @author BoWei
 * 2020/4/28 10:34 AM
 */
public class ClickTool {

    private long lastClickTime;
    private final int fastClickInterval;

    public ClickTool(int fastClickInterval) {
        this.fastClickInterval = fastClickInterval;
    }

    public boolean isCanClick() {
        long curClickTime = System.currentTimeMillis();
        long interval = (curClickTime - lastClickTime);
        lastClickTime = curClickTime;
        return interval > fastClickInterval;
    }
}
