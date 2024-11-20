package com.sparta.adjustment.domain.user.component;

import com.sparta.adjustment.domain.user.User;
import com.sparta.adjustment.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserComponentTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserComponent userComponent;

    @Test
    @DisplayName("오버로딩 된 두개의 User 조회 메서드 테스트")
    void getUser_whenExist_returnUser() {
        //given
        Long userId = 1L;
        String userEmail = "userEmail@naver.com";
        User userIdExpect =  new User();
        User userEmailExpect =  new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(userIdExpect));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(userEmailExpect));

        //when
        User userIdActual = userComponent.getUser(userId);
//        User userEmailActual = userComponent.getUser(userEmail);

        //then
        assertEquals(userIdExpect, userIdActual);
//        assertEquals(userEmailExpect, userEmailActual);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmail(userEmail);
    }

    @Test
    @DisplayName("오버로딩 된 두개의 User 존재 하지 않을 시 런타임 에러 발생")
    void getUser_whenNotExist_throwError(){
        //given
        Long userId = 1L;
        String userEmail = "userEmail@naver.com";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exception1 = assertThrows(EntityNotFoundException.class,
                () -> userComponent.getUser(userId));
        EntityNotFoundException exception2 = assertThrows(EntityNotFoundException.class,
                () -> userComponent.getUser(userEmail));

        //then
        assertEquals("해당 유저가 존재 하지 않습니다.",exception1.getMessage(),exception2.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmail(userEmail);
    }

}