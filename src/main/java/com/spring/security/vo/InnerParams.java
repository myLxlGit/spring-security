package com.spring.security.vo;

import lombok.Data;
import lombok.NonNull;

import java.util.Objects;

/**
 * @author lxl
 * @date 2023/9/15 11:13
 */
@Data
public class InnerParams implements Comparable<InnerParams> {

    private String key;
    private String value;

    public InnerParams() {
    }

    public InnerParams(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int compareTo(@NonNull InnerParams o) {
        return key.compareTo(Objects.requireNonNull(o.getKey()));
    }

    @Override
    public String toString() {
        return key + value;
    }

    public String toUrlParam() {
        return key + "=" + value;
    }

}
