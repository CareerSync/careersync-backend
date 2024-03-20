package com.example.demo.src.subscription;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.subscription.model.GetSubscriptionRes;
import com.example.demo.src.subscription.model.PatchSubscriptionReq;
import com.example.demo.src.subscription.model.PostSubscriptionReq;
import com.example.demo.src.subscription.model.PostSubscriptionRes;
import com.example.demo.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     *  구독 내역 조회 API
     * [GET] /app/subscription
     * @return BaseResponse<PostSubscriptionRes>
     */
    @GetMapping("")
    public BaseResponse<List<GetSubscriptionRes>> getSubscriptions() {
        List<GetSubscriptionRes> subscriptions = subscriptionService.getSubscriptions();
        return new BaseResponse<>(subscriptions, messageUtils.getMessage("SUCCESS"));
    }

    /**
     *  구독 내역 수정 API
     * [PATCH] /app/subscription
     * ResponseBody: PatchSubscriptionReq
     * - nextPaymentDate: 다음 결제 일자
     *
     * @return BaseResponse<String>
     */
    @PatchMapping("/{subscriptionId}")
    public BaseResponse<String> modifySubscriptionNextPaymentDate(@PathVariable("subscriptionId") Long subscriptionId, @RequestBody PatchSubscriptionReq req) {
        subscriptionService.modifyNextSubscriptionDate(subscriptionId, req);
        String result = "구독 내역의 다음 결제 일자 수정 완료";
        return new BaseResponse<>(result, messageUtils.getMessage("SUCCESS"));
    }

    /**
     *  구독 내역 삭제 API
     * [DELETE] /app/subscription
     *
     * @return BaseResponse<String>
     */
    @DeleteMapping("/{subscriptionId}")
    public BaseResponse<String> deleteSubscription(@PathVariable("subscriptionId") Long subscriptionId) {
        subscriptionService.deleteSubscription(subscriptionId);
        String result = "구독 내역 삭제 완료";
        return new BaseResponse<>(result, messageUtils.getMessage("SUCCESS"));
    }

}
