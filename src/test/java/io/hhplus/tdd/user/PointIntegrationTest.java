package io.hhplus.tdd.user;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PointIntegrationTest {

    @Autowired
    private PointService service;


    @Test
    void 유저_충전_테스트() {

        // given 유저 기본정보 설정
        long id = 1L;
        long amount = 1_000L;

        // when 포인트 충전
        UserPoint result = service.chargePoint(id, amount);

        // then 포인트충전, 히스토리 쌓이는것 확인
        assertNotNull(result);
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.point()).isEqualTo(amount);

        List<PointHistory> pointHistories = service.selectAllByUserId(id);
        assertThat(pointHistories.size()).isEqualTo(1);

    }


    @Test
    void 유저_정보_조회() {

        // given 유저 기본정보
        long id = 2L;
        long amount = 2_000L;
        long addAmount = 1_000L;

        service.chargePoint(id, amount);
        service.chargePoint(id, addAmount);

        // when 유저 정보 조회
        UserPoint result = service.findPoint(id);

        // then 유저에 충전한 금액들이 제대로 저장 됐는지 검증
        assertNotNull(result);
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.point()).isEqualTo(amount + addAmount);

    }


    @Test
    void 포인트_사용() {

        // given 유저 기본정보 세팅
        long id = 3L;
        long amount = 10_000L;
        long useAmount1 = 3_000L;
        long useAmount2 = 4_000L;

        service.chargePoint(id, amount);

        // when 포인트 사용
        service.use(id, useAmount1);
        UserPoint result = service.use(id, useAmount2);

        // then 유저에 사용한 금액 확인
        assertNotNull(result);
        assertThat(result.id()).isEqualTo(id);

        assertThat(result.point()).isEqualTo(amount - useAmount1 - useAmount2);

    }


    @Test
    void 포인트_충전_히스토리_확인() {

        // given 유저 세팅(포인트 충전 및 사용)
        long id = 4L;
        long chargeAmount1 = 10_000L;
        long chargeAmount2 = 2_000L;
        long useAmount1 = 3_000L;
        long useAmount2 = 4_000L;

        service.chargePoint(id, chargeAmount1);
        service.use(id, useAmount1);
        service.use(id, useAmount2);
        service.chargePoint(id, chargeAmount2);

        // when 유저 히스토리 확인
        List<PointHistory> result = service.selectAllByUserId(id);

        // then 검증
        assertNotNull(result);
        assertThat(result.get(0).userId()).isEqualTo(id);
        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get(result.size() -1)).isEqualTo(chargeAmount1 - useAmount1 - useAmount2 + chargeAmount2);

    }


}
