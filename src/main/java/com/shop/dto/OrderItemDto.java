package com.shop.dto;

import com.shop.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderItemDto {
    public OrderItemDto(OrderItem orderItem, String imgUrl){
        this.itemNm = orderItem.getItem().getItemNm();
        this.count = orderItem.getCount();
        this.imgUrl = imgUrl;
        this.orderPrice = orderItem.getOrderPrice();
    }

    private String itemNm;
    private int count;
    private int orderPrice;
    private String imgUrl;

}
