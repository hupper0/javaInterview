package com.hupper.algorithm;

/**
 * @author lhp@meitu.com
 * @date 2018/6/24 下午6:00
 */
public class LinkNode {

    public static void main(String[] args) {
        LinkNode list = new LinkNode();
        list.addTailNode(new Node(1));
        list.addTailNode(new Node(4));
        list.addTailNode(new Node(46));
        list.addTailNode(new Node(40));
        list.addTailNode(new Node(8));
//        list.addTailNode(new Node(47));

//        list.delNode(1);
//        System.out.println(list.findElem(list.head, 90));
//        list.printList(list.head);
//        list.deleteDuplecate(list.head);
//        list.printList(list.head);
//        list.insertNode(1,new Node(11));
//        list.printList(list.head);
//        list.reverseIteratively2(list.head);
        list.printList(list.reverseIteratively2(list.head));
    }


    Node tail;
    Node head;
    int pos=-1;

    public LinkNode(){

    }


    public void printList(Node head) {
        while (head != null) {
            System.out.print(head.data + ",");
            head = head.next;
        }
        System.out.println();
    }

    public int length() {
        int length = 0;
        Node tmp = head;
        while (tmp != null) {
            length++;
            tmp = tmp.next;
        }
        return length;
    }

    public Node getNode(int pos) {
        Node t = head;
        while(true){
            if(pos>0){
                pos --;
                t = t.next;
            }else{
                return t;
            }
        }
    }

    public void delNode(int pos){
        if(pos<0){
            return;
        }
        Node t = head;
        while(true){
            if(pos>0){
                pos --;
                t = t.next;
            }else{
                t.next.pre = t.pre;
                t.pre.next = t.next;
                return;
            }
        }
    }

    public void deleteDuplecate(Node head) {
        Node cur = head;
        while (cur!=null){
            //再次遍历链表，如果元素相同 则进行删除节点
            Node start = head;
            Node pre = null;
            while (start!=null){
                if(start!=cur && start.data==cur.data){
                    //删除
                    pre.next = start.next;
                }
                pre = start;
                start = start.next;
            }
            cur = cur.next;
        }
    }


    /**
     * 排序好的链表 如果去除重复元素
     * @param head
     */
    public void deleteDuplecate2(Node head) {
        Node cur = head;
        while (cur!=null){
           Node next = cur.next;
           while (true){
               if(cur.data == next.data){
                    cur.next = next.next;
                    next = next.next;
               }else{
                   break;
               }
           }
            cur = cur.next;
        }
    }


    /**
     * 在某一个位置的前面插入
     * @param pos
     * @param node
     */
    public void insertNode(int pos, Node node) {
        if(node == null || pos<0){
            return ;
        }
        if (pos==0){
            if(head == null){
                head = tail = node.pre = node.next = node;
                return;
            }
            Node t = head;
            node.next = t;
            head = t.pre = node;
        }else {
            Node t_next = head;
            Node t_pre;
            while(true){
                if(pos>0){
                    pos --;
                    t_next = t_next.next;
                }else{
                    t_pre = t_next.pre;
                    t_next.pre = node;
                    node.next = t_next;
                    t_pre.next = node;
                    node.pre = t_pre;
                    return;
                }
            }
        }
    }



    public static Node MergeSortedLists(Node head1,Node head2) {
        if(head1==null){
            return head2;
        }else if(head2==null){
            return head1;
        }
        Node mergedHead = null;

        if(head1.data<head2.data){
            mergedHead = head1;
            mergedHead.next = MergeSortedLists(head1.next, head2);
        }else{
            mergedHead = head2;
            mergedHead.next = MergeSortedLists(head1, head2.next);
        }
        return mergedHead;
    }


    public Node reverseIteratively2(Node head) {
        Node current = head.next;
        Node pre = head;
        Node cn;
        pre.next = null;
        while(current!=null){
            cn = current.next;
            current.next = pre ;

            pre = current;
            current = cn;
        }
        return pre;
    }

        /**
         * 链表翻转  (假设链表是单向)
         * @param head
         * @return
         */
    public Node reverseIteratively(Node head){
//        Node current = head;
//        Node pre = null;
//        Node h_head = null;
//        while(current!=null){
//            Node next = current.next;
//            current.next = pre;
//            pre = current;
//            if(next == null){
//                h_head = current;
//            }
//            current = next;
//        }



        Node current = head;
        Node pre = null;
        Node next;
        Node newHead =null;
        while(current!=null){
            next = current.next;
//            current.next = pre;
            //业务逻辑

//            if(next == null){
//                newHead = current;
//            }
            pre = current;
            current = next;
        }
        return newHead;
    }


    /**
     *  查找倒数 第k个元素
     *  俩个指针，
     * @param head
     * @param k
     * @return
     */
    public Node findElem(Node head, int k) {
        Node t1 = head;
        Node t2 = head;
        int t = k;
        while(t > 0 && t1!=null){
            t --;
            t1 = t1.next;
        }
        while (t1!=null){
            t1 = t1.next;
            t2 = t2.next;
        }
        return t2;
    }
    /**
     * 在链表末尾增加节点
     * @param node
     */
    public void addTailNode(Node node){
        if(node == null){
            return ;
        }
        if(head == null){
            head = tail = node.pre = node.next = node;
        }else{
            tail.next = node;
            node.pre = tail;
            tail = node;
        }
    }


    public Node clone(Node head){
        Node newHead = null;
        if(head!=null){
            newHead = new Node(head.data);
            Node sourcePointer = head.next;
            Node destPointer = newHead;
            while(sourcePointer!=null){
                Node cloneNode = new Node(sourcePointer.data);
                destPointer.next = cloneNode;
                destPointer = cloneNode;
                sourcePointer = sourcePointer.next;
            }
        }

        return newHead;
    }

    public boolean hasCircle(Node head){
        Node h1 = head;
        Node h2 = head;
        while(true){
            h1 = h1.next;
            h2 = h2.next.next;
            if(h1 == h2 && h1!=null){
                return true;
            }
            if(h1==null || h2 ==null){
                return false;
            }
        }
    }

    /**
     *  查找倒数 第k个元素
     *  俩个指针，
     * @param head
     * @param k
     * @return
     */
    public Node findElem2(Node head, int k) {
        int i = k;
        Node n1 = head;
        Node n2 = head;
        while (i>0 && n1!=null){
            i --;
            n1 = n1.next;
        }
        if(n1==null){
            return null;
        }
        while (k<0){
            k--;
            n1 = n1.next;
            n2 = n2.next;
            if(n1==null){
                return null;
            }
        }
        return n2;
    }




}


class Node{
    Node(int data){
        this.data = data;
    }
    int data;
    Node pre;
    Node next;

    @Override
    public String toString() {
        return data+"";
    }
}