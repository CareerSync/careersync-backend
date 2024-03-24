package com.example.demo.src.item;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.common.exceptions.BaseException;
import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.item.model.GetItemRes;
import com.example.demo.src.item.model.PatchItemReq;
import com.example.demo.src.item.model.PostItemReq;
import com.example.demo.src.item.model.PostItemRes;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.common.entity.BaseEntity.*;
import static com.example.demo.common.response.BaseResponseStatus.INVALID_STATE;

@Slf4j
@Tag(name = "item 도메인", description = "구독 상품 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/items")
public class ItemController {

    private final ItemService itemService;
    private final JwtService jwtService;
    private final MessageUtils messageUtils;

    /**
     * 상품 등록 API
     * [POST] /app/items
     *
     * @return BaseResponse<PostItemRes>
     */
    @Operation(summary = "상품 등록", description = "입력된 상품 등록 요청에 따라 상품을 등록합니다.")
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostItemRes> createItem(@RequestBody PostItemReq req) {
        jwtService.getUserId();
        PostItemRes itemRes = itemService.createItem(req);
        return new BaseResponse<>(itemRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 상품 조회 API
     * [GET] /app/items
     *
     * @return BaseResponse<List<GetItemRes>>
     */
    @Operation(summary = "상품 조회", description = "등록된 모든 상품을 조회합니다.")
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetItemRes>> getItems() {
        jwtService.getUserId();
        List<GetItemRes> items = itemService.getItems();
        return new BaseResponse<>(items, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 상품 1개 조회 API
     * [GET] /app/items/:itemId
     *
     * @return BaseResponse<GetItemRes>
     */
    @Operation(summary = "상품 1개 조회", description = "입력된 itemId값에 해당하는 상품을 조회합니다.")
    @ResponseBody
    @GetMapping("/{itemId}")
    public BaseResponse<GetItemRes> getItem(@PathVariable("itemId") Long itemId) {
        jwtService.getUserId();
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
    @Operation(summary = "상품 정보 수정", description = "기존 상품의 이름 혹은 가격을 수정합니다.")
    @ResponseBody
    @PatchMapping("/{itemId}")
    public BaseResponse<String> modifyItem(@PathVariable("itemId") Long itemId, @RequestBody PatchItemReq req) {
        jwtService.getUserId();
        itemService.modifyItem(itemId, req);
        return new BaseResponse<>(messageUtils.getMessage("MODIFY_ITEM_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 상품 수정 API
     * [PATCH] /app/items? state=
     *
     * @return BaseResponse<String>
     */
    @Operation(summary = "상품 상태 수정", description = "입력된 상태값에 따라 기존 상품의 상태값을 수정합니다.")
    @ResponseBody
    @PatchMapping("/{itemId}/state")
    public BaseResponse<String> modifyItemState(@PathVariable("itemId") Long itemId, @RequestParam("state") String state) {

        if (!state.equals("ACTIVE") && !state.equals("INACTIVE")) {
            throw new BaseException(INVALID_STATE, messageUtils.getMessage("INVALID_STATE"));
        }

        jwtService.getUserId();
        itemService.modifyItemState(itemId, State.valueOf(state.toUpperCase()));
        return new BaseResponse<>(messageUtils.getMessage("MODIFY_ITEM_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }


    /**
     * 상품 삭제 API
     * [DELETE] /app/items/:itemId
     *
     * @return BaseResponse<String>
     */
    @Operation(summary = "상품 삭제", description = "입력된 itemId값에 해당하는 상품을 삭제합니다.")
    @ResponseBody
    @DeleteMapping("/{itemId}")
    public BaseResponse<String> deleteItem(@PathVariable("itemId") Long itemId) {
        jwtService.getUserId();
        itemService.deleteItem(itemId);
        return new BaseResponse<>(messageUtils.getMessage("DELETE_ITEM_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }


}
