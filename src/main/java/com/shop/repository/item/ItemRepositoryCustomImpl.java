package com.shop.repository.item;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;

public class ItemRepositoryCustomImpl {

    //private final QItem qItem;
    //@Override
//    public void queryDslTest(){
//        this.createItemList();
//        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
//        QItem qItem = QItem.item;
//        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
//                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
//                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
//                .orderBy(qItem.price.desc());
}
