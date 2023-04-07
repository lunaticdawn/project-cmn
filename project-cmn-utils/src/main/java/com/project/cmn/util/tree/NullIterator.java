package com.project.cmn.util.tree;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 말단 노드용 {@link Iterator}
 */
public class NullIterator<E extends TreeComponent> implements Iterator<E> {

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public E next() {
        throw new NoSuchElementException();
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
