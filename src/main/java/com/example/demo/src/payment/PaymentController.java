package com.example.demo.src.payment;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.payment.model.*;
import com.example.demo.utils.MessageUtils;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.example.demo.src.payment.entity.Payment.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/app/payment")
public class PaymentController {

    private final PaymentService paymentService;
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
    public BaseResponse<PaymentRes> validateIamport(@RequestParam VerificationReq req) {
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
    public BaseResponse<List<GetPaymentRes>> cancelPayment(@RequestParam(name = "paymentState", required = false) PaymentState paymentState){

        if (paymentState == null) {
            List<GetPaymentRes> payments = paymentService.getPayments();
            return new BaseResponse<>(payments, messageUtils.getMessage("SUCCESS"));
        }

        List<GetPaymentRes> payments = paymentService.getPaymentsByState(paymentState);
        return new BaseResponse<>(payments, messageUtils.getMessage("SUCCESS"));
    }


}
