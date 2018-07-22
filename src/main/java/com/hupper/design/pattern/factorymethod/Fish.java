package com.hupper.design.pattern.factorymethod;

/**
 * Function:
 *
 * @author lhp
 *         Date: 19/03/2018 14:32
 * @since JDK 1.8
 */
public class Fish extends Animal {


    @Override
    protected void desc() {
        System.out.println("fish name is=" + this.getName());
    }
}
