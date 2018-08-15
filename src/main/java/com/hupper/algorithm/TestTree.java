package com.hupper.algorithm;

import lombok.ToString;

import java.util.LinkedList;
import java.util.Stack;

/**
 * @author lhp@meitu.com
 * @date 2018/6/26 上午11:12
 */
public class TestTree {


    public static void main(String[] args) throws Exception {
        BiTree b1 = new BiTree(1);
        b1.left = new BiTree(2);
        b1.right = new BiTree(3);
        b1.left.left = new BiTree(4);
        b1.left.right = new BiTree(5);
        b1.right.right = new BiTree(6);
        b1.right.left = new BiTree(7);
//        b1.left.left.right = new BiTree(100);
//
////        levelTraverse(b1, new LinkedList());
        BiTree b2 = new BiTree(-1);
        convert(b1);
        System.out.println("");
//        lastOrderByStack(b1);
//        int[] A = {1,5,8,11,12,4,5,6,7,8,0,1,2,3};
////        System.out.println(findMin(A, 6,0, A.length));
//
//        ReorderOddEven(A);
//        System.out.println(Arrays.toString(A));

    }


    public int deep(BiTree root) {

        if (root != null) {
            int leftDeep = deep(root.left) + 1;
            int rightDeep = deep(root.right) + 1;
            return Math.max(leftDeep, rightDeep);
        } else {
            return 0;
        }
    }


    //递归实现前(先)序遍历  DLR
    protected static void preorder(BiTree biTree) {
        if (biTree != null) {
            System.out.println(biTree.data);
            preorder(biTree.left);
            preorder(biTree.right);
        }
    }

    //zhonng须 LDR
    protected static void minorder(BiTree biTree) {
        if (biTree != null) {
            minorder(biTree.left);
            System.out.print(biTree.data + ",");
            minorder(biTree.right);
        }
    }

    //后续 左右中LRD
    protected static void lastrder(BiTree biTree) {
        if (biTree != null) {
            lastrder(biTree.left);
            lastrder(biTree.right);
            System.out.print(biTree.data + ",");

        }
    }

    public static void preOrderByStack(BiTree biTree) {
        Stack<BiTree> stack = new Stack<>();
        BiTree node = biTree;
        while (true) {
            while (node != null) {
                System.out.print(node.data);
                stack.push(node);
                node = node.left;
            }

            while (!stack.isEmpty()) {
                BiTree biTree1 = stack.pop();
                if (biTree1.right != null) {
                    node = biTree1.right;
                    break;
                }
            }
            if (node == null && stack.isEmpty()) {
                return;
            }
        }
    }


    /**
     * 栈 中序遍历
     * <p>
     * <p>
     * 中序：左中右； 从根节点不断循环去找左节点，入栈； 如果左节点空，则进行出栈 【输出出栈的结果】，出栈后如果该节点右节点不为空，则继续不断循环左节点入栈
     * 保存指针节点
     * <p>
     * 先顺序： 中左右； 从根节点不断循环去找左节点，【输出节点】，然后入栈；如果左节点空，则进行出栈  出栈后如果该节点右节点不为空 则继续不断循环左节点入栈（入栈前输出结果）
     * 保存指针节点
     * <p>
     * <p>
     * <p>
     * 第三方缓存: 数组， 队列 链表， 栈
     * <p>
     * 双层while  依赖指针传递信息
     *
     * @param biTree
     */
    public static void minOrderByStack(BiTree biTree) {
        Stack<BiTree> stack = new Stack<>();
        BiTree node = biTree;
        while (true) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }

            while (!stack.isEmpty()) {
                BiTree biTree1 = stack.pop();
                if (biTree1 != null) {
                    System.out.print(biTree1.data + ",");
//                    node = biTree;
                    if (biTree1.right != null) {
//                    stack.push(biTree1.right);
                        node = biTree1.right;
                        break;
                    }
                }
            }
            if (node == null && stack.isEmpty()) {
                return;
            }
        }
    }


    public static void lastOrderByStack(BiTree biTree) {
        Stack<BiTree> stack = new Stack<>();
        BiTree node = biTree;
        BiTree node2 = null;
        while (true) {

            while (node != null) {
                stack.push(node);
                node = node.left;
            }


            while (!stack.isEmpty()) {
                BiTree biTree1 = stack.peek();
                if (biTree1 != null) {
                    if (biTree1.right != null && biTree1.right != node2) {
//                    stack.push(biTree1.right);

                        node = biTree1.right;
                        break;
                    } else {
                        biTree1 = stack.pop();
                        node2 = biTree1;
                        System.out.print(biTree1.data + ",");
                    }
                }
            }
            if (node == null && stack.isEmpty()) {
                return;
            }
            if (node2 == biTree) {
                return;
            }
        }
    }


    //左中右
    public static void minOrderByStack2(BiTree biTree) {
        BiTree current = biTree;
        Stack<BiTree> stack = new Stack();
        while (true) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            while (!stack.isEmpty()) {
                BiTree node = stack.pop();
                System.out.println(node);
                if (node.right != null) {
                    current = node.right;
                    break;
                }
            }
            if (stack.isEmpty() && current == null) {
                break;
            }
        }
    }


    //左右中
    public static void lastOrderByStack2(BiTree biTree) {
        BiTree current = biTree;
        Stack<BiTree> stack = new Stack<>();
        BiTree pre = null;
        while (true) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }

            while (!stack.isEmpty()) {
                BiTree node = stack.peek();
                if (node.right != null && node.right != pre) {
                    current = node.right;
                    break;
                } else {
                    node = stack.pop();
                    pre = node;
                    System.out.println(node);
                }
            }
            if (stack.isEmpty() && current == null) {
                break;
            }
        }
    }


    /**
     * 广度度优先遍历
     *
     * @param root
     */
    public static void levelTraverse(BiTree root, LinkedList<BiTree> list) {
        while (true) {
            BiTree biTree = list.poll();
            if (biTree != null) {
                System.out.print(biTree.data);
            } else {
                break;
            }
        }
        if (root != null) {
            if (root.left != null) {
                list.offer(root.left);
            }
            if (root.right != null) {
                list.offer(root.right);
            }
            levelTraverse(root.left, list);
            levelTraverse(root.right, list);
        }
    }

    /**
     * 输入一棵二叉搜索树，将该二叉搜索树转换成一个排序的双向链表。要求:不能创建任何新的结点，只能调整树中结点指针的指向。
     *
     * @param root
     * @return
     */
    public static void convert(BiTree root) {
        BiTree current = root;
        Stack<BiTree> stack = new Stack<>();
        BiTree pre = null;
        while (true) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            while (!stack.isEmpty()) {
                BiTree node = stack.pop();
                if (pre != null) {
                    pre.right = node;
                    node.left = pre;
                    pre = node;
                } else {
                    pre = node;
                }
                if (node.right != null) {
                    current = node.right;
                    break;
                }
            }
            if (stack.isEmpty() && current == null) {
                break;
            }
        }
    }

    //中序遍历
    private static void minorder2(BiTree biTree) {
        if (biTree != null) {
            minorder2(biTree.left);
            System.out.print(biTree.data + ",");
            minorder2(biTree.right);
        }
    }


    /**
     * int[] A = {4,5,6,7,8,0,1,2,3};
     * int[] A1 = {1,1,0,0,1,1,1};
     * 题目大致为：
     * 一个递增排序的数组的一个旋转(把一个数组最开始的若干元素搬到数组的末尾，称之为数组的旋转)，输出旋转数组的最小元素。
     * 思路：
     * 其实旋转过后的数组是部分有序的，这样的数组依然可以使用折半查找的思路求解
     *
     * @param arr
     * @return
     * @throws Exception
     */
    public static int findMin(int arr[], int data, int start, int end) throws Exception {
        int mid = (end + start) / 2;
        if (data == arr[mid]) {
            return mid;
        }
        if (data > arr[start] && data < arr[mid]) {
            return findMin(arr, data, start, mid);
        }
        if (data > arr[start] && data > arr[mid]) {
            return findMin(arr, data, mid, end);
        } else if (data < arr[start]) {
            return findMin(arr, data, mid, end);
        } else {
            return -100;
        }
    }

    /**
     * 对于一个数组，实现一个函数使得所有奇数位于数组的前半部分，偶数位于数组的后半部分
     *
     * @param arr
     */
    public static void ReorderOddEven(int[] arr) {
        int start = 0;
        int end = arr.length - 1;
        while (start < end) {
            while (arr[start] % 2 != 0) {      //奇数 就循环
                start++;
            }
            if (start >= end) {
                return;
            }
            while (arr[end] % 2 == 0) {       //偶数就循环
                end--;
            }
            if (start >= end) {
                return;
            }
            //交换
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
        }
    }


    private int bsearch(int start, int end, int arr[], int data) {
        int mid = (end + start) / 2;
        if (data == arr[mid]) {
            return mid;
        }
        if (arr[mid] > data) {
            return bsearch(start, mid, arr, data);
        } else {
            return bsearch(mid, end, arr, data);
        }
    }
}

@ToString
class BiTree {
    BiTree left;
    BiTree right;

    int data;

    public BiTree(int data) {
        this.data = data;
    }


//    public String toString(){
//        return data+"";
//    }
}