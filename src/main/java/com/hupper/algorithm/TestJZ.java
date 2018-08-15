package com.hupper.algorithm;

import org.junit.Test;

/**
 * @author lhp@meitu.com
 * @date 2018/7/8 下午10:47
 */
public class TestJZ {


//    @Test
//    public void test1() {
////        //测试：最小的数，最大的数，中间的数，没有的数
//        int[][] a = {{1, 2, 8, 9}, {2, 4, 9,12}, {4, 7, 10, 13}, {6, 8, 11, 15}};
//        System.out.println(Find(a, 1));
//        System.out.println(Find(a, 15));
//        System.out.println(Find(a, 7));
//        System.out.println(Find(a, 5));
//        System.out.println(Find(a, 0));
//    }

    @Test
    public void test2() {
        //测试：最小的数，最大的数，中间的数，没有的数
        int[][] a = {{1, 2, 8, 9}, {2, 4, 9, 12}, {4, 7, 10, 13}, {6, 8, 11, 15}};
        System.out.println(F(a, 4, 0, 0, a[0].length - 1, a.length - 1));
        System.out.println(Find2(a, 4));
//        System.out.println(F(a, 7));
//        System.out.println(F(a, 5));
//        System.out.println(F(a, 0));
    }

    //    @Test
    public void test3() {
        int[] array = {1, 2, 3, 3, 3, 4, 4, 5, 6, 7, 7};
//        AtomicInteger m = new AtomicInteger();
        Integer m = 0;
//        getNumK(array, 3, 0,array.length-1, m);

        System.out.println(getNumK2(array, 3, 0, array.length - 1));
    }


    //二维数组查找
    private static boolean Find(int array[][], int number) {
        boolean flag = false;
        for (int column = 0; column < array.length; column++) {
            for (int row = 0; row < array[column].length; row++) {
                if (number == array[column][row]) {
                    return true;
                }
            }
        }
        return flag;
    }


    /**
     * 在一个二维数组中，每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。
     * 请完成一个函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数
     * <p>
     * 分析思路： 前提条件 二维数组的每行的列数是相等的，如果不等补充0
     *
     * @param array
     * @param number
     * @return
     */
    private static boolean F(int array[][], int number, int colStart, int rowStart, int colEnd, int rowEnd) {

        if ((rowEnd - rowStart == 1) ||
                (colEnd - colStart == 1)) {
            return number == array[colStart][rowStart] || number == array[colEnd][rowStart]
                    || number == array[colStart][rowEnd] || number == array[colStart][rowEnd];
        }
        int centerCol = ((colEnd + colStart) / 2);
        int centerRow = ((rowEnd + rowStart) / 2);

        if (centerCol < 0 || centerRow < 0 || colEnd < colStart || rowStart > rowEnd) {
            return false;
        }
        if (array[centerCol][centerRow] == number) {
            return true;
        } else if (array[centerCol][centerRow] > number) {
            return F(array, number, colStart, rowStart, centerCol, centerRow);
        } else {
            return F(array, number, centerCol, rowStart, colEnd, centerRow) ||
                    F(array, number, colStart, centerRow, centerCol, rowEnd) ||
                    F(array, number, centerCol + 1, centerRow + 1, colEnd, rowEnd);
        }
    }


    /**
     * 在一个已经排序好的数组，数组里有重复的元素，给定一个已知的数，判断该数在数组里出现的次数
     *
     * @param array
     * @param k
     * @param start
     * @param end
     * @return
     */
    private static int getNumK2(int[] array, int k, int start, int end) {
        if (start > end) {
            return 0;
        }
        int center = (end + start) / 2;
        if (k == array[center]) {
            return 1 + getNumK2(array, k, start, center - 1) + getNumK2(array, k, center + 1, end);
        } else if (center == start && center == end) {
            return 0;
        }
        if (k < array[center]) {
            return getNumK2(array, k, start, center - 1);
        } else if ((k > array[center])) {
            return getNumK2(array, k, center + 1, end);
        } else {
            return 0;
        }
    }

    /**
     * 在一个二维数组中，每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。
     * 请完成一个函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数
     *
     * @param array
     * @param target
     * @return
     */
    public boolean Find2(int[][] array, int target) {
        int col = array.length - 1;
        int row = 0;
        boolean flag = false;
        while (col >= 0 && row <= array[0].length) {
            if (array[row][col] == target) {
                flag = true;
                break;
            } else if (array[row][col] > target) {
                col--;
            } else {
                row++;
            }
        }
        return flag;
    }

    /**
     * 在一个已经排序好的数组，数组里有重复的元素，给定一个已知的数，判断该数在数组里出现的次数
     *
     * @param array
     * @param k
     * @param start
     * @param end
     * @return
     */
    private static void getNumK(int[] array, int k, int start, int end, Integer mm) {
        if (start > end) {
            return;
        }
        int center = (end + start) / 2;
        if (k == array[center]) {
            mm = mm + 1;
            getNumK(array, k, start, center - 1, mm);
            getNumK(array, k, center + 1, end, mm);
            return;
        } else if (center == start && center == end) {
            return;
        }
        if (k < array[center]) {
            getNumK(array, k, start, center - 1, mm);
        } else if ((k > array[center])) {
            getNumK(array, k, center + 1, end, mm);
        }
    }

    /**
     * 这也是归并排序的思路
     * 逆序对个数查找 思路：
     * 把一个大数组，拆成俩个子数组，分别求俩个子数组内部的逆序对+  俩个数组之间的逆序对
     * <p>
     * 在俩个数组之间的逆序对算完之后，要把这俩个数组进行排序，保证下次计算重复计算
     *
     * @param array
     * @param copy
     * @param begin
     * @param end
     * @return
     */
    public static int iPairs(int[] array, int[] copy, int begin, int end) {
        if (begin == end) {
            return 0;
        }
        int mid = (begin + end) / 2;
        // 递归调用
        int left = iPairs(copy, array, begin, mid);
        int right = iPairs(copy, array, mid + 1, end);
        // 归并
        int i = mid, j = end, pos = end;
        // 记录相邻子数组间逆序数
        int count = 0;
        while (i >= begin && j >= mid + 1) {
            if (array[i] > array[j]) {
                copy[pos--] = array[i--];
                count += j - mid;
            } else {
                copy[pos--] = array[j--];
            }
        }
        while (i >= begin) {
            copy[pos--] = array[i--];
        }

        while (j >= mid + 1) {
            copy[pos--] = array[j--];
        }
        return left + right + count;
    }

}