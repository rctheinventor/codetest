package com.infusion.reverselist;


public class Main {

    public static void main(String[] args) {
        Main main = new Main();
        Node previousNode = null;
        Node node = null;
        for (int i = 0; i < 10; i++) {
            node = new Node((10 - i), previousNode);
            previousNode = node;
        }
        Node originalHeadNode = previousNode;
        System.out.println("Original list");
        main.printList(originalHeadNode);
        System.out.println();

        Node newHeadNode = main.reverse(originalHeadNode);

        System.out.println("Reversed list");
        main.printList(newHeadNode);
    }

    /**
     * <p>
     * TODO:  Complete the implementation of this method.
     * </p>
     * Rules:
     * <ol>
     * <li>Not allowed to create new Node objects.</li>
     * <li>Not allowed to modify the value of an existing node.</li>
     * </ol>
     * @param node
     * @return
     */
    private Node reverse(Node node) {
        return null;
    }


    private void printList(Node headNode) {
        while (headNode != null) {
            System.out.println("Current node: " + headNode);
            headNode = headNode.getNext();
        }
    }
}
