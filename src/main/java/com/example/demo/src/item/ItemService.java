package com.example.demo.src.item;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.common.entity.BaseEntity.State;
import com.example.demo.common.exceptions.BaseException;
import com.example.demo.src.item.entity.Item;
import com.example.demo.src.item.model.GetItemRes;
import com.example.demo.src.item.model.PatchItemReq;
import com.example.demo.src.item.model.PostItemReq;
import com.example.demo.src.item.model.PostItemRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // GET
    public List<GetItemRes> getItems() {

        List<GetItemRes> items = itemRepository.findAll().stream()
                .map(GetItemRes::new)
                .collect(Collectors.toList());

        return items;
    }

    public GetItemRes getItem(Long itemId) {
        Item item = itemRepository.findByIdAndState(itemId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_ITEM));

        return new GetItemRes(item);
    }

    // PATCH
    public void modifyItem(Long itemId, PatchItemReq req) {
        Item item = itemRepository.findByIdAndState(itemId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_ITEM));

        String name = req.getName();
        int price = req.getPrice();

        item.updateName(name);
        item.updatePrice(price);
    }

    public void modifyItemState(Long itemId, State state) {
        Item item = itemRepository.findByIdAndState(itemId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_ITEM));
        item.updateState(state);
    }



    // DELETE
    public void deleteItem(Long itemId) {
        Item item = itemRepository.findByIdAndState(itemId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_ITEM));
        itemRepository.delete(item);
    }


}
