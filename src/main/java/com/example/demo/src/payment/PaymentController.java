package com.example.demo.src.payment;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.payment.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.MessageUtils;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.GET;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.example.demo.src.payment.entity.Payment.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/app/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final JwtService jwtService;
    private final MessageUtils messageUtils;

    /**
     *  테스트 결제 시작 API
     * [GET] /app/payment/startPayment
     * @return html
     */
    @GetMapping("/startPayment")
    public void startPayment(HttpServletResponse response) throws IOException {
        paymentService.startPayment(response);
    }

    /**
     *  결제 내역 검증 API
     * [POST] /app/payment/validate
     * @return BaseResponse<PaymentRes>
     */
    @PostMapping("/validate")
    public BaseResponse<PaymentRes> validateIamport(@RequestBody VerificationReq req) {
        PaymentRes paymentRes = paymentService.validateIamport(req);
        return new BaseResponse<>(paymentRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     *  결제 내역 취소 API
     * [POST] /app/payment/cancel
     * @return BaseResponse<IamportResponse<Payment>>
     */
    @PostMapping("/cancel")
    public BaseResponse<IamportResponse<Payment>> cancelPayment(@RequestBody CancelReq cancelReq){
        jwtService.getUserId();
        IamportResponse<Payment> cancelResponse = paymentService.cancelReservation(cancelReq);
        return new BaseResponse<>(cancelResponse, messageUtils.getMessage("SUCCESS"));
    }

    /**
     *  결제 내역 조회 API
     * [GET] /app/payment? paymentState
     * RequestParam
     * - SUCCESS : 성공한 결제 로그만 보여줌
     * - FAIL : 실패한 결제 로그만 보여줌
     * - 없을 경우, 모든 결제 로그 조회
     * @return BaseResponse<List<GetPayment>>
     */
    @GetMapping("")
    public BaseResponse<List<GetPaymentRes>> getPayments(@RequestParam(name = "paymentState", required = false) PaymentState paymentState){

        jwtService.getUserId();

        if (paymentState == null) {
            List<GetPaymentRes> payments = paymentService.getPayments();
            return new BaseResponse<>(payments, messageUtils.getMessage("SUCCESS"));
        }

        List<GetPaymentRes> payments = paymentService.getPaymentsByState(paymentState);
        return new BaseResponse<>(payments, messageUtils.getMessage("SUCCESS"));
    }

    /**
     *  결제 내역 수정 API
     * [PATCH] /app/payment/:paymentId
     * RequestBody
     * PatchPaymentReq
     * - merchantUid: 주문번호
     * @return BaseResponse<String>
     */
    @PatchMapping("/{paymentId}")
    public BaseResponse<String> modifyPaymentMerchantUid(@PathVariable("paymentId") Long paymentId, @RequestBody PatchPaymentReq req){
        jwtService.getUserId();
        paymentService.modifyPaymentMerchantUid(paymentId, req);
        return new BaseResponse<>(messageUtils.getMessage("MODIFY_PAYMENT_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

    /**
     *  결제 내역 상태 수정 API
     * [PATCH] /app/payment?state=
     *
     * @return BaseResponse<String>
     */
    @PatchMapping("/{paymentId}/state")
    public BaseResponse<String> modifyPaymentState(@PathVariable("paymentId") Long paymentId, @RequestParam("state") State state){
        jwtService.getUserId();
        paymentService.modifyPaymentState(paymentId, state);
        return new BaseResponse<>(messageUtils.getMessage("MODIFY_PAYMENT_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

    /**
     *  결제 내역 삭제 API
     * [DELETE] /app/payment/:paymentId
     * @return BaseResponse<String>
     */
    @DeleteMapping("/{paymentId}")
    public BaseResponse<String> deletePayment(@PathVariable("paymentId") Long paymentId){
        jwtService.getUserId();
        paymentService.deletePayment(paymentId);
        return new BaseResponse<>(messageUtils.getMessage("DELETE_PAYMENT_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

}
