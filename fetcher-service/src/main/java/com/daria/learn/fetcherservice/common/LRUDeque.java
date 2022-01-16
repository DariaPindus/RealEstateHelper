package com.daria.learn.fetcherservice.common;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class LRUDeque<T> {

    private static class Node<T> {
        private Node<T> prev;
        private Node<T> next;
        private final T value;
        Node(T value) {
            this.value = value;
            this.prev = null;
            this.next = null;
        }

    }

    private final ReentrantLock lock = new ReentrantLock();
    private final Map<T, Node<T>> map = new LinkedHashMap<>();

    private Node<T> head = null;
    private Node<T> tail = null;

    private int size = 0;
    private int maxSize = 1000;

    public LRUDeque() { }

    public LRUDeque(int maxSize) {
        this.maxSize = maxSize;
    }

    public void add(T element) {
        lock.lock();

        try {
            if (map.containsKey(element)) {
                unlinkNode(map.get(element));
            }

            if (size == maxSize) {
                pollLast();
            }

            Node<T> node = new Node<>(element);
            if (this.size == 0) {
                this.head = node;
                this.tail = node;
            } else {
                this.head.prev = node;
                node.next = head;
                this.head = node;
            }
            map.put(element, node);
            size++;
        } finally {
            lock.unlock();
        }
    }

    public T pollLast() {
        lock.lock();

        try {
            T last = this.tail.value;
            this.tail = tail.prev;
            this.tail.next = null;
            this.map.remove(last);
            size--;
            return last;
        } finally {
            lock.unlock();
        }
    }

    public T pollFirst() {
        lock.lock();

        try {
            T first = this.head.value;
            this.head = head.next;
            this.head.prev = null;
            this.map.remove(first);
            size--;
            return first;
        } finally {
            lock.unlock();
        }
    }

    public boolean contains(T element) {
        return map.containsKey(element);
    }

    private void unlinkNode(Node<T> node) {
        size--;
        if (this.head == node) {
            this.head = node.next;
        } else if (this.tail == node) {
            this.tail = node.prev;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
    }

}
