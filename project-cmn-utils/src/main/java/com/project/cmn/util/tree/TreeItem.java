package com.project.cmn.util.tree;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.Iterator;

/**
 * 자식 노드 객체
 */
@Getter
@RequiredArgsConstructor
public class TreeItem<T extends TreeDto> extends TreeComponent<T> {
    @Serial
    private static final long serialVersionUID = 1L;

    private final T info;

    @Override
    public Iterator<TreeComponent<T>> iterator() {
        return new NullIterator<>();
    }

    @Override
    public String getShowHtml() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<li>\n");
        stringBuilder.append("    <a href='#'>");
        stringBuilder.append(info.getId());
        stringBuilder.append("    </a>\n");
        stringBuilder.append("</li>\n");

        return stringBuilder.toString();
    }
}
