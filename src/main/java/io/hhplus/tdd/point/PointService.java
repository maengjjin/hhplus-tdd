package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import java.awt.Point;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable pointTable;

    private final PointHistoryTable historyTable;


    public UserPoint chargePoint(long id, long point) {

        if(point <= 0){
            throw new IllegalArgumentException();
        }

        UserPoint userPoint = pointTable.selectById(id);
        userPoint = pointTable.insertOrUpdate(userPoint.id(),point);

        historyTable.insert(userPoint.id(), userPoint.point(), TransactionType.CHARGE, userPoint.updateMillis());

        return userPoint;
    }

    public UserPoint findPoint(long id) {

        return  pointTable.selectById(id);
    }

    public UserPoint use(long id, long amount) {

        UserPoint userPoint = findPoint(id);

        if(amount > userPoint.point()){
            throw new IllegalArgumentException("포인트 부족");
        }

        long point = userPoint.point() - amount;

        userPoint = pointTable.insertOrUpdate(id, point);

        historyTable.insert(id, point, TransactionType.USE, userPoint.updateMillis());

        return userPoint;
    }

    public List<PointHistory> selectAllByUserId(long id) {

        return historyTable.selectAllByUserId(id);
    }



}
