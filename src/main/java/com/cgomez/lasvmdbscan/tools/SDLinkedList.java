
package com.cgomez.lasvmdbscan.tools;

/**
 *
 * @author Alan Filipe
 */
public class SDLinkedList<E> {
    
    public static class Node<E> {
        
        public Node prev;
        public Node next;
        public E obj;

        public Node(E obj, Node prev, Node next){
            this.obj = obj;
            this.prev = prev;
            this.next = next;
        }
    }
    
    public final Node<E> start;
    
    public SDLinkedList(){
        start = new Node<>(null, null, null);
    }
    
    public void addFirst(E obj){
        Node<E> node = new Node<>(obj, start, start.next);
        start.next = node;
    }
    
    public void remove(Node node){
        node.prev.next = node.next;
    }
}
