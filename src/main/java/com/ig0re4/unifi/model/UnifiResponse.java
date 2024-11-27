package com.ig0re4.unifi.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UnifiResponse<T> {
    private UnifiMetadata meta;
    private List<T> data;
}
