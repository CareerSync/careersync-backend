package com.example.demo.src.service;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.service.model.PostItemReq;
import com.example.demo.src.service.model.PostItemRes;
import com.example.demo.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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


}
