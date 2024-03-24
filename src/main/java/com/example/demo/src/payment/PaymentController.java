package com.example.demo.src.payment;

import com.example.demo.common.exceptions.BaseException;
import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.payment.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.MessageUtils;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.GET;
import retrofit2.http.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.example.demo.common.response.BaseResponseStatus.INVALID_STATE;
import static com.example.demo.src.payment.entity.Payment.*;
@Slf4j
@Tag(name = "payment 도메인", description = "결제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/app/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final JwtService jwtService;
    private final MessageUtils messageUtils;

    /**
     *  테스트 결제 시작 API
     * [GET] /app/payments/startPayment/{:itemId}
     * @return html
     */
    @Operation(summary = "결제 화면", description = "브라우저에서 해당 url로 접속 시, paymentStart 버튼이 보이는 페이지를 조회합니다. 입력된 userId값에 해당하는 유저가 itemId값에 해당하는 상품을 결제 시도합니다.")
    @GetMapping("/startPayment")
    public void startPayment(@RequestParam("userId") Long userId, @RequestParam("itemId") Long itemId, HttpServletResponse response) throws IOException {
        paymentService.startPayment(userId, itemId, response);
    }

    /**
     *  결제 내역 검증 API
     * [POST] /app/payments/validate
     * @return BaseResponse<PaymentRes>
     */
    @Operation(summary = "결제 내역 검증", description = "결제 페이지에서 결제 완료 후, 해당 결제 내역을 검증합니다.")
    @PostMapping("/validate")
    public BaseResponse<PaymentRes> validateIamport(@RequestBody VerificationReq req) {
        PaymentRes paymentRes = paymentService.validateIamport(req);
        return new BaseResponse<>(paymentRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     *  결제 내역 취소 API
     * [POST] /app/payments/cancel
     * @return BaseResponse<IamportResponse<Payment>>
     */
    @Operation(summary = "결제 내역 취소", description = "결제된 내역에 대해 환불 신청을 합니다. refundAmount값이 0이면 모든 금액을 환불하고, 그렇지 않다면 입력된 값만큼 부분 환불을 진행합니다.")
    @PostMapping("/cancel")
    public BaseResponse<IamportResponse<Payment>> cancelPayment(@RequestBody CancelReq cancelReq){
        jwtService.getUserId();
        IamportResponse<Payment> cancelResponse = paymentService.cancelReservation(cancelReq);
        return new BaseResponse<>(cancelResponse, messageUtils.getMessage("SUCCESS"));
    }

    /**
     *  결제 내역 조회 API
     * [GET] /app/payments? paymentState
     * RequestParam
     * - SUCCESS : 성공한 결제 로그만 보여줌
     * - FAIL : 실패한 결제 로그만 보여줌
     * - 없을 경우, 모든 결제 로그 조회
     * @return BaseResponse<List<GetPayment>>
     */
    @Operation(summary = "결제 내역 조회", description = "로그인한 유저의 결제 내역을 조회합니다. paymentState값이 없다면 모든 내역을 조회하고, 있다면 성공 혹은 실패한 내역만 조회합니다.")
    @GetMapping("")
    public BaseResponse<List<GetPaymentRes>> getPayments(@RequestParam(name = "paymentState", required = false) PaymentState paymentState){

        Long userId = jwtService.getUserId();

        if (paymentState == null) {
            List<GetPaymentRes> payments = paymentService.getPayments(userId);
            return new BaseResponse<>(payments, messageUtils.getMessage("SUCCESS"));
        }

        List<GetPaymentRes> payments = paymentService.getPaymentsByState(userId, paymentState);
        return new BaseResponse<>(payments, messageUtils.getMessage("SUCCESS"));
    }

    /**
     *  결제 내역 1개 조회 API
     * [GET] /app/payments/:paymentId
     * RequestParam
     *
     * @return BaseResponse<GetPaymentRes>
     */
    @Operation(summary = "결제 내역 1개 조회", description = "입력된 paymentId값에 해당하는 결제 내역을 조회합니다.")
    @GetMapping("/{paymentId}")
    public BaseResponse<GetPaymentRes> getPayment(@PathVariable("paymentId") Long paymentId){

        jwtService.getUserId();
        GetPaymentRes payment = paymentService.getPayment(paymentId);

        return new BaseResponse<>(payment, messageUtils.getMessage("SUCCESS"));
    }

    /**
     *  결제 내역 수정 API
     * [PATCH] /app/payments/:paymentId
     * RequestBody
     * PatchPaymentReq
     * - merchantUid: 주문번호
     * @return BaseResponse<String>
     */
    @Operation(summary = "결제 내역 주문 번호 수정", description = "입력된 paymentId값에 해당하는 결제 내역의 주문 번호를 수정합니다.")
    @PatchMapping("/{paymentId}")
    public BaseResponse<String> modifyPaymentMerchantUid(@PathVariable("paymentId") Long paymentId, @RequestBody PatchPaymentReq req){
        jwtService.getUserId();
        paymentService.modifyPaymentMerchantUid(paymentId, req);
        return new BaseResponse<>(messageUtils.getMessage("MODIFY_PAYMENT_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

    /**
     *  결제 내역 상태 수정 API
     * [PATCH] /app/payments/:paymentId ?state=
     *
     * @return BaseResponse<String>
     */
    @Operation(summary = "결제 내역 상태 수정", description = "입력된 paymentId값에 해당하는 결제 내역의 상태값을 수정합니다.")
    @PatchMapping("/{paymentId}/state")
    public BaseResponse<String> modifyPaymentState(@PathVariable("paymentId") Long paymentId, @RequestParam("state") String state){

        if (!state.equals("ACTIVE") && !state.equals("INACTIVE")) {
            throw new BaseException(INVALID_STATE, messageUtils.getMessage("INVALID_STATE"));
        }

        jwtService.getUserId();
        paymentService.modifyPaymentState(paymentId, State.valueOf(state.toUpperCase()));
        return new BaseResponse<>(messageUtils.getMessage("MODIFY_PAYMENT_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

    /**
     *  결제 내역 삭제 API
     * [DELETE] /app/payments/:paymentId
     * @return BaseResponse<String>
     */
    @Operation(summary = "결제 내역 삭제", description = "입력된 paymentId값에 해당하는 결제 내역을 삭제합니다.")
    @DeleteMapping("/{paymentId}")
    public BaseResponse<String> deletePayment(@PathVariable("paymentId") Long paymentId){
        jwtService.getUserId();
        paymentService.deletePayment(paymentId);
        return new BaseResponse<>(messageUtils.getMessage("DELETE_PAYMENT_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

}
