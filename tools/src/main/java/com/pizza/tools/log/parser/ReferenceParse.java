package com.pizza.tools.log.parser;

import java.lang.ref.Reference;

import com.pizza.tools.log.common.LogConvert;

/**
 * Reference解析器
 */
public class ReferenceParse implements Parser<Reference> {
    @Override
    public Class<Reference> parseClassType() {
        return Reference.class;
    }

    @Override
    public String parseString(Reference reference) {
        Object actual = reference.get();
        String builder = reference.getClass().getSimpleName() + "<"
                + actual.getClass().getSimpleName() + "> {" + "→" + LogConvert.objectToString(actual);
        return builder + "}";
    }
}
