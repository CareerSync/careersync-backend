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
public class GetItemRes {
    private Long id;
    private String name;
    private int price;

    public GetItemRes(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.price = item.getPrice();
    }
}
