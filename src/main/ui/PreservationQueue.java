package ui;

import java.util.LinkedList;
import java.util.Queue;

public class PreservationQueue<E> extends LinkedList<E> {
    private LinkedList<E> queue1;
    private LinkedList<E> queue2;

    private boolean whichQueue; // True: write @ 1, read @ 2    False: write @ 2, read @ 1
    private int     maxSize;

    public PreservationQueue(int maxSize) {
        super();

        queue1 = new LinkedList<>();
        queue2 = new LinkedList<>();

        this.maxSize = maxSize;
        whichQueue   = true;
    }

    @Override
    public boolean add(E e) {
        if (whichQueue) {
            addItem(queue1, e);
        } else {
            addItem(queue2, e);
        }

        return true;
    }

    private void addItem(LinkedList<E> linkedList, E e) {
        linkedList.add(e);

        if (linkedList.size() > maxSize) {
            linkedList.removeLast();
        }
    }

    public void preserve() {
        whichQueue = !whichQueue;
    }

    public Queue getQueue() {
        if (whichQueue) {
            return queue2;
        } else {
            return queue1;
        }
    }
}
