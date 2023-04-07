package com.project.cmn.util.tree;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Tree 탐색을 위한 객체
 */
public class TreeComponent<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 노드 정보를 가져온다.
     *
     * @return 노드 정보
     */
    public T getInfo() {
        throw new UnsupportedOperationException();
    }

    /**
     * 하위 {@link TreeComponent}들을 가져온다.
     *
     * @return 하위 {@link TreeComponent}들
     */
    public List<TreeComponent<T>> getChildren() {
        throw new UnsupportedOperationException();
    }

    /**
     * 하위 {@link TreeComponent}를 추가한다.
     *
     * @param treeComponent 하위 {@link TreeComponent}
     */
    public void add(TreeComponent<T> treeComponent) {
        throw new UnsupportedOperationException();
    }

    /**
     * 탐색을 위한 반복자를 가져온다
     *
     * @return 탐색을 위한 반복자
     */
    public Iterator<TreeComponent<T>> iterator() {
        throw new UnsupportedOperationException();
    }

    /**
     * 보여줄 HTML 코드를 가져온다.
     *
     * @return 보여줄 HTML 코드
     */
    public String getShowHtml() {
        throw new UnsupportedOperationException();
    }
}
