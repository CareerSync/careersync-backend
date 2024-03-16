package com.example.demo.src.payment;

import com.example.demo.common.exceptions.BaseException;
import com.example.demo.common.payment.IamportClientInitializer;
import com.example.demo.src.payment.model.*;
import com.example.demo.src.item.ItemRepository;
import com.example.demo.src.item.entity.Item;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.demo.common.entity.BaseEntity.State.*;
import static com.example.demo.common.response.BaseResponseStatus.*;
import static com.example.demo.src.payment.entity.Payment.*;
import static com.example.demo.src.payment.entity.Payment.PaymentState.*;
import static com.example.demo.src.payment.entity.Payment.PaymentState.SUCCESS;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentService {

    private IamportClient iamportClient;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final ItemRepository itemRepository;

    @Value("${spring.imp.api-key}")
    private String apiKey;

    @Value("${spring.imp.api-secret}")
    private String secretKey;

    @PostConstruct
    public void init() {
        IamportClientInitializer clientInitializer = new IamportClientInitializer(apiKey, secretKey);
        this.iamportClient = clientInitializer.initializeIamportClient();
    }

    // GET
    public void startPayment(HttpServletResponse response) throws IOException {

        // 회원이 아니라면 에러반환
//        Optional<User> findUser = userRepository.findById(req.getUserId());
//        if (!findUser.isPresent()) {
//            throw new BaseException(NOT_FIND_USER);
//        }
//
//        // 등록된 상품이 아니라면 에러반환
//        Optional<Item> findItem = itemRepository.findById(req.getItemId());
//        if (!findItem.isPresent()) {
//            throw new BaseException(NOT_FIND_ITEM);
//        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        ResponseCookie cookie = ResponseCookie.from("Lax", "Lax")
                .path("/app/payment/startPayment")
                .sameSite("None")
                .secure(true)
                .domain("gridgetest-server.shop")
                .build();

        response.addHeader("Set-Cookie",cookie.toString());

        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <!-- jQuery -->\n" +
                "    <meta http-equiv=\"Content-Security-Policy\" content=\"upgrade-insecure-requests\">\n" +
                "    <meta name=\"_csrf\" id=\"_csrf\" content=\"${_csrf.token}\">\n" +
                "    <meta name=\"_csrf_header\" id=\"_csrf_header\" content=\"${_csrf.headerName}\">\n" +
                "    <script\n" +
                "            type=\"text/javascript\"\n" +
                "            src=\"https://code.jquery.com/jquery-1.12.4.min.js\"\n" +
                "    ></script>\n" +
                "    <!-- iamport.payment.js -->\n" +
                "    <script\n" +
                "            type=\"text/javascript\"\n" +
                "            src=\"https://cdn.iamport.kr/js/iamport.payment-1.2.0.js\"\n" +
                "    ></script>\n" +
                "    <script>\n" +
                "        var IMP = window.IMP;\n" +
                "        var header = $(\"meta[name='_csrf_header']\").attr('content');\n" +
                "        var token = $(\"meta[name='_csrf']\").attr('content');\n" +
                "        IMP.init('imp57126857');\n" +
                "\n" +
                "        function requestPay() {\n" +
                "            IMP.request_pay(\n" +
                "                {\n" +
                "                    pg: \"html5_inicis\",\t\t//KG이니시스 pg파라미터 값\n" +
                "                    pay_method: \"card\",\t\t//결제 방법\n" +
                "                    merchant_uid: 'merchant_' + new Date().getTime(), //주문번호\n" +
                "                    name: '당근',\t\t//상품 명\n" +
                "                    amount: 300,\t\t\t//금액\n" +
                "         \t\t\t\tbuyer_email: \"shinsj4653@gmail.com\",\n" +
                "      \t\t\t\tbuyer_name: \"홍길동\",\n" +
                "      \t\t\t\tbuyer_tel: \"010-4242-4242\",\n" +
                "      \t\t\t\tbuyer_addr: \"서울특별시 강남구 신사동\",\n" +
                "      \t\t\t\tbuyer_postcode: \"01181\"\n" +
                "     \t\n" +
                "                },\n" +
                "                function (rsp) {\n" +
                "      \t\t\t\t//rsp.imp_uid 값으로 결제 단건조회 API를 호출하여 결제결과를 판단합니다.\n" +
                "                    if (rsp.success) {\n" +
                "                        $.ajax({\n" +
                "                            url: \"https://gridgetest-server.shop/app/payment/validate\", \n" +
                "                            beforeSend: function(xhr){ \n" +
                "                               xhr.setRequestHeader(header, token); \n" +
                "                            }, \n" +
                "                            method: \"POST\",\n" +
                "                            contentType: \"application/json\",\n" +
                "                            dataType: \"json\",\n" +
                "                            data: JSON.stringify({\n" +
                "                                impUid: rsp.imp_uid,            // 결제 고유번호\n" +
                "                                merchantUid: rsp.merchant_uid,   // 주문번호\n" +
                "                                name: rsp.name,   // 상품 이름\n" +
                "                                buyerEmail: rsp.buyer_email,   // 유저 이메일\n" +
                "                                amount: rsp.paid_amount // 상품 가격\n" +
                "                            }),\n" +
                "                        }).done(function (data) {\n" +
                "                            // 가맹점 서버 결제 API 성공시 로직\n" +
                "                        alert(\"결제에 성공하였습니다. 에러 내용: \" + data.imp_uid);\n" +
                "                        })\n" +
                "                    } else {\n" +
                "                        alert(\"결제에 실패하였습니다. 에러 내용: \" + rsp.error_msg);\n" +
                "                    }\n" +
                "                }\n" +
                "            );\n" +
                "        }\n" +
                "    </script>\n" +
                "    <meta charset=\"UTF-8\"/>\n" +
                "    <title>Sample Payment</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<button onclick=\"requestPay()\">paymentTest</button>\n" +
                "<!-- 결제하기 버튼 생성 -->\n" +
                "</body>\n" +
                "</html>");
        out.flush();


    }

    @Transactional(readOnly = true)
    public List<GetPaymentRes> getPayments() {
        List<GetPaymentRes> paymentResList = paymentRepository.findAll().stream()
                .map(GetPaymentRes::new)
                .collect(Collectors.toList());

        return paymentResList;
    }

    @Transactional(readOnly = true)
    public List<GetPaymentRes> getPaymentsByState(PaymentState paymentState) {
        if (paymentState.equals(SUCCESS)) {
            return paymentRepository.findAllByPaymentState(SUCCESS).stream()
                    .map(GetPaymentRes::new)
                    .collect(Collectors.toList());
        }
        else if(paymentState.equals(FAIL)) {
            return paymentRepository.findAllByPaymentState(FAIL).stream()
                    .map(GetPaymentRes::new)
                    .collect(Collectors.toList());
        } else {
            throw new BaseException(PAYMENT_TYPE_ERROR);
        }
    }


    // POST
    @Transactional(noRollbackFor = BaseException.class)
    public PaymentRes validateIamport(VerificationReq req) {
        log.info("validateIamport start!");

        String impUid = req.getImpUid();
        String merchantUid = req.getMerchantUid();
        int amount = req.getAmount(); // 사용자가 결제한 금액
        String name = req.getName(); // 상품 이름
        String buyerEmail = req.getBuyerEmail(); // 회원 이메일

        Optional<User> findUser = userRepository.findByEmailAndState(buyerEmail, ACTIVE);

        if (!findUser.isPresent()) {
            log.info("findUser not found");

            throw new BaseException(NOT_FIND_USER);
        }

        Optional<Item> findItem = itemRepository.findByNameAndState(name, ACTIVE);

        if (!findItem.isPresent()) {
            log.info("findItem not found");

            throw new BaseException(NOT_FIND_ITEM);
        }

        // 실제 db에 저장되어 있는 유저랑 상품 정보
        User user = findUser.get();
        Item item = findItem.get();

        // 이미 등록된 결제 고유 번호면 결제 실패
        Optional<com.example.demo.src.payment.entity.Payment> findPaymentByImpUid = paymentRepository.findByImpUidAndState(impUid, ACTIVE);

        if (findPaymentByImpUid.isPresent()) {
            // 결제 실패 기록 남기기
            savePayment(user, item, impUid, merchantUid, FAIL);
            // 결제 실패 했으므로, 전액 환불
            cancelReservationWithFullRefund(impUid);
            throw new BaseException(DUPLICATED_IMP_UID);
        }

        // 이미 등록된 주문 번호면 결제 실패
        Optional<com.example.demo.src.payment.entity.Payment> findPaymentByMerchantUid = paymentRepository.findByMerchantUidAndState(merchantUid, ACTIVE);

        if (findPaymentByMerchantUid.isPresent()){
            // 결제 실패 기록 남기기
            savePayment(user, item, impUid, merchantUid, FAIL);
            // 결제 실패 했으므로, 전액 환불
            cancelReservationWithFullRefund(impUid);
            throw new BaseException(DUPLICATED_MERCHANT_UID);
        }

        // db에 저장된 상품 금액과 사용자가 결제한 금액이 같은지 확인
        // 금액이 다르면 결제 취소
        int itemDBPrice = findItem.get().getPrice();

        if (itemDBPrice != amount) {
            // 결제 실패 기록 남기기
            savePayment(user, item, impUid, merchantUid, FAIL);

            // 결제 실패 했으므로, 전액 환불
            cancelReservationWithFullRefund(impUid);
            throw new BaseException(PAYMENT_PRICE_ERROR);
        }

        // 결제 성공 기록 남기기
        log.info("결제 성공!");
        return savePayment(user, item, impUid, merchantUid, SUCCESS);

    }

    public PaymentRes savePayment(User user, Item item, String impUid, String merchantUid,
                                  PaymentState paymentState) {
        com.example.demo.src.payment.entity.Payment payment = builder()
                .user(user)
                .item(item)
                .impUid(impUid)
                .merchantUid(merchantUid)
                .paymentState(paymentState)
                .build();

        com.example.demo.src.payment.entity.Payment savePayment = paymentRepository.save(payment);
        log.info("savePayment id : {}", savePayment.getId());
        return new PaymentRes(savePayment.getId());
    }

    public void cancelReservationWithFullRefund(String impUid){
        log.info("cancelReservationWithFullRefund");
        IamportResponse<Payment> response = iamportClient.paymentByImpUid(impUid);
        //cancelData 생성
        CancelData cancelData = createCancelData(response, 0);
        //결제 취소
        iamportClient.cancelPaymentByImpUid(cancelData);
    }

    public IamportResponse<Payment> cancelReservation(CancelReq cancelReq){
        IamportResponse<Payment> response = iamportClient.paymentByImpUid(cancelReq.getImpUid());
        //cancelData 생성
        CancelData cancelData = createCancelData(response, cancelReq.getRefundAmount());
        //결제 취소
        IamportResponse<Payment> paymentCancelRes = iamportClient.cancelPaymentByImpUid(cancelData);

        Optional<com.example.demo.src.payment.entity.Payment> findPayment = paymentRepository.findByImpUidAndState(cancelReq.getImpUid(), ACTIVE);

        // 취소한 결제 -> 상태 값 INACTIVE로 업데이트
        if (findPayment.isPresent()) {
            com.example.demo.src.payment.entity.Payment payment = findPayment.get();
            payment.updateState(INACTIVE);
        }

        return paymentCancelRes;
    }

    private CancelData createCancelData(IamportResponse<Payment> response, int refundAmount) {
        if (refundAmount == 0) { //전액 환불일 경우
            return new CancelData(response.getResponse().getImpUid(), true);
        }
        //부분 환불일 경우 checksum을 입력해 준다.
        return new CancelData(response.getResponse().getImpUid(), true, new BigDecimal(refundAmount));
    }
}
