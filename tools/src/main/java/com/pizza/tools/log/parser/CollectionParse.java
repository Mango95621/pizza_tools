package com.pizza.tools.log.parser;

import android.annotation.SuppressLint;

import java.util.Collection;
import java.util.Iterator;

import com.pizza.tools.log.common.LogConvert;

/**
 * Collection解析器
 */
public class CollectionParse implements Parser<Collection> {
    @Override
    public Class<Collection> parseClassType() {
        return Collection.class;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String parseString(Collection collection) {
        String simpleName = collection.getClass().getName();
        StringBuilder msg = new StringBuilder("%s size = %d [" + LINE_SEPARATOR);
        msg = new StringBuilder(String.format(msg.toString(), simpleName, collection.size()));
        if (!collection.isEmpty()) {
            Iterator iterator = collection.iterator();
            int flag = 0;
            while (iterator.hasNext()) {
                String itemString = "[%d]:%s%s";
                Object item = iterator.next();
                msg.append(String.format(itemString, flag, LogConvert.objectToString(item),
                        flag++ < collection.size() - 1 ? "," + LINE_SEPARATOR : LINE_SEPARATOR));
            }
        }
        return msg + "]";
    }
}
