package com.hupper.algorithm;

import java.util.Stack;

/**
 * 实现一个以O(1)的时间复杂度来获得栈的最大值
 * @author lhp@meitu.com
 * @date 2018/7/24 下午4:07
 */
public class MyStack {


    private int[] arr;
    private int size = 0;

    private Stack maxStack = new Stack();
    private int MAX_SIZE = Integer.MAX_VALUE;

    public MyStack(){
        arr = new int [MAX_SIZE];
    }


    public MyStack(int maxSize){
        arr = new int [maxSize];
    }


    public void push(int val){
        arr[size] = val;
        size ++;
        int tmp = (int)maxStack.peek();
        if(val > tmp){
            maxStack.push(val);
        }else{
            maxStack.push(tmp);
        }
    }


    public int pop(){
        int tmp = arr[size];
        size --;
        maxStack.pop();
        return tmp;
    }

    /**
     * 实现一个以O(1)的时间复杂度来获得栈的最大值
     * @return
     */
    public int getMax(){
        return (int)maxStack.peek();
    }


}
