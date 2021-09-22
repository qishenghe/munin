package com.qishenghe.munin.cache.pack;

import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 字典容器（单个字典）
 *
 * @author qishenghe
 * @date 2021/6/7 13:46
 * @change 2021/6/7 13:46 by qishenghe for init
 */
@Data
public class DictSinglePack implements Serializable {

    /**
     * 无序字典
     */
    private Map<String, DictEntity> dictMap;

    /**
     * 有序字典【null_maybe】
     */
    private List<DictEntity> dictList;

    /**
     * 根据单个字典的源数据生成字典实体
     *
     * @param dictEntityList 目标字典的源数据
     * @return 目标字典实体
     * @since 1.0.0
     * @author qishenghe
     * @date 2021/6/7 13:54
     * @change 2021/6/7 13:54 by qishenghe for init
     */
    public static DictSinglePack createSinglePackByTargetDictData(List<DictEntity> dictEntityList) {

        DictSinglePack result = new DictSinglePack();
        // 判空
        if (dictEntityList != null && dictEntityList.size() != 0) {
            // 生成无序字典缓存
            Map<String, DictEntity> resultDictMap = new HashMap<>(dictEntityList.size() << 1, 0.5f);
            for (DictEntity single : dictEntityList) {
                resultDictMap.put(single.getCode(), single);
            }
            // 生成有序字典缓存
            // 排序
            ArrayList<DictEntity> resultDictList =
                    dictEntityList.stream().sorted(Comparator.comparingInt(DictEntity::getSortNum))
                                            .collect(Collectors.toCollection(ArrayList::new));
            // 修剪
            resultDictList.trimToSize();

            result.setDictMap(resultDictMap);
            result.setDictList(resultDictList);
        }

        return result;
    }


}
