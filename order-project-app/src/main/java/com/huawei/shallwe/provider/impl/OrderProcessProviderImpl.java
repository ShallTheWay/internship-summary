package com.huawei.shallwe.provider.impl;

import com.alibaba.fastjson.JSON;
import com.huawei.shallwe.api.dto.request.OrderProcessReq;
import com.huawei.shallwe.api.provider.OrderProcessProvider;
import com.huawei.shallwe.service.OrderProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@Slf4j
public class OrderProcessProviderImpl implements OrderProcessProvider {

    @Autowired
    private OrderProcessService orderProcessService;

    @Override
    public String submit(@RequestBody OrderProcessReq orderProcessReq) {
        log.info("submit -> orderProcessReq:{}", JSON.toJSONString(orderProcessReq));
        orderProcessService.submitOrder(orderProcessReq);
        return "Hello world!";
    }

    @Override
    public String submitWhenThrow() {
        orderProcessService.submitOrderWhenThrow();
        return "submitWhenThrow";
    }

}
