package io.hhplus.tdd.user;

import static io.hhplus.tdd.point.common.PointChargeLimits.*;
import static io.hhplus.tdd.point.common.ValidatorAmount.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.hhplus.tdd.point.repository.PointHistoryTable;
import io.hhplus.tdd.point.repository.UserPointTable;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.common.TransactionType;
import io.hhplus.tdd.point.entity.UserPoint;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    private UserPointTable pointTable;

    @Mock
    private PointHistoryTable historyTable;

    @InjectMocks
    private PointService service;

    private final long id = 1L;

    @Test
    @DisplayName("금액 검증 로직 실패 테스트")
    void chargeFailsIfAmountIsInvalid() {

        assertThrows(IllegalArgumentException.class, () -> minChargeAmount(MIN_CHARGE_AMOUNT - 1L));
        assertThrows(IllegalArgumentException.class, () -> maxChargeAmount(MAX_CHARGE_AMOUNT + 1L));
        assertThrows(IllegalArgumentException.class, () -> useChargeAmount(USE_CHARGE_AMOUNT - 1L));

    }

    @Test
    @DisplayName("금액 검증 로직 성공 테스트")
    void chargePassIfAmountIsInvalid() {

        assertDoesNotThrow(() -> minChargeAmount(MIN_CHARGE_AMOUNT));
        assertDoesNotThrow(() -> minChargeAmount(MAX_CHARGE_AMOUNT));
        assertDoesNotThrow(() -> useChargeAmount(USE_CHARGE_AMOUNT));

    }

    @Test
    @DisplayName("포인트 충전 성공")
    void chargePointTest() {

        //given : 초기 유저 포인트 정보
        long point = 1000L;

        UserPoint existingUser = new UserPoint(id, 0L, System.currentTimeMillis()); // 기존 포인트 0
        UserPoint updatedUser = new UserPoint(id, point, System.currentTimeMillis()); // 충전 1000
        PointHistory history = new PointHistory(1, id, point, TransactionType.CHARGE, updatedUser.updateMillis());

        Mockito.when(pointTable.selectById(id)).thenReturn(existingUser);
        Mockito.when(pointTable.insertOrUpdate(id, existingUser.point() + point)).thenReturn(updatedUser);
        Mockito.when( historyTable.insert(id, point, TransactionType.CHARGE, updatedUser.updateMillis())).thenReturn(history);

        // when : 포인트 충전
        UserPoint userPoint = service.chargePoint(id, point);

        // then : 포인트 충전 히스토리에 등록 검증
        assertThat(userPoint.point()).isEqualTo(history.amount());
        Mockito.verify(pointTable, Mockito.times(1)).selectById(id);

    }


    @Test
    @DisplayName("아이디로 유저 정보 조회")
    void pointFindTest() {

        //given : 아이디로 유저 조회 했을 때
        UserPoint user = new UserPoint(id, 1000L, System.currentTimeMillis()); // 기존 유저 정보

        Mockito.when(pointTable.selectById(id)).thenReturn(user);

        // when : 해당 아이디로 유저 정보 조회
        UserPoint userPoint = service.findPoint(id);

        // then : 유저 확인
        assertThat(userPoint).isEqualTo(user);
        Mockito.verify(pointTable, Mockito.times(1)).selectById(userPoint.id());

    }

    @Test
    @DisplayName("사용할 포인트가 부족 할 때 exception")
        // 0은 컨트롤러 에서 검증
    void usePointExceptionTest() {

        //given : 고객이 보유하고 있는 금액
        UserPoint user = new UserPoint(id, 1000L, System.currentTimeMillis()); // 기존 유저 정보

        Mockito.when(pointTable.selectById(id)).thenReturn(user);

        // when : 포인트 사용
        long amount = 3000L;

        // then : 검증
        assertThrows(IllegalArgumentException.class, () -> service.use(id, amount));
        Mockito.verify(pointTable, Mockito.times(1)).selectById(user.id()); // 조회를 했는지
    }

    @Test
    @DisplayName("포인트 사용 성공 했을 때")
    void usePointTest() {

        //given : 고객이 보유하고 있는 금액
        UserPoint user = new UserPoint(id, 10000L, System.currentTimeMillis()); // 기존 유저 정보
        UserPoint updateUser = new UserPoint(id, 7000L, System.currentTimeMillis()); // 사용 후 유저 정보
        PointHistory history = new PointHistory(id, id, 7000L, TransactionType.USE, user.updateMillis());// 사용 후 금액

        Mockito.when(pointTable.selectById(id)).thenReturn(user);
        Mockito.when(pointTable.insertOrUpdate(id, 7000L)).thenReturn(updateUser);
        Mockito.when(historyTable.insert(id, 7000L, TransactionType.USE, history.updateMillis())).thenReturn(history);

        // when : 포인트 사용
        long amount = 3000L;
        UserPoint use = service.use(id, amount);

        // then : 검증
        assertThat(use.point()).isEqualTo(user.point() - amount);
        Mockito.verify(pointTable, Mockito.times(1)).insertOrUpdate(id, user.point() - amount); // 조회를 했는지
        Mockito.verify(historyTable, Mockito.times(1)).insert(history.userId(), history.amount(), TransactionType.USE,
            history.updateMillis());

    }

    @Test
    @DisplayName("포인트 히스토리 조회 성공")
    void historyAllTest() {

        // given : 특정 사용자의 포인트 히스토리 데이터가 존재할 때
        PointHistory history1 = new PointHistory(1L, id, 10000L, TransactionType.CHARGE, System.currentTimeMillis());
        PointHistory history2 = new PointHistory(2L, id, 3000L, TransactionType.USE, System.currentTimeMillis());
        PointHistory history3 = new PointHistory(3L, id, 1000L, TransactionType.USE, System.currentTimeMillis());
        List<PointHistory> historyList = List.of(history1, history2, history3);

        Mockito.when(historyTable.selectAllByUserId(id)).thenReturn(historyList);

        // when : id로 히스토리를 조회 했을 때
        List<PointHistory> list = service.selectAllByUserId(id);

        // then : 검증
        assertThat(historyList).isEqualTo(list);
        Mockito.verify(historyTable, Mockito.times(1)).selectAllByUserId(id);
    }


}
