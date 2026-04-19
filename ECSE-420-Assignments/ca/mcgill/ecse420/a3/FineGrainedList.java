package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedList<T> {
    private Node head;

    public FineGrainedList() {
        // Initialize with sentinel nodes to avoid null checks
        head = new Node(Integer.MIN_VALUE);
        head.next = new Node(Integer.MAX_VALUE);
    }

    private class Node {
        T item;
        int key;
        Node next;
        Lock lock = new ReentrantLock();

        Node(int key) { this.key = key; }
    }

    public boolean contains(T item) {
        int key = item.hashCode();
        head.lock.lock(); 
        Node pred = head;
        try {
            Node curr = pred.next;
            curr.lock.lock();
            try {
                while (curr.key < key) {
                    pred.lock.unlock(); // Release previous lock
                    pred = curr;        // Shift pointers
                    curr = curr.next;
                    curr.lock.lock();   // Hand-over-hand: grab next lock
                }
                return (curr.key == key);
            } finally {
                curr.lock.unlock(); // Always release curr
            }
        } finally {
            pred.lock.unlock(); // Always release pred
        }
    }

    // Adds an item to the list in sorted order (by hashCode)
    public boolean add(T item) {
        int key = item.hashCode();
        head.lock.lock();
        Node pred = head;
        try {
            Node curr = pred.next;
            curr.lock.lock();
            try {
                while (curr.key < key) {
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock();
                }
                if (curr.key == key) {
                    return false; // already present
                }
                Node newNode = new Node(key);
                newNode.item = item;
                newNode.next = curr;
                pred.next = newNode;
                return true;
            } finally {
                curr.lock.unlock();
            }
        } finally {
            pred.lock.unlock();
        }
    }
}