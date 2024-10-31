package com.sparta.adjustment.api.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonApiResponse<T> {

    private ApiResponseStatus status;
    private String message;
    private T data;

    public static <T> CommonApiResponse<T> success(T data){
        return new CommonApiResponse<>(ApiResponseStatus.SUCCESS, data, null);
    }
    public static CommonApiResponse<?> successWithNoContent(){
        return new CommonApiResponse<>(ApiResponseStatus.SUCCESS, null, null);
    }
    public static CommonApiResponse<?> fail(BindingResult bindingResult){
        Map<String,String> responseErrors = new HashMap<>();

        List<ObjectError> allErrors = bindingResult.getAllErrors();
        for(ObjectError error : allErrors){
            if(error instanceof FieldError){
                responseErrors.put(((FieldError) error).getField(), error.getDefaultMessage());
            }else{
                responseErrors.put(error.getObjectName(), error.getDefaultMessage());
            }
        }

        return new CommonApiResponse<>(ApiResponseStatus.FAIL, responseErrors, null);
    }

    public static CommonApiResponse<?> error(String message) {
        return new CommonApiResponse<>(ApiResponseStatus.ERROR, null, message);
    }

    // 생성자 추가
    public CommonApiResponse(ApiResponseStatus status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

}
