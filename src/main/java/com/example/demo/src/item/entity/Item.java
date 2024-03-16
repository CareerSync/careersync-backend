package com.example.demo.src.item.entity;

import com.example.demo.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@JsonAutoDetect(fieldVisibility = ANY)
@Table(name = "TB_ITEM")
public class Item extends BaseEntity {

    @Id
    @Column(name = "itemId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false)
    private int price;

    @Builder
    public Item(Long id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

}
