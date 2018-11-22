package com.hupper.algorithm;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 剑指offer训练题
 *
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

    public static void main(String args[]){

        long s = System.currentTimeMillis();
        int[][] arr = {{1,3,5,7},{10,11,16,20},{23,30,34,50}};
        searchMatrix(arr, 1);
        long e = System.currentTimeMillis();
        System.out.println((e-s)/1000);
    }

//    @Test
    public void test2() {
        //测试：最小的数，最大的数，中间的数，没有的数
        int[][] a = {{1, 2, 8, 9}, {2, 4, 9, 12}, {4, 7, 10, 13}, {6, 8, 11, 15}};
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


//    public static int GetMaxGroupValueOuter(int[] arrary){
//        int endIndex = arrary.length - 1;
//        int startIndex = 0;
//        if (endIndex == 0){
//            return arrary[0];
//        }
//        if (endIndex == 1){
//            return Math.max(arrary[0], arrary[1]);
//        }
//        int t1 = getMaxGroupValue(arrary, startIndex, endIndex - 1);
//        int t2 = getMaxGroupValue(arrary, startIndex, endIndex );
//        return Math.max(t1, t2);
//    }

//    private static int getMaxGroupValue(int[] arrary, int startIndex, int endIndex) {
//        if (startIndex == 0
//                || startIndex == 1) {
//            if (endIndex == startIndex){
//                return arrary[startIndex];
//            }
//            if (endIndex == startIndex + 1) {
//                return Math.max(arrary[startIndex], arrary[startIndex + 1]);
//            }
//        }
//
//
//
//
//    }

    public static List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> list = new ArrayList<>();
        if(nums.length==0){
            return list;
        }
        Arrays.sort(nums);



        for(int m=0;m<nums.length;m++){
            int start = 0;
            int end = nums.length-1;
            while(end>start){
                if(nums[start] + nums[end] == (-nums[m]) && (m!=start && m!=end )){
                    ArrayList<Integer> l = new ArrayList<>();
                    l.add(nums[start]);
                    l.add(nums[end]);
                    l.add(0-nums[start]-nums[end]);
                    Collections.sort(l);
                    if(!list.contains(l)){
                        list.add(l);
                    }

                    start++;
                    end--;
                }else if(nums[start] + nums[end] < -nums[m]){
                    start++;
                }else{
                    end --;
                }

            }
        }
        return list;
    }


    /**
     * 假设按照升序排序的数组在预先未知的某个点上进行了旋转。
     *
     * ( 例如，数组 [0,1,2,4,5,6,7] 可能变为 [4,5,6,7,0,1,2] )。
     *
     * 搜索一个给定的目标值，如果数组中存在这个目标值，则返回它的索引，否则返回 -1 。
     *
     * 你可以假设数组中不存在重复的元素。
     *
     * 你的算法时间复杂度必须是 O(log n) 级别。
     *
     * 示例 1:
     *
     * 输入: nums = [4,5,6,7,0,1,2], target = 0
     * 输出: 4
     * 示例 2:
     *
     * 输入: nums = [4,5,6,7,0,1,2], target = 3
     * 输出: -1
     * @param arr
     * @param target
     * @return
     */
    public static int search(int[] arr, int target) {
        int start = 0;
        int end = arr.length-1;
        int ret = -1;

        if(arr.length==0){
            return ret;
        }else if(arr.length==1){
            return arr[start]==target?start:ret;
        }
        while(end>=start){
            int mid = (end+start)/2;
            if(end-start==1){
                if(arr[start]==target || arr[end]==target){
                    return arr[start]==target?start:end;
                }else {
                    break;
                }
            }
            if(arr[mid] == target){
                ret = mid;
                break ;
            }else {
                //确定mid切刀的位置
                if(arr[mid]>=arr[start]){
                    if(arr[mid]>target && arr[start]<=target){
                        end = mid;
                    }else{
                        start = mid;
                    }
                }else{
                    if(arr[mid]<=target && target<arr[start]){
                        start = mid;
                    }else{
                        end = mid;
                    }
                }
            }
        }
        return ret;
    }

    public static  boolean searchMatrix(int[][] arr, int target) {
        if(arr.length==0 || arr[0].length==0){
            return false;
        }
        int startColumn = arr[0].length-1;
        int startLine = 0;
        boolean f = false;
        while(true){
            if(arr[startLine][startColumn]==target){
                f = true;
                break;
            }else if(arr[startLine][startColumn]<target){
                startLine ++ ;
            }else{
                startColumn--;
            }

            if(startLine>arr.length-1 || startColumn<0){
                break;
            }
        }
        return f;
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
    private static boolean searchMatrix(int array[][], int number, int colStart, int rowStart, int colEnd, int rowEnd) {

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
            return searchMatrix(array, number, colStart, rowStart, centerCol, centerRow);
        } else {
            return searchMatrix(array, number, centerCol, rowStart, colEnd, centerRow) ||
                    searchMatrix(array, number, colStart, centerRow, centerCol, rowEnd) ||
                    searchMatrix(array, number, centerCol + 1, centerRow + 1, colEnd, rowEnd);
        }
    }}