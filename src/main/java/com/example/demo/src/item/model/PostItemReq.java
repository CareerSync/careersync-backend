package com.example.demo.src.item.model;

import com.example.demo.src.item.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostItemReq {
    private String name;
    private int price;

    public Item toEntity() {
        return Item.builder()
                .name(name)
                .price(price)
                .build();
    }

}
