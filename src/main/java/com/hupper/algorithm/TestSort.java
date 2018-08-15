package com.hupper.algorithm;

/**
 * @author lhp@meitu.com
 * @date 2018/7/11 下午7:07
 */
public class TestSort {

    public static void main(String args[]) throws Exception {
//        int arrar[] = {1, 2, 3, 66, 44, 22, 123, 12};
//        int copy[] = {1, 2, 3, 66, 44, 22, 123, 12};
//
////        quick(arrar, 0 , arrar.length-1);
////        selectSort(arrar);
////        insertSort(arrar);
//
//        mergerSort(arrar, copy, 0, arrar.length-1);
//
////        heapSort(arrar);
////
//        printArr(arrar);
//        System.out.println("------------");
//        printArr(copy);

//        System.out.println(4%3);
    }

    private static void printArr(int[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + ",");
        }
        System.out.println();
    }

    private static void quick(int[] array, int begin, int end) {
        if (end > begin) {
            int mid = getMid(array, begin, end);
            quick(array, begin, mid - 1);
            quick(array, mid + 1, end);
        }
    }

    private static int getMid(int[] array, int begin, int end) {
        int mid = array[begin];

        while (end > begin) {
            while (end > begin && mid <= array[end]) {
                end--;
            }
            array[begin] = array[end];
            while (end > begin && mid >= array[begin]) {
                begin++;
            }
            array[end] = array[begin];
        }
        array[begin] = mid;
        return begin;
    }

    private static void wq(int[] array, int i, int j) {
        int t = array[i];
        array[i] = array[j];
        array[j] = t;
    }


    private static void selectSort(int[] array) {
        for (int i = 0; i < array.length; i++) {
            int minIndex = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
            }
            wq(array, i, minIndex);
        }
    }


    /**
     * 堆排序思路：
     * 1、基础知识：如果当前节点左边是i, 那么左右子节点分别是 2*i+1,2*1+2, 父节点是(i-1)/2
     * 2、存在一个指针，从数组末尾开始逐个向前创建小顶堆，然后最顶上的元素时最小的， 然后把堆顶元素和最后的元素进行交换，指针迁移
     * 3、创建小顶堆：指针从末位元素开始，逐个向前移动；指针指向的元素，再找与之对应的父元素，父亲元素和俩个子元素进行比较交换，如果可以比较换，则进行交换，然后指针上移动指向父元素
     * <p>
     * 针对数据，从末尾逐个建立小顶堆，然后交换元素，指针迁移
     *
     * @param arrar
     */
    private static void heapSort(int[] arrar) {
        for (int i = 0; i < arrar.length; i++) {
            createLittleHeap(arrar, arrar.length - 1 - i);
            swap(arrar, 0, arrar.length - 1 - i);
        }
    }

    /**
     * 建立小顶堆，从最后一个元素的父节点开始计算，只要该点有子节点，交换当前节点和子节点最大的元素。然后指针下移，知道该节点没子节点
     * <p>
     * <p>
     * 创建的是小顶堆，移动的指针是 2*i+1  和 i ，i+ 1进行比较 , 如果2*i+1的值大，则进行移动，
     *
     * @param data
     * @param lastIndex
     */
    private static void createLittleHeap(int[] data, int lastIndex) {
        for (int i = (lastIndex - 1) / 2; i >= 0; i--) {
            int parent = i;
            while (2 * parent + 1 <= lastIndex) {
                int bigIndex = 2 * parent + 1;
                if (bigIndex + 1 < lastIndex && data[bigIndex + 1] > data[bigIndex]) {
                    bigIndex = bigIndex + 1;
                }
                if (data[bigIndex] > data[parent]) {
                    swap(data, bigIndex, parent);
                    parent = bigIndex;
                } else {
                    break;
                }
            }
        }
    }


    /**
     * 归并排序
     *
     * @param array
     * @param copy
     * @param begin
     * @param end
     */
    private static void mergerSort(int[] array, int[] copy, int begin, int end) {
        if (begin >= end) {
            return;
        }
        int mid = (end + begin) / 2;

        mergerSort(copy, array, begin, mid);
        mergerSort(copy, array, mid + 1, end);

        int i = mid;
        int pos = end, k = end;

        while (k >= mid + 1 && i >= 0) {
            if (array[i] > array[k]) {
                copy[pos--] = array[i--];
            } else {
                copy[pos--] = array[k--];
            }
        }

        while (i >= 0) {
            copy[pos--] = array[i--];
        }
        while (k >= mid + 1) {
            copy[pos--] = array[k--];
        }

    }


    private static void swap(int[] data, int i, int j) {
        if (i == j) {
            return;
        }
        data[i] = data[i] + data[j];
        data[j] = data[i] - data[j];
        data[i] = data[i] - data[j];
    }


    private static void insertSort(int[] array) {
        for (int i = 0; i < array.length; i++) {
            int j = i + 1;
            while (j > 0 && j < array.length && array[j] < array[i]) {
                wq(array, i, j);
                j--;
                i--;
            }
        }
    }


}