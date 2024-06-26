package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity{
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    //상품 코드
    
    @Column(nullable = false, length = 50)
    private String itemNm;  //상품명
    
    @Column(name = "price", nullable = false)
    private int price;  //가격
    
    @Column(nullable = false)
    private int stockNumber;    //재고수량
    
    @Lob
    @Column(nullable = false)
    private String itemDetail;  //상품 상세 설명
    
    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;  //상품 판매 형태

    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    public void removeStock(int stockNumber, Item item){
        int restStock = this.stockNumber - stockNumber;
        if(restStock<=0){
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
        }
        if(restStock<0){
            throw new OutOfStockException("상품의 재고가 부족합니다.(현재 재고 수량: " + this.stockNumber + ")");
        }
        this.stockNumber = restStock;
    }

    public void addStock(int stockNumber, Item item){

        this.stockNumber += stockNumber;

        if(stockNumber>0){
            item.setItemSellStatus(ItemSellStatus.SELL);
        }
    }
}
