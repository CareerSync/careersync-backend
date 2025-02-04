# CareerSync - RAG 기술을 활용한 채용정보제공 챗봇 서비스

![image](https://github.com/user-attachments/assets/2272a29a-9bbb-4e8f-8800-b00c8c773ded)



## ✨Introduction


https://github.com/user-attachments/assets/fe06ff3b-98a2-41d8-a053-b602f6415344

## 👀Why RAG?
  －　최신 채용 정보 실시간 제공: 기존 언어 모델은 최신 정보 반영이 어렵지만, RAG 기법을 활용하면 사전에 수집된 최신 채용 공고를 실시간으로 검색하여 사용자에게 제공할 수 있음.　　

  －　개인 맞춤형 채용 정보 추천: 사용자의 개인 이력을 컨텍스트로 활용하여, 단순 키워드 검색이 아닌 보다 정교한 채용 공고 매칭이 가능함.　　

  －　개인화된 경험 최적화: Redis 캐싱 시스템을 도입하여 사용자의 최근 대화 내용을 신속하게 반영, 보다 빠르고 개인화된 채용 정보 제공이 가능함.　　


## ✨ERD
![gridgetest-server-erd](https://github.com/shinsj4653/2024-Server-Gridge-Test/assets/49470452/7ab71972-7a3c-47f6-a92b-64eeb48c684c)  


## 💻Service Architecture

![image](https://github.com/user-attachments/assets/a23466bd-5e37-4ba1-8203-79990ce850db)

## ✨Flowchart

![image](https://github.com/user-attachments/assets/ba5ceaa9-726a-4f49-8a76-62c7bb6ee118)

## 🛠️Tech Stack

Framework - <img src="https://camo.githubusercontent.com/521688401a8b06ccebdbd83ab34f6e5014d171bf3ed456cb7e17767c15305ae9/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f537072696e6720426f6f742d3644423333463f7374796c653d666f722d7468652d736f6369616c266c6f676f3d537072696e6720426f6f74266c6f676f436f6c6f723d7768697465"/>  <img src="https://camo.githubusercontent.com/c09043d941dfefe085df9bd68836e0fc40d87541011bce983602c279bb904435/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f477261646c652d3032333033413f7374796c653d666f722d7468652d736f6369616c266c6f676f3d477261646c65266c6f676f436f6c6f723d7768697465" />

Database - `Azure SQL Database`, `CosmosDB`, `Redis for Azure`    

ORM - <img src="https://camo.githubusercontent.com/ec87dc323254d71bfd22eb2f61ac85f317147df89b0e9376bee5bf5fb07c27d7/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f537072696e672044617461204a50412d3644423333463f7374796c653d666f722d7468652d736f6369616c266c6f676f3d44617461627269636b73266c6f676f436f6c6f723d7768697465" />  

Deploy - <img src="https://camo.githubusercontent.com/928f6ed384cb7ea730bcfae1b95c96be89f9948f46032eed6a9b5976b0d93cf1/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f47697468756220416374696f6e732d3230383846463f7374796c653d666f722d7468652d736f6369616b266c6f676f3d676974687562616374696f6e73266c6f676f436f6c6f723d7768697465" /> <img src="https://camo.githubusercontent.com/aea28cb501aa5c1f2141edba7d130c5c1100de39b25b1ab21fc12e3c9dbc2f88/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f446f636b65722d3234393645443f7374796c653d666f722d7468652d736f6369616b266c6f676f3d646f636b6572266c6f676f436f6c6f723d7768697465" />   

Logging - <img src="https://camo.githubusercontent.com/0eb1ba481971344198bae714d48c419626b3f02e38760c4989bc1fb6c9ccd884/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4c6f676261636b2d323541313632" />     

API Docs - <img src="https://camo.githubusercontent.com/aa961b7feeb5d94eb02518040ba87db1a546bb9f20af29399ad4de9101f91b04/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f537072696e67446f63205377616767657220332d3835454132443f7374796c653d666f722d7468652d736f6369616b266c6f676f3d73776167676572266c6f676f436f6c6f723d7768697465" />        


## ✨Structure
앞에 (*)이 붙어있는 파일(or 폴더)은 추가적인 과정 이후에 생성된다.
```text
api-server-spring-boot
  > * build
  > gradle
  > src.main.java.com.example.demo
    > common
      > config
        | RestTemplateConfig.java // HTTP get,post 요청을 날릴때 일정한 형식에 맞춰주는 template
        | SwaggerConfig.java // Swagger 관련 설정
        | WebConfig.java // Web 관련 설정(CORS 설정 포함)
        | DataEnverConfig.java // Spring Data Envers 사용을 위한 auditReader 생성
        | SecurityConfig.java // Swagger 및 Postman 으로 API 테스트 시, 403 error 방지하기 위한 Security 관련 설정
        | ServletConfig.java // API Response 메세지의 다국어 지원 설정
      > entity
        | BaseEntity.java // create, update, state 등 Entity에 공통적으로 정의되는 변수를 정의한 BaseEntity
      > exceptions
        | BaseException.java // Controller, Service에서 Response 용으로 공통적으로 사용 될 익셉션 클래스
        | ExceptionAdvice.java // ExceptionHandler를 활용하여 정의해놓은 예외처리를 통합 관리하는 클래스
      > file
        | FileHandler.java // MultipartFile 리스트를 BoardImage 리스트로 변환해주는 클래스
      > oauth
        | GoogleOauth.java // Google OAuth 처리 클래스
        | KakaoOauth.java // Kakao OAuth 처리 클래스
        | OAuthService.java // OAuth 공통 처리 서비스 클래스
        | SocialOauth.java // OAuth 공통 메소드 정의 인터페이스
      > payment
        | IamportClientInitializer.java // 포트원 결제 요청 처리를 위한 클라이언트를 생성해주는 클래스
      > response
        | BaseResponse.java // Controller 에서 Response 용으로 공통적으로 사용되는 구조를 위한 모델 클래스
        | BaseResponseStatus.java // Controller, Service에서 사용할 Response Status 관리 클래스
      > scheduler
        | SchedulerService.java // 스케쥴러 로직 담당 클래스
      > secret
        | Secret.java // jwt 암호키 보관 클래스
      | Constant // 상수 보관 클래스
    > src
      > test
        > entity
          | Comment.java // Comment Entity
          | Memo.java // Memo Entity
        > model
          | GetMemoDto.java
          | MemoDto.java
          | PostCommentDto.java
        | TestController.java // Memo API Controller
        | TestService.java // Memo API Service
        | MemoRepository.java // Memo Spring Data JPA
        | CommentRepository.java // Comment Spring Data JPA
      > admin
        > model
          | PostUserLogTimeReq.java // CUD 히스토리 조회 시, 특정 시간 범위 지정을 위한 Request
        | AdminController.java // 신고당한 유저 차단, CUD 히스토리 조회 Controller
        | AdminService.java // 신고당한 유저 차단 로직 담당
      > revision
        > entity
          | Revision.java // 기존 Revision 테이블 설정 변경을 위한 클래스
      > user
        > entity
          | User.java // User Entity
        > model
          | GetSocialOAuthRes.java // OAuth 인증 관련 DTO(토튼 정보)
          | GetUserRes.java    
          | GoogleUser.java // OAuth 인증 관련 DTO(유저 정보)
          | PatchUserReq.java
          | PostLoginReq.java
          | PostLoginRes.java 
          | PostUserReq.java 
          | PostUserRes.java 
        | UserController.java
        | UserService.java
        | UserRepository.java
      > board
        > entity
          | Board.java // Board Entity
          | BoardImage.java // BoardImage Entity
        > model
          | BoardFileVO.java // Board 등록을 위한 VO 클래스 - 게시물 정보와 게시물에 등록될 이미지 리스트 존재
          | BoardImageDto.java // MultipartFile 이미지 형태를 BoardImage로 변환하기 위한 Dto 
        | BoardController.java
        | BoardService.java
        | BoardRepository.java
        | BoardImageRepository.java
      > report
        > entity
          | Report.java // Report Entity
        | ReportController.java
        | ReportService.java
        | ReportRepository.java
      > item
        > entity
          | Item.java // Item Entity
        | ItemController.java
        | ItemService.java
        | ItemRepository.java
      > payment
        > entity
          | Payment.java // Payment Entity
        > model
          | CancelReq.java // 결제 취소를 위한 Request
          | VerificationReq.java // 결제 내역 검증을 위한 Request
        | PaymentController.java
        | PaymentService.java
        | PaymentRepository.java
      > subscription
        > entity
          | Subscription.java // Subscription Entity
        | SubscriptionController.java
        | SubscriptionService.java
        | SubscriptionRepository.java
    > utils
      | JwtService.java // JWT 관련 클래스
      | SHA256.java // 암호화 알고리즘 클래스
      | ValidateRegex.java // 정규표현식 관련 클래스
    | DemoApplication // SpringBootApplication 서버 시작 지점
  > resources
    | application.yml // Database 연동을 위한 설정 값 세팅 및 Port 정의 파일 - dev, prod로 관리
    | logback-spring.xml // logback 설정 xml 파일
build.gradle // gradle 빌드시에 필요한 dependency 설정하는 곳
.gitignore // git 에 포함되지 않아야 하는 폴더, 파일들을 작성 해놓는 곳

```
