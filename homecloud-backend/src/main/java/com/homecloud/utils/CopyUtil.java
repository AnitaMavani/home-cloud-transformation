package com.homecloud.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class CopyUtil {

    /**
     * Copies properties from a list of source objects to a list of target objects.
     *
     * @param sList  The list of source objects.
     * @param classz The class type of the target objects.
     * @param <T>    The type of the target objects.
     * @param <S>    The type of the source objects.
     * @return A list of target objects with properties copied from the source objects.
     */
    public static <T, S> List<T> copyList(List<S> sList, Class<T> classz) {
        return sList.stream()
                .map(source -> copyProperties(source, classz))
                .collect(Collectors.toList());
    }

    private static <T, S> T copyProperties(S source, Class<T> classz) {
        try {
            T target = classz.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Error creating instance of class: " + classz.getName(), e);
        }
    }

}