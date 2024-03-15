package com.example.demo.src.payment;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.payment.model.CancelReq;
import com.example.demo.src.payment.model.PaymentReq;
import com.example.demo.src.payment.model.PaymentRes;
import com.example.demo.src.payment.model.VerificationReq;
import com.example.demo.utils.MessageUtils;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        IamportResponse<Payment> cancelResponse = paymentService.cancelReservation(cancelReq);
        return new BaseResponse<>(cancelResponse, messageUtils.getMessage("SUCCESS"));
    }

}
