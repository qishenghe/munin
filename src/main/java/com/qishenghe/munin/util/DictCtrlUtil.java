package com.qishenghe.munin.util;

import com.qishenghe.munin.cache.pack.DictEntity;
import com.qishenghe.munin.cache.pack.DictSinglePack;
import com.qishenghe.munin.kit.CloneUtil;
import com.qishenghe.munin.session.MuninSession;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 字典控制工具
 *
 * @author qishenghe
 * @date 2021/6/5 18:14
 * @change 2021/6/5 18:14 by qishenghe for init
 */
@Data
public class DictCtrlUtil {

    /**
     * MuninSession
     */
    private transient MuninSession muninSession;

    /**
     * 构造
     *
     * @param muninSession muninSession
     */
    public DictCtrlUtil(MuninSession muninSession) {
        this.muninSession = muninSession;
    }

    /**
     * 只读属性配置Key
     */
    public static final String CONFIG_READONLY = "readOnly";

    /**
     * 只读 true：返回示例对象，修改会同步修改缓存内容 false：返回拷贝，随意修改，但性能较差
     */
    private boolean readOnly;

    /**
     * 只读属性默认值
     */
    public static final boolean DEFAULT_READONLY = false;

    /**
     * 根据字典编码获取字典中的全部键值对【有序】
     * 
     * @param dictCode 字典编码
     * @return 键值对【List】
     */
    public List<DictEntity> getPairsListByDictCode(String dictCode) {
        DictSinglePack singleDict = muninSession.getDictPack().getDictPack().get(dictCode);
        if (readOnly) {
            return singleDict.getDictList();
        } else {
            return CloneUtil.deepCopy(singleDict.getDictList());
        }
    }

    /**
     * 根据字典编码获取字典中的全部键值对【无序】
     * 
     * @param dictCode 字典编码
     * @return 键值对【Map】
     */
    public Map<String, DictEntity> getPairsMapByDictCode(String dictCode) {
        DictSinglePack singleDict = muninSession.getDictPack().getDictPack().get(dictCode);
        if (readOnly) {
            return singleDict.getDictMap();
        } else {
            return CloneUtil.deepCopy(singleDict.getDictMap());
        }
    }

    /**
     * 根据字典编码与编码（键）获取指定实体
     * 
     * @param dictCode 字典编码
     * @param code 编码（键）
     * @return 目标实体
     */
    public DictEntity getDictInfoByCode(String dictCode, String code) {
        DictSinglePack singleDict = muninSession.getDictPack().getDictPack().get(dictCode);
        if (readOnly) {
            return singleDict.getDictMap().get(code);
        } else {
            return CloneUtil.deepCopy(singleDict.getDictMap().get(code));
        }
    }

    /**
     * 根据字典编码与含义（值）获取指定实体
     * 
     * @param dictCode 字典编码
     * @param meaning 含义（值）
     * @param dim 是否模糊匹配（true：模糊匹配，false：等值校验）
     * @return 目标实体（多值可能）
     */
    public List<DictEntity> getDictInfoByMeaning(String dictCode, String meaning, boolean dim) {

        DictSinglePack singleDict = muninSession.getDictPack().getDictPack().get(dictCode);
        List<DictEntity> singleDictList = singleDict.getDictList();

        List<DictEntity> resultList = new ArrayList<>();
        for (DictEntity single : singleDictList) {
            // 判空
            if (meaning == null) {
                if (single.getMeaning() == null) {
                    resultList.add(single);
                }
            } else {
                if (dim) {
                    // 模糊
                    if (StringUtils.contains(single.getMeaning(), meaning)) {
                        resultList.add(single);
                    }
                } else {
                    // 等值校验
                    if (meaning.equals(single.getMeaning())) {
                        resultList.add(single);
                    }
                }
            }
        }
        if (readOnly) {
            return resultList;
        } else {
            return CloneUtil.deepCopy(resultList);
        }
    }

}
