package com.huawei.shallwe.api.provider;

import com.huawei.shallwe.api.dto.request.OrderProcessReq;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order/process")
public interface OrderProcessProvider {

    /**
     *
     * @param orderProcessReq
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public String submit(OrderProcessReq orderProcessReq);

    @RequestMapping(value = "/submitWhenThrow", method = RequestMethod.GET)
    public String submitWhenThrow();

}
