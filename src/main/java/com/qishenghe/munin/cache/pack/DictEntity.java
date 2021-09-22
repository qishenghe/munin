package com.qishenghe.munin.cache.pack;

import lombok.Data;

import java.io.Serializable;

/**
 * 字典实体
 *
 * @author qishenghe
 * @date 2021/6/7 9:43
 * @change 2021/6/7 9:43 by qishenghe for init
 */
@Data
public class DictEntity implements Serializable, Cloneable {

    /**
     * 字典编码
     */
    private String dictCode;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 编码（键）
     */
    private String code;

    /**
     * 含义（值）
     */
    private String meaning;

    /**
     * 排序编号
     */
    private Integer sortNum;

    /**
     * 扩展信息
     */
    private String expand;

}
