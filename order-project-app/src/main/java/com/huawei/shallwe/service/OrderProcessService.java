package com.huawei.shallwe.service;

import com.huawei.shallwe.api.dto.request.OrderProcessReq;

public interface OrderProcessService {

    void submitOrder(OrderProcessReq orderProcessReq);

    void submitOrderWhenThrow();
}
