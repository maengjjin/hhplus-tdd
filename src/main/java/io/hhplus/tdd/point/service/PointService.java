package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.common.ValidatorAmount;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.common.TransactionType;
import io.hhplus.tdd.point.repository.PointHistoryTable;
import io.hhplus.tdd.point.repository.UserPointTable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable pointTable;

    private final PointHistoryTable historyTable;


    public UserPoint chargePoint(long id, long amount) {

        ValidatorAmount.maxChargeAmount(amount);
        ValidatorAmount.minChargeAmount(amount);

        UserPoint userPoint = pointTable.selectById(id);
        userPoint = pointTable.insertOrUpdate(userPoint.id(),userPoint.point() + amount);

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
