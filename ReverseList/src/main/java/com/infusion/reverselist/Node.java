package com.infusion.reverselist;

/**
 * Created by rchase on 9/3/2015.
 */
public class Node {

    private Node next = null;

    /**
     * value is final and private because setting value is cheating. value is just an example of the contents of a node,
     * in this example it's an int but in another scenario it could be a large complex object.
     */
    private final int value;

    public Node(int value, Node next) {
        this.value = value;
        this.next = next;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Node{" + "value=" + value +
                ", " + "next=" + ((next == null) ? "null" : next.getValue()) + "}";
    }
}
