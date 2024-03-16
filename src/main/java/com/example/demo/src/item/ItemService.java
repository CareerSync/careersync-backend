package com.example.demo.src.item;

import com.example.demo.common.exceptions.BaseException;
import com.example.demo.src.item.entity.Item;
import com.example.demo.src.item.model.PostItemReq;
import com.example.demo.src.item.model.PostItemRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.demo.common.entity.BaseEntity.State.*;
import static com.example.demo.common.response.BaseResponseStatus.*;

@Transactional
@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemRepository itemRepository;

    // POST
    public PostItemRes createItem(PostItemReq req) {

        Optional<Item> findItem = itemRepository.findByNameAndState(req.getName(), ACTIVE);

        if (findItem.isPresent()) {
            throw new BaseException(DUPLICATED_ITEM);
        }

        Item item = itemRepository.save(req.toEntity());
        return new PostItemRes(item.getId(), item.getName(), item.getPrice());
    }

}
