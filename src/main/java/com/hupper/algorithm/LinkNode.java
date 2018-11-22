package com.hupper.algorithm;

import lombok.ToString;

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
//        list.printList(list.reverseIteratively2(list.head));

        ListNode l1 = new ListNode(5);
        l1.next = new ListNode(50);
        l1.next.next = new ListNode(4);
        l1.next.next.next = new ListNode(7);
        l1.next.next.next.next = new ListNode(3);

        printList(sortList2(l1));
    }


    Node tail;
    Node head;
    int pos = -1;

    public LinkNode() {

    }


    public void printList(Node head) {
        while (head != null) {
            System.out.print(head.data + ",");
            head = head.next;
        }
        System.out.println();
    }

    public static void printList(ListNode head) {
        while (head != null) {
            System.out.print(head.val + ",");
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
        while (true) {
            if (pos > 0) {
                pos--;
                t = t.next;
            } else {
                return t;
            }
        }
    }

    public void delNode(int pos) {
        if (pos < 0) {
            return;
        }
        Node t = head;
        Node pre = null;
        while (t != null) {
            if (pos > 0) {
                pos--;
                pre = t;
                t = t.next;
            } else {
                pre.next = t.next;
                return;
            }
        }
    }

    public void deleteDuplecate(Node head) {
        Node cur = head;
        while (cur != null) {
            //再次遍历链表，如果元素相同 则进行删除节点
            Node start = head;
            Node pre = null;
            while (start != null) {
                if (start != cur && start.data == cur.data) {
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
     *
     * @param head
     */
    public void deleteDuplecate2(Node head) {
        Node cur = head;
        while (cur != null) {
            Node next = cur.next;
            while (true) {
                if (cur.data == next.data) {
                    cur.next = next.next;
                    next = next.next;
                } else {
                    break;
                }
            }
            cur = cur.next;
        }
    }


    public void deleteDuplecate3(Node head) {
        Node cur = head;
        while (cur != null) {
            Node point = cur.next;
            Node pointPre = cur;
            while (point != null) {
                if (cur.data == point.data) {
                    pointPre.next = point.next;
                }
                pointPre = point;
                point = point.next;
            }
            cur = cur.next;
        }


    }


    /**
     * 在某一个位置的前面插入
     *
     * @param pos
     * @param node
     */
    public void insertNode(int pos, Node node) {
        if (node == null || pos < 0) {
            return;
        }
        if (pos == 0) {
            if (head == null) {
                head = tail = node.pre = node.next = node;
                return;
            }
            Node t = head;
            node.next = t;
            head = t.pre = node;
        } else {
            Node t_next = head;
            Node t_pre;
            while (true) {
                if (pos > 0) {
                    pos--;
                    t_next = t_next.next;
                } else {
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
    //1 50 4 7
    public static ListNode sortList2(ListNode head) {
        ListNode current = head;
        ListNode pre = new ListNode(0);
        ListNode newHead = pre;
        while(current!=null){
            ListNode nextC = current.next;
            ListNode change = null;
            ListNode changePre = current;
            ListNode tmpPre = changePre;
            while(nextC!=null){
                if(change==null && nextC.val<current.val){
                    tmpPre = changePre;
                    change = nextC;
                }else if(change!=null && nextC.val<change.val){
                    change = nextC;
                    tmpPre = changePre;
                }
                changePre = nextC;
                nextC = nextC.next;
            }
            if(change!=null){
                tmpPre.next = change.next;
                pre.next = change;
                change.next = current;
                pre = change;
            }
            else{
                if(newHead.next==null){
                    newHead.next = current;
                }
                pre = current;
                current = current.next;
            }
        }
        return newHead;
    }


    public static ListNode paration(ListNode begin, ListNode end){
        if(begin == null || begin == end){
            return begin;
        }


        int val = begin.val;
        ListNode index = begin,
                 cur = begin.next;

        while(cur != end){
            if(cur.val < val){
                index = index.next;
                int tmp = cur.val;
                cur.val = index.val;
                index.val = tmp;
            }
            cur = cur.next;
        }


        begin.val = index.val;
        index.val = val;

        return index;
    }



    private static ListNode[] getMin(ListNode head){
        int v = -999999;
        ListNode pre = null;
        ListNode node = head;
        while(head!=null){
            if(head.val<v){
                pre = node;
                node = head;
            }
            head = head.next;
        }
        ListNode[] ret =  {pre, node};
        return ret;
    }



    public static Node MergeSortedLists(Node head1, Node head2) {
        if (head1 == null) {
            return head2;
        } else if (head2 == null) {
            return head1;
        }
        Node mergedHead = null;

        if (head1.data < head2.data) {
            mergedHead = head1;
            mergedHead.next = MergeSortedLists(head1.next, head2);
        } else {
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
        while (current != null) {
            cn = current.next;
            current.next = pre;

            pre = current;
            current = cn;
        }
        return pre;
    }

    /**
     * 链表翻转  (假设链表是单向)
     *
     * @param head
     * @return
     */
    public Node reverseIteratively(Node head) {
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
        Node newHead = null;
        while (current != null) {
            next = current.next;
            current.next = pre;
//            业务逻辑

            if (next == null) {
                newHead = current;
            }
            pre = current;
            current = next;
        }
        return newHead;
    }


    /**
     * 查找倒数 第k个元素
     * 俩个指针，
     *
     * @param head
     * @param k
     * @return
     */
    public Node findElem(Node head, int k) {
        Node t1 = head;
        Node t2 = head;
        int t = k;
        while (t > 0 && t1 != null) {
            t--;
            t1 = t1.next;
        }
        while (t1 != null) {
            t1 = t1.next;
            t2 = t2.next;
        }
        return t2;
    }

    /**
     * 在链表末尾增加节点
     *
     * @param node
     */
    public void addTailNode(Node node) {
        if (node == null) {
            return;
        }
        if (head == null) {
            head = tail = node.pre = node.next = node;
        } else {
            tail.next = node;
            node.pre = tail;
            tail = node;
        }
    }


    public Node clone(Node head) {
        Node newHead = null;
        if (head != null) {
            newHead = new Node(head.data);
            Node sourcePointer = head.next;
            Node destPointer = newHead;
            while (sourcePointer != null) {
                Node cloneNode = new Node(sourcePointer.data);
                destPointer.next = cloneNode;
                destPointer = cloneNode;
                sourcePointer = sourcePointer.next;
            }
        }

        return newHead;
    }

    public boolean hasCircle(Node head) {
        Node h1 = head;
        Node h2 = head;
        while (true) {
            h1 = h1.next;
            h2 = h2.next.next;
            if (h1 == h2 && h1 != null) {
                return true;
            }
            if (h1 == null || h2 == null) {
                return false;
            }
        }
    }

    /**
     * 查找倒数 第k个元素
     * 俩个指针，
     *
     * @param head
     * @param k
     * @return
     */
    public Node findElem2(Node head, int k) {
        int i = k;
        Node n1 = head;
        Node n2 = head;
        while (i > 0 && n1 != null) {
            i--;
            n1 = n1.next;
        }
        if (n1 == null) {
            return null;
        }
        while (k < 0) {
            k--;
            n1 = n1.next;
            n2 = n2.next;
            if (n1 == null) {
                return null;
            }
        }
        return n2;
    }


    /**
     * 合并 k 个排序链表，返回合并后的排序链表。请分析和描述算法的复杂度。
     * <p>
     * 输入:
     * [
     * 1->4->5,
     * 1->3->4,
     * 2->6
     * ]
     * 输出: 1->1->2->3->4->4->5->6
     *
     * @param lists
     * @return
     */
    public static ListNode mergeKLists(ListNode[] lists) {
        ListNode tmp = null;
        for (int i = 0; i < lists.length; i++) {
            if (tmp == null) {
                tmp = lists[i];
            } else {
                tmp = merge(tmp, lists[i]);
            }
        }

        return tmp;
    }


    public static ListNode merge(ListNode n1, ListNode n2) {
        if (n2 == null) {
            return n1;
        } else if (n1 == null) {
            return n2;
        }
        ListNode cu_1 = n1;
        ListNode cu_2 = n2;

        ListNode head;

        //init head;
        if (cu_2.val > cu_1.val) {
            head = cu_1;
            cu_1 = cu_1.next;
        } else {
            head = cu_2;
            cu_2 = cu_2.next;
        }
        ListNode p = head;
        while (true) {
            if (cu_2 == null && cu_1 != null) {
                p.next = cu_1;
                break;
            } else if (cu_1 == null && cu_2 != null) {
                p.next = cu_2;
                break;
            }
            if (cu_1.val < cu_2.val) {
                ListNode tmp = cu_1.next;
                p.next = cu_1;
                p = p.next;
                cu_1 = tmp;

            } else {
                p.next = cu_2;
                p = p.next;
                cu_2 = cu_2.next;
            }
            if (cu_1 == null && cu_2 == null) {
                break;
            }


        }
        return head;

    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
    }

    @Override
    public String toString() {
        return val + "";
    }
}

class Node {
    Node(int data) {
        this.data = data;
    }

    int data;
    Node pre;
    Node next;

    @Override
    public String toString() {
        return data + "";
    }
}