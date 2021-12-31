package com.qishenghe.munin.util;

import java.lang.annotation.*;

/**
 * 字典指向 注：用于注解一个属性，指向该属性应用哪个字典编码进行转换
 *
 * @author qishenghe
 * @date 2021/6/8 9:59
 * @change 2021/6/8 9:59 by qishenghe for init
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MuninPoint {

    /**
     * 字典编码
     * 
     * @return 字典编码
     */
    String dictCode();

    /**
     * 字典名称
     * 
     * @return 字典名称
     */
    String dictName() default "";

    // 缺省情况下的处理逻辑
    // 前置non，后置non 按指向处理（先前置，然后后置）
    // 前置non，后置null（修正后置为当前字段），按指向处理（先前置，然后后置）
    // 前置null（修正前置为当前字段），后置non，按指向处理（先前置，然后后置）

    // 前置null，后置null，覆盖原值

    /**
     * 转换前内容放置位置（null：默认不保存原code内容）
     * 
     * @return 属性名
     */
    String beforeTransCopyTo() default "";

    /**
     * 转换结果放置位置（null：默认放置至当前字段）
     * 
     * @return 属性名
     */
    String overTransCopyTo() default "";

    /**
     * 限制转换次数【暂未实现】
     *
     * @return true：限制，false：不限制
     */
    boolean limitTrans() default true;

    /**
     * 可转换次数【暂未实现】
     *
     * @return 可执行转换的次数，默认为1
     */
    int ableTransTimes() default 1;

}
