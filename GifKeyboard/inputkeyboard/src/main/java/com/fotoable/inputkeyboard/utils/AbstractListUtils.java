/*
 * Copyright (c) 2017. @author ouyang copyright@fotoable Inc.  Anyone can copy this
 */

package com.fotoable.inputkeyboard.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class AbstractListUtils {
    @NonNull
    public static <T> List<T> shuffle(@Nullable List<T> list) {
        if (isEmpty((List) list)) {
            return Collections.emptyList();
        }
        List<T> items = new ArrayList();
        Random random = new Random();
        while (!isEmpty((List) list)) {
            items.add(list.remove(random.nextInt(list.size())));
        }
        return items;
    }

    @NonNull
    public static List<String> orJoinString(List<String> base, List<String> items) {
        if (base == null && items == null) {
            return new ArrayList();
        }
        if (base == null) {
            return items;
        }
        if (items == null) {
            return base;
        }
        Set<String> set = new HashSet();
        for (String str : base) {
            set.add(str);
        }
        for (String str2 : items) {
            if (!set.contains(str2)) {
                set.add(str2);
                base.add(str2);
            }
        }
        return base;
    }

//    @NonNull
//    public static <T extends Gif> List<T> orJoinGif(List<T> base, List<T> items) {
//        if (base == null && items == null) {
//            return new ArrayList();
//        }
//        if (base == null) {
//            return items;
//        }
//        if (items == null) {
//            return base;
//        }
//        Set<String> set = new HashSet();
//        for (T t : base) {
//            set.add(t.getId());
//        }
//        for (T t2 : items) {
//            if (!set.contains(t2.getId())) {
//                set.add(t2.getId());
//                base.add(t2);
//            }
//        }
//        return base;
//    }

    public static <T> boolean isEmpty(@Nullable List<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> boolean isEmpty(@Nullable Set<T> set) {
        return set == null || set.isEmpty();
    }

    public static <T> boolean hasOnlyOneItem(@Nullable List<T> list) {
        return !isEmpty((List) list) && list.size() == 1;
    }

    public static <T> List<List<T>> splits(@Nullable List<T> list, int limit) {
        List<List<T>> result = new ArrayList();
        if (!isEmpty((List) list)) {
            if (limit < 2) {
                result.add(list);
            } else {
                List<T> row = new ArrayList();
                for (T item : list) {
                    row.add(item);
                    if (row.size() == limit) {
                        result.add(row);
                        row = new ArrayList();
                    }
                }
                if (!isEmpty((List) row)) {
                    result.add(row);
                }
            }
        }
        return result;
    }

//    @NonNull
//    public static <T extends ICollection> List<T> orJoinCollection(List<T> base, List<T> items) {
//        if (base == null && items == null) {
//            return new ArrayList();
//        }
//        if (base == null) {
//            return items;
//        }
//        if (items == null) {
//            return base;
//        }
//        Set<String> set = new HashSet();
//        for (T t : base) {
//            set.add(t.getName());
//        }
//        for (T t2 : items) {
//            if (!set.contains(t2.getName())) {
//                set.add(t2.getName());
//                base.add(t2);
//            }
//        }
//        return base;
//    }
}