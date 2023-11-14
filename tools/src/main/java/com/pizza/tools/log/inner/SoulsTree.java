package com.pizza.tools.log.inner;

/**
 * @Description: 日志主干树实现
 */
public class SoulsTree extends Tree {

    private volatile Tree[] forestAsArray = new Tree[0];

    @Override
    public void wtf(String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (Tree tree : forest) {
            tree.wtf(message, args);
        }
    }

    @Override
    public void wtf(Object object) {
        Tree[] forest = forestAsArray;
        for (Tree tree : forest) {
            tree.wtf(object);
        }
    }

    @Override
    public void e(String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (Tree tree : forest) {
            tree.e(message, args);
        }
    }

    @Override
    public void e(Object object) {
        Tree[] forest = forestAsArray;
        for (Tree tree : forest) {
            tree.e(object);
        }
    }

    @Override
    public void w(String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (Tree tree : forest) {
            tree.w(message, args);
        }
    }

    @Override
    public void w(Object object) {
        Tree[] forest = forestAsArray;
        for (Tree tree : forest) {
            tree.w(object);
        }
    }

    @Override
    public void d(String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (Tree tree : forest) {
            tree.d(message, args);
        }
    }

    @Override
    public void d(Object object) {
        Tree[] forest = forestAsArray;
        for (Tree tree : forest) {
            tree.d(object);
        }
    }

    @Override
    public void i(String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (Tree tree : forest) {
            tree.i(message, args);
        }
    }

    @Override
    public void i(Object object) {
        Tree[] forest = forestAsArray;
        for (Tree tree : forest) {
            tree.i(object);
        }
    }

    @Override
    public void v(String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (Tree tree : forest) {
            tree.v(message, args);
        }
    }

    @Override
    public void v(Object object) {
        Tree[] forest = forestAsArray;
        for (Tree tree : forest) {
            tree.v(object);
        }
    }

    @Override
    public void json(String json) {
        Tree[] forest = forestAsArray;
        for (Tree tree : forest) {
            tree.json(json);
        }
    }

    @Override
    public void xml(String xml) {
        Tree[] forest = forestAsArray;
        for (Tree tree : forest) {
            tree.xml(xml);
        }
    }

    @Override
    protected void log(int type, String tag, String message) {
        throw new AssertionError("Missing override for log method.");
    }

    public Tree[] getForestAsArray() {
        return forestAsArray;
    }

    public void setForestAsArray(Tree[] forestAsArray) {
        this.forestAsArray = forestAsArray;
    }
}
