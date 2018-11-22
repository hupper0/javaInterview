package com.hupper.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lhp@meitu.com
 * @date 2018/8/16 下午9:31
 */
public class Test01 {

    public static void main(String args[]) {
////        int[][] num = {{1,2,5,7},
//////                        {3,4,6,77},
//////                        {31,42,63,77},
//////                        {131,142,163,177}};
//////        testint(num);
//
////        int[] num = {1, -2, 3, 10, -4, 7, 2, -1};
////
////        maxSum(num);
//        int[] arr = {5,6,7,8,9 ,10,12, 1,2,3,4 };
////        testint3(arr,0, arr.length-1);
//        testint4(arr,0, arr.length-1,7);

        int arr[] = {1,3,5,2,4,6,7,8};
        int[] arr2 = {7 ,5 ,6 ,8 ,10 ,6,12,13,16, 2,100};
        int[] arr3 = { 8,-4,6,-1,3,7,2,-3};
//        list(arr2);
        list3(arr3);

    }

    //顺时针打印一个矩阵
    private static void testint(int[][] num) {
        int startX = 0;
        int endX = num[0].length - 1;

        int startY = 0;
        int endY = num.length - 1;

        while (true) {
            for (int i = startX; i <= endX; i++) {
                System.out.println(num[startY][i]);
            }
            for (int i = startY + 1; i <= endY; i++) {
                System.out.println(num[i][endX]);
            }
            for (int i = endX - 1; i >= startX; i--) {
                System.out.println(num[endY][i]);
            }
            for (int i = endY - 1; i > startY; i--) {
                System.out.println(num[i][startX]);
            }
            startX++;
            endX--;
            startY++;
            endY--;
            if (startX > endX || startY > endY) {
                return;
            }
        }
    }

    public static int maxSum(int[] num) {
        int curSum = 0;
        int curMaxSum = -99999999;
        int start = 0;
        int end = 0;
        for (int i = 0; i < num.length; i++) {
            if (curSum <= 0) {
                curSum = num[i];
                start = i;
            } else {
                curSum += num[i];
            }
            if (curSum > curMaxSum) {
                curMaxSum = curSum;
                end = i;
            }
        }
        for (int i = start; i <= end; i++) {
            System.out.println(num[i]);
        }
        return curMaxSum;
    }

    /**
     * 输入有个数组，数组里面有正数和负数，求子数组（和），该子数组的和是最大的，
     * 1 2 -1 -3 8 10 -7 6
     * @param arr
     */
    private static void testint0(int[] arr) {
        int start = 0, end = 0;
        int finalsum = 0;
        int tmpSum = 0;
        for(int i =0;i<=arr.length-1;i++){
            if(arr[i]<=0){
                start ++;
            }else{
                tmpSum = tmpSum + arr[i];
                end ++;
            }
        }
    }

    /**
     * 输入有个数组，数组里面有正数和负数，求子数组（和），该子数组的和是最大的，
     *
     * @param num
     */
    private static void testint2(int[] num) {

        int[] num2 = new int[num.length];
        int j = 0;
        int i = 1;
        int tmpSum = num[0];
        int oper = num[0] > 0 ? 1 : -1;
        while (i < num.length) {

            if ((num[i] > 0 && oper == 1) || (num[i] < 0 && oper == -1)) {
                tmpSum = tmpSum + num[i];
            } else {
                if (j - 1 >= 0 && num2[j - 1] + tmpSum > 0) {
                    num2[j] = tmpSum;
                    tmpSum = num[i];
                    j++;
                } else if (j == 0) {
                    num2[j] = tmpSum;
                    tmpSum = num[i];
                    j++;
                } else {
                    tmpSum = num[i];
                }
            }
            if (i == num.length - 1) {
                num2[j] = tmpSum;
            }
            oper = num[i] > 0 ? 1 : -1;
            i++;
        }

        for (int l = 0; l < num2.length; l++) {
            System.out.print(num2[l] + ",");
        }

        System.out.println();
    }


    /**
     * {5,6,7,8,9,2,3,4 }
     * @param arr
     */
    private static void testint3(int[] arr, int begin , int end) {
        int mid = (begin + end)/2;
        if(arr[mid]>arr[begin]){
            testint3(arr, mid, end);
        }
        if(arr[mid]<arr[begin] ){
            testint3(arr, begin, mid);
        }
        if(arr[mid]<arr[begin] ){
            System.out.println(mid);
        }
    }

    /**
     * {5,6,7,8,9 ,10,12, 1,2,3,4 };
     * 旋转数组，二分法查找目标值
     * 把一些可以确定的值 当成定量，使问题简单化
     * @param arr
     * @param begin
     * @param end
     * @param target
     */
    private static void testint4(int[] arr, int begin , int end, int target) {
        int mid = (begin + end)/2;
        if (arr[mid]==target){
            System.out.println(mid);
            return;
        }
        //把切割的点确定位置，把它变成确定的常量
        if(arr[mid] > arr[begin]){
            //切刀左边了。
            if(target>arr[begin] && target<arr[mid]){
                testint4(arr, begin, mid, target);
            }else {
                testint4(arr, mid, end, target);
            }
        }else {
            //切刀you边了。
            if(target>arr[mid] && target<arr[end]){
                testint4(arr, mid, end, target);
            }else {
                testint4(arr, begin, mid, target);
            }
        }
    }


    /**
     * 求最长升顺序序列 的个数
     *  {1,3,5,2,4,6,7,8};
     * d[0] = 0
     * d[1] = 1
     *
     * if a[i] > a[j]
     *      d[i] = d[j] + 1
     *      else d[i] = a[j]
     * @param arr
     */
    private static void list2(int[] arr){
        int[] drr = new int[arr.length+1];
        drr[arr.length] = 1;
        drr[0] = 0;
        for(int i = arr.length-1; i>=1;i--){
            int max = 0;
            for(int j=i-1;j<=arr.length-1;j++){
                if(arr[j] >= arr[i] && drr[j] > max){
                    max=drr[j];
                    drr[i] = max + 1;
                }
            }
        }

        //最后统计drr数组最大值即可
    }



    /**求最长升顺序序列 的个数
     *  {1,3,5,2,4,6,7,8};
     *
     *
     * 解题思路：1、先想逐步遍历的思路 ，计算时间复杂度，如果不行，在考虑分治思想
     *         2、分治思想：递归，二分，
     *         3、遍历：指针  双指针 动态规划
     *
     *      {7 ,5 ,6 ,8 ,10 ,6,12,13,16};
     *
     * d[0] = 0
     * d[1] = 1
     *
     * if a[i] > a[j]
     *      d[i] = d[j] + 1
     *      else d[i] = a[j]
     *
     *
     * @param arr
     */
    private static void list(int[] arr){
        int sum = 1;
        int k = 0;
        while (k<arr.length){
            int preIndex = k;
            int tmpSum = 1;
            int nextStart = k+1;
            boolean hasInit = false;
            for(int i=k+1;i<arr.length;i++){
                if(arr[i] > arr[preIndex]){
                    tmpSum ++ ;
                    preIndex = i;
                }else{
                    if(!hasInit){
                        nextStart = i;
                        hasInit = true;
                    }
                    if(tmpSum>=sum){
                        sum = tmpSum;
                    }
                }
                if(i==arr.length-1){
                    if(tmpSum>sum){
                        sum = tmpSum;
                    }
                }
            }
            if(nextStart>k+1){
                k = nextStart;
            }else {
                k ++;
            }
        }
        System.out.println(sum);
    }


    /**
     * 求数组的所有子数组的和的最大值
     *
     *  8,-4,6,-1,3,7,2,-3
     *
     *  resultSum = 8 找到第一个正数值，
     *
     *  tmpSum ——> 去寻找这样一个值，后面加起来大于resultSum的 如果tmpSum=0 说明tmpSum相互抵消了
     *
     *
     */
    private static void list3(int[] arr){
        int resultSum=0;
        int tmpSum = 0;
        for(int i=0;i<arr.length;i++){
            tmpSum = tmpSum + arr[i];
            if(tmpSum>resultSum){
                resultSum = tmpSum;
            }
            if(tmpSum<0){
                tmpSum = 0;
            }
        }
        System.out.println(resultSum);
    }









}
