package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PointServiceTest {

    @Autowired
    PointHistoryTable pointTable;

    @Autowired
    PointController controller;

    @Autowired
    UserPointTable userTable;

    @Test
    void selectUserTest(){

        UserPoint user = userTable.insertOrUpdate(1, 1000);
        UserPoint find = userTable.selectById(user.id());
        Assertions.assertThat(user).isEqualTo(find);

    }

    @Test
    void insertPointTest(){

        PointHistory history = pointTable.insert(1L, 1000, TransactionType.CHARGE, 1000);
        pointTable.insert(1L, 2000, TransactionType.CHARGE, 1000);

        List<PointHistory> list = pointTable.selectAllByUserId(history.id());

    }
}
