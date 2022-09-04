package com.huawei.shallwe.service.impl;

import com.alibaba.fastjson.JSON;
import com.huawei.shallwe.anno.PcAnno;
import com.huawei.shallwe.api.dto.request.OrderProcessReq;
import com.huawei.shallwe.service.OrderProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service(value = "orderProcessService")
@Slf4j
@PcAnno("Hello everybody, this is Shallwe!")
public class OrderProcessServiceImpl implements OrderProcessService {


    @Override
    public void submitOrder(OrderProcessReq orderProcessReq) {
        log.info("orderProcessServiceImpl.submitOrder --> orderProcessReq:{}", JSON.toJSONString(orderProcessReq));
    }

    @Override
    public void submitOrderWhenThrow() {
        log.info("orderProcessServiceImpl.submitOrderWhenThrow");
        int i = 1 / 0;
    }
}
