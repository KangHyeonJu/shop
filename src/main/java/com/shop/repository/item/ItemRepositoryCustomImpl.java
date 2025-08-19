package com.shop.repository.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import com.shop.entity.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.shop.entity.QItem.item;
import static com.shop.entity.QItemImg.itemImg;

@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    //    public ItemRepositoryCustomImpl(JPAQueryFactory queryFactory){
//        this.queryFactory = queryFactory;
//    }
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ? null : item.itemSellStatus.eq(searchSellStatus);
    }

    private BooleanExpression regDtsAfter(String searchDateType){
        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        }else if (StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        }else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        }else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        }else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }
        return item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if(StringUtils.equals("itemNm", searchBy)){
            return item.itemNm.like("%" + searchQuery + "%");
        }else if(StringUtils.equals("createdBy", searchBy)){
            return item.createdBy.like("%" + searchQuery + "%");
        }
        return null;
    }

    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : item.itemNm.like("%" + searchQuery + "%");
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        List<Item> content = queryFactory.selectFrom(item)
                                                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                                                       searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                                                       searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())).orderBy(item.id.desc())
                                                        .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();


        Long totalCount = queryFactory.select(Wildcard.count).from(item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne();

        Optional<Long> total = Optional.ofNullable(totalCount);

        return new PageImpl<>(content, pageable, total.orElse(0L));
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        List<MainItemDto> content = queryFactory
                .select(
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price
                        )
                ).from(itemImg)
                .innerJoin(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(searchByLike("itemNm",  itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .innerJoin(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(searchByLike("itemNm", itemSearchDto.getSearchQuery()))
                .fetchOne();

        Optional<Long> total = Optional.ofNullable(totalCount);

        return new PageImpl<>(content, pageable, total.orElse(0L));
    }
}
