package com.infusion.reverselist;

public class MainComplete {

    public static void main(String[] args) {
        MainComplete main = new MainComplete();
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

        Node newHeadNode = main.reverseRecursive(originalHeadNode);

        System.out.println("Reversed list - recursive");
        main.printList(newHeadNode);
        System.out.println();

        newHeadNode = main.reverseIterative(newHeadNode);

        System.out.println("Reversed list - iterative");
        main.printList(newHeadNode);
        System.out.println();
    }

    private Node reverseIterative(Node currentNode) {
        Node previousNode = null;
        Node nextNode;
        while (currentNode != null) {
            nextNode = currentNode.getNext();
            currentNode.setNext(previousNode);
            previousNode = currentNode;
            currentNode = nextNode;
        }
        return previousNode;
    }

    private Node reverseRecursive(Node node) {
        if (node == null) {
            return null;
        }

        if (node.getNext() == null) {
            return node;
        }

        Node restOfList = reverseRecursive(node.getNext());
        node.getNext().setNext(node);
        node.setNext(null);
        return restOfList;
    }

    private void printList(Node headNode) {
        while (headNode != null) {
            System.out.println("Current node: " + headNode);
            headNode = headNode.getNext();
        }
    }
}
