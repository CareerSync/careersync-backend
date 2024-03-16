package com.example.demo.src.item;

import com.example.demo.src.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static com.example.demo.common.entity.BaseEntity.*;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByIdAndState(Long id, State state);
    Optional<Item> findByNameAndState(String name, State state);
}
