package hello.exception.api;

import hello.exception.exception.UserException;
import hello.exception.exhandler.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class ApiExceptionV2Controller {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandler(IllegalArgumentException e){
        /*
        이대로 사용하면 정상처리가 되서 코드가 200이 되버린다.

        @ResponseStatus(HttpStatus.BAD_REQUEST)를 추가해주면
        정상이 아닌 Bad Request로 판단.
         */
        log.error("[exceptionHandler] ex ", e);
        return new ErrorResult("Bad", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHander(UserException e){
        log.error("[exceptionHandler] ex ", e);
        ErrorResult errorResult = new ErrorResult("USER-ex", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandler(Exception e){
        /*
        최상위 Exception 위에 Exception에서 처리되지 못한 Exception 넘어옴
        다른 Handler에서 놓치는 걸 대비해서 만드는 ExceptionHandler
        Spring은 항상 detail한게 우선권을 갖기 때문에 위에서 처리될 경우 부모로 안넘어온다.

        ExceptionHandler는 일반적인 mvc보다 api에서 사용한다.
         */
        log.error("[exceptionHandler] ex ", e);
        return new ErrorResult("EX", "내부 오류");
    }

    @GetMapping("/api2/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {

        if (id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");
        }
        if (id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력 값");
        }
        if (id.equals("user-ex")) {
            throw new UserException("사용자 오류");
        }

        return new MemberDto(id, "hello " + id);
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }

}
