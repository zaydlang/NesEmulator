package ui;

import java.util.LinkedList;
import java.util.Queue;

public class MaxedQueue<E> extends LinkedList<E> {
    private LinkedList<E> queue;
    private int maxSize;

    public MaxedQueue(int maxSize) {
        super();

        queue = new LinkedList<>();
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(E e) {
        addItem(queue, e);
        return true;
    }

    private void addItem(LinkedList<E> linkedList, E e) {
        linkedList.add(e);

        if (linkedList.size() > maxSize) {
            linkedList.removeLast();
        }
    }

    public Queue getQueue() {
        return queue;
    }
}
