package com.pizza.tools.log.parser;

import java.lang.ref.Reference;

import com.pizza.tools.log.common.LogConvert;

/**
 * @Description: Reference解析器
 */
public class ReferenceParse implements Parser<Reference> {
    @Override
    public Class<Reference> parseClassType() {
        return Reference.class;
    }

    @Override
    public String parseString(Reference reference) {
        Object actual = reference.get();
        StringBuilder builder = new StringBuilder(reference.getClass().getSimpleName() + "<"
                + actual.getClass().getSimpleName() + "> {");
        builder.append("→").append(LogConvert.objectToString(actual));
        return builder.toString() + "}";
    }
}
