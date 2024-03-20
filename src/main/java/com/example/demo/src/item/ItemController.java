package com.example.demo.src.item;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.item.model.GetItemRes;
import com.example.demo.src.item.model.PatchItemReq;
import com.example.demo.src.item.model.PostItemReq;
import com.example.demo.src.item.model.PostItemRes;
import com.example.demo.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/items")
public class ItemController {

    private final ItemService itemService;
    private final MessageUtils messageUtils;

    /**
     * 상품 등록 API
     * [POST] /app/items
     *
     * @return BaseResponse<PostItemRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostItemRes> createItem(@RequestBody PostItemReq req) {
        PostItemRes itemRes = itemService.createItem(req);
        return new BaseResponse<>(itemRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 상품 조회 API
     * [GET] /app/items
     *
     * @return BaseResponse<List<GetItemRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetItemRes>> getItems() {
        List<GetItemRes> items = itemService.getItems();
        return new BaseResponse<>(items, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 상품 1개 조회 API
     * [GET] /app/items/:itemId
     *
     * @return BaseResponse<GetItemRes>
     */
    @ResponseBody
    @GetMapping("/{itemId}")
    public BaseResponse<GetItemRes> getItem(@PathVariable("itemId") Long itemId) {
        GetItemRes item = itemService.getItem(itemId);
        return new BaseResponse<>(item, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 상품 수정 API
     * [PATCH] /app/items/:itemId
     *
     * RequestBody : PatchItemReq
     * - name: 상품이름
     * - price: 상품가격
     *
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{itemId}")
    public BaseResponse<String> modifyItem(@PathVariable("itemId") Long itemId, @RequestBody PatchItemReq req) {
        itemService.modifyItem(itemId, req);
        return new BaseResponse<>(messageUtils.getMessage("MODIFY_ITEM_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 상품 삭제 API
     * [DELETE] /app/items/:itemId
     *
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/{itemId}")
    public BaseResponse<String> deleteItem(@PathVariable("itemId") Long itemId) {
        itemService.deleteItem(itemId);
        return new BaseResponse<>(messageUtils.getMessage("DELETE_ITEM_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }


}
