package com.example.demo.src.subscription;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.subscription.model.PostSubscriptionReq;
import com.example.demo.src.subscription.model.PostSubscriptionRes;
import com.example.demo.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final MessageUtils messageUtils;

    /**
     *  구독 추가 API
     * [POST] /app/subscription
     * @return BaseResponse<PostSubscriptionRes>
     */
    @PostMapping("")
    public BaseResponse<PostSubscriptionRes> createSubscription(@RequestBody PostSubscriptionReq req) {
        PostSubscriptionRes subscriptionRes = subscriptionService.createSubscription(req);
        return new BaseResponse<>(subscriptionRes, messageUtils.getMessage("SUCCESS"));
    }

}
