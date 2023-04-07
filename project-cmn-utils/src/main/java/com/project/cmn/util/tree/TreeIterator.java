package com.project.cmn.util.tree;

import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * TreeComponent 탐색을 위한 {@link Iterator}
 */
public class TreeIterator<E extends TreeComponent> implements Iterator<E> {
    private final Deque<Iterator<E>> deque = new LinkedBlockingDeque<>();

    /**
     * 생성자
     *
     * @param iterator {@link Iterator}
     */
    public TreeIterator(Iterator<E> iterator) {
        deque.push(iterator);
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        if (deque.isEmpty()) {
            // 자식 노드가 없다는 의미
            return false;
        } else {
            Iterator<E> iterator = deque.peek();

            if (!iterator.hasNext()) {
                deque.pop();

                return hasNext();
            } else {
                return true;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @SuppressWarnings("unchecked")
    @Override
    public E next() {
        if (hasNext()) {
            Iterator<E> iterator = deque.peek();
            E treeComponent = iterator.next();

            deque.push(treeComponent.iterator());

            return treeComponent;
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
