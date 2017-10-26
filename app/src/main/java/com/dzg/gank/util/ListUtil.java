package com.dzg.gank.util;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2017/4/18.
 */

public class ListUtil {
    public static List removeDuplicate(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }
}
