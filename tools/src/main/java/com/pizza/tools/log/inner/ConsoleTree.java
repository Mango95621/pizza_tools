package com.pizza.tools.log.inner;

/**
 * @date: 2017-03-31 14:29
 */
public class ConsoleTree extends Tree {
    @Override
    protected void log(int type, String tag, String message) {
        System.out.println(tag + "\t" + message);
    }
}
