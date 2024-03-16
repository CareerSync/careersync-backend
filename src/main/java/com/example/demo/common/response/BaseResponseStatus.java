package com.example.demo.common.response;

import com.example.demo.utils.MessageUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 200 : 요청 성공
     */
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공하였습니다."),


    /**
     * 400 : Request, Response 오류
     */

    USERS_EMPTY_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "이메일을 입력해주세요."),
    TEST_EMPTY_COMMENT(false, HttpStatus.BAD_REQUEST.value(), "코멘트를 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,HttpStatus.BAD_REQUEST.value(),"중복된 이메일입니다."),
    POST_TEST_EXISTS_MEMO(false,HttpStatus.BAD_REQUEST.value(),"중복된 메모입니다."),
    POST_REPORT_EXISTS_USER_AND_POST(false,HttpStatus.BAD_REQUEST.value(),"중복된 신고 내역입니다."),
    REVTYPE_ERROR(false,HttpStatus.BAD_REQUEST.value(),"잘못된 revision type 값입니다."),
    PAYMENT_TYPE_ERROR(false,HttpStatus.BAD_REQUEST.value(),"잘못된 payment type 값입니다."),
    PAYMENT_PRICE_ERROR(false,HttpStatus.BAD_REQUEST.value(),"결제한 금액과 저장된 상품 금액이 다릅니다."),
    SUBSCRIPTION_ERROR(false,HttpStatus.BAD_REQUEST.value(),"결제 실패 혹은 이미 환불된 결제 내역의 상품을 구독하려 합니다."),

    RESPONSE_ERROR(false, HttpStatus.NOT_FOUND.value(), "값을 불러오는데 실패하였습니다."),

    DUPLICATED_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "중복된 이메일입니다."),
    DUPLICATED_ITEM(false, HttpStatus.BAD_REQUEST.value(), "중복된 상품입니다."),
    DUPLICATED_IMP_UID(false, HttpStatus.BAD_REQUEST.value(), "중복된 아임포트 고유번호입니다."),
    DUPLICATED_MERCHANT_UID(false, HttpStatus.BAD_REQUEST.value(), "중복된 거래 고유번호입니다."),
    DUPLICATED_SUBSCRIPTION(false, HttpStatus.BAD_REQUEST.value(), "중복된 유저 혹은 상품입니다."),
    INVALID_MEMO(false,HttpStatus.NOT_FOUND.value(), "존재하지 않는 메모입니다."),
    INVALID_USER(false, HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다."),
    INVALID_POST(false, HttpStatus.NOT_FOUND.value(), "존재하지 않는 게시글입니다."),
    INVALID_PAYMENT_USER(false, HttpStatus.NOT_FOUND.value(), "결제 내역에 존재하지 않는 유저입니다."),
    INVALID_PAYMENT_ITEM(false, HttpStatus.NOT_FOUND.value(), "결제 내역에 존재하지 않는 상품입니다."),
    FAILED_TO_LOGIN(false,HttpStatus.NOT_FOUND.value(),"없는 아이디거나 비밀번호가 틀렸습니다."),
    EMPTY_JWT(false, HttpStatus.UNAUTHORIZED.value(), "JWT를 입력해주세요."),
    INVALID_JWT(false, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,HttpStatus.FORBIDDEN.value(),"권한이 없는 유저의 접근입니다."),
    NOT_FIND_USER(false,HttpStatus.NOT_FOUND.value(),"일치하는 유저가 없습니다."),
    NOT_FIND_POST(false,HttpStatus.NOT_FOUND.value(),"일치하는 게시물이 없습니다."),
    NOT_FIND_REPORT(false, HttpStatus.NOT_FOUND.value(),"일치하는 신고 내역이 없습니다."),
    NOT_FIND_ITEM(false, HttpStatus.NOT_FOUND.value(),"일치하는 상품이 없습니다."),
    INVALID_OAUTH_TYPE(false, HttpStatus.BAD_REQUEST.value(), "알 수 없는 소셜 로그인 형식입니다."),


    /**
     * 500 :  Database, Server 오류
     */
    DATABASE_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버와의 연결에 실패하였습니다."),
    PASSWORD_ENCRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 복호화에 실패하였습니다."),


    MODIFY_FAIL_USERNAME(false,HttpStatus.INTERNAL_SERVER_ERROR.value(),"유저네임 수정 실패"),
    DELETE_FAIL_USERNAME(false,HttpStatus.INTERNAL_SERVER_ERROR.value(),"유저 삭제 실패"),
    MODIFY_FAIL_MEMO(false,HttpStatus.INTERNAL_SERVER_ERROR.value(),"메모 수정 실패"),

    UNEXPECTED_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "예상치 못한 에러가 발생했습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
