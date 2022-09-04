package com.huawei.shallwe.api.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderProcessReq {

    @JsonProperty("order_id")
    @JsonAlias(value = {"orderId", "order_id", "orderid"})
    private String orderId;

    @JsonProperty("data")
    private Object data;

    @JsonProperty("date")
    @DateTimeFormat     // 反序列化
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")     // 序列化
    private Date date;

}
