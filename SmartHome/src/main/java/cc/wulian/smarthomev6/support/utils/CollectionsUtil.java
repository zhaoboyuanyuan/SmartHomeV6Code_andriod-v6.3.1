package cc.wulian.smarthomev6.support.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CollectionsUtil {

    public static <K, V> List<V> mapConvertToList(Map<K, V> map) {
        List<V> list = new ArrayList<V>();
        for (V value : map.values()) {
            list.add(value);
        }
        return list;
    }

    /**
     * 获取列表最后一条数据
     */
    public static <T> T last(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        return list.get(list.size() - 1);
    }

    /**
     * 获取列表第一条数据
     */
    public static <T> T first(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    public static <K> boolean isEquals(Collection<K> collection1,
                                       Collection<K> collection2) {

        if (collection1 == null && collection2 == null) {
            return true;
        }
        if (collection1 == null || collection2 == null || collection1.size() != collection2.size()) {
            return false;
        }
        boolean result = true;
        Iterator<K> ite2 = collection2.iterator();
        while (ite2.hasNext()) {
            if (!collection1.contains(ite2.next())) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * 过滤实现
     */
    public interface CollectionFilter<K> {
        /**
         * 过滤返回
         *
         * @return true    保留这条数据 false	过滤这条数据
         */
        boolean onFilter(K k);
    }

    /**
     * 过滤器
     *
     * @param filter 过滤实现
     * @return 过滤后的列表
     */
    public static <K> List<K> filter(List<K> list, CollectionFilter<K> filter) {
        List<K> c = new ArrayList<>();
        for (K k : list) {
            if (filter.onFilter(k)) {
                c.add(k);
            }
        }
        return c;
    }
}
