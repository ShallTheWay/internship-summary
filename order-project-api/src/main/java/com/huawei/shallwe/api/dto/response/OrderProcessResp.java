package com.huawei.shallwe.api.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderProcessResp {

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("data")
    private Object data;

    @JsonProperty("date")
    @DateTimeFormat     // 反序列化
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")     // 序列化
    private Date date;


}
