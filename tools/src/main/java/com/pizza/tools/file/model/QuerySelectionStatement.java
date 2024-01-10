package com.pizza.tools.file.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BoWei
 * 2023/8/25 11:46
 *
 */
public class QuerySelectionStatement {
    private StringBuilder selection;
    private List<String> selectionArgs;
    private boolean needAddPre;

    public QuerySelectionStatement(StringBuilder selection, List<String> selectionArgs, boolean needAddPre) {
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.needAddPre = needAddPre;
    }

    public void append(String selectionNew, String selectionArgsNew) {
        selection.append((needAddPre ? " and " : " ") + selectionNew + " ");
        selectionArgs.add(selectionArgsNew);
    }

    public StringBuilder getSelection() {
        return selection;
    }

    public void setSelection(StringBuilder selection) {
        this.selection = selection;
    }

    public List<String> getSelectionArgs() {
        if (selectionArgs == null) {
            return new ArrayList<>();
        }
        return selectionArgs;
    }

    public void setSelectionArgs(List<String> selectionArgs) {
        this.selectionArgs = selectionArgs;
    }

    public boolean isNeedAddPre() {
        return needAddPre;
    }

    public void setNeedAddPre(boolean needAddPre) {
        this.needAddPre = needAddPre;
    }
}