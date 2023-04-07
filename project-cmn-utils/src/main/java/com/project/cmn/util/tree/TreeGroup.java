package com.project.cmn.util.tree;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 자식을 가진 노드 객체
 */
@Getter
@RequiredArgsConstructor
public class TreeGroup<T extends TreeDto> extends TreeComponent<T> {
    @Serial
    private static final long serialVersionUID = 1L;

    private T info;

    private List<TreeComponent<T>> children;

    public TreeGroup(T info) {
        this.info = info;
        this.children = new ArrayList<>();
    }

    @Override
    public void add(TreeComponent<T> treeComponent) {
        if (children == null) {
            children = new ArrayList<>();
        }

        children.add(treeComponent);
        info.add(treeComponent.getInfo());
    }

    @Override
    public Iterator<TreeComponent<T>> iterator() {
        return new TreeIterator<>(children.iterator());
    }

    @Override
    public String getShowHtml() {
        StringBuilder stringBuilder = new StringBuilder();

        if (StringUtils.equals(info.getId(), "0")) {
            stringBuilder.append("<ul>\n");

            if (children != null && !children.isEmpty()) {
                for (TreeComponent<T> treeComponent : children) {
                    stringBuilder.append(treeComponent.getShowHtml());
                }
            }

            stringBuilder.append("</ul>");
        } else {
            stringBuilder.append("<li>\n");
            stringBuilder.append("    <a href='#'>");
            stringBuilder.append(info.getId());
            stringBuilder.append("    </a>\n");

            if (children != null && !children.isEmpty()) {
                stringBuilder.append("    <ul>\n");

                for (TreeComponent<T> treeComponent : children) {
                    stringBuilder.append(treeComponent.getShowHtml());
                }

                stringBuilder.append("    </ul>\n");
            }

            stringBuilder.append("</li>\n");
        }

        return stringBuilder.toString();
    }
}
