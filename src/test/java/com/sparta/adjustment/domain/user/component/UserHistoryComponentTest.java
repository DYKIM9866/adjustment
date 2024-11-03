package com.sparta.adjustment.domain.user.component;

import com.sparta.adjustment.domain.history.UserVideoHistory;
import com.sparta.adjustment.domain.history.component.HistoryComponent;
import com.sparta.adjustment.domain.history.repository.UserVideoHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserHistoryComponentTest {
//    @Mock
//    private UserVideoHistoryRepository userVideoHistoryRepository;
//
//    @InjectMocks
//    private HistoryComponent userHistoryComponent;
//
//    @Test
//    @DisplayName("userId와 videoId로 유저의 최근 시청기록을 조회한다.")
//    void getUserVideoHistory_useUserIdVideoId_returnOptional(){
//        //given
//        Long userId = 1L;
//        Long videoId = 2L;
//        UserVideoHistory expect = new UserVideoHistory();
//        when(userVideoHistoryRepository.findTopByUserIdAndVideoIdOrOrderByCreatedAtDesc(userId, videoId))
//                .thenReturn(Optional.of(expect));
//        when(userVideoHistoryRepository.findTopByUserIdAndVideoIdOrOrderByCreatedAtDesc(2L, 1L))
//                .thenReturn(Optional.empty());
//
//        //when
//        Optional<UserVideoHistory> actual = userHistoryComponent.getUserVideoHistory(userId, videoId);
//        Optional<UserVideoHistory> empty = userHistoryComponent.getUserVideoHistory(2L, 1L);
//
//        //then
//        assertEquals(expect, actual.get());
//        assertTrue(empty.isEmpty());
//        verify(userVideoHistoryRepository, times(1)).findTopByUserIdAndVideoIdOrOrderByCreatedAtDesc(userId,videoId);
//        verify(userVideoHistoryRepository, times(1)).findTopByUserIdAndVideoIdOrOrderByCreatedAtDesc(2L,1L);
//    }

}