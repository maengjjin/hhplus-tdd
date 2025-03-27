package io.hhplus.tdd.point.common;

import static io.hhplus.tdd.point.common.PointChargeLimits.*;

public class ValidatorAmount {

    // 최대 금액 충전
    public static void maxChargeAmount(long amount){
        if(amount > MAX_CHARGE_AMOUNT){
            throw new IllegalArgumentException("충전 금액이 최대 한도를 초과했습니다.");
        }
    }

    // 최소 금액 충전
    public static void minChargeAmount(long amount){
        if(amount < MIN_CHARGE_AMOUNT){
            throw new IllegalArgumentException("충전 금액이 최소 한도보다 적습니다.");
        }
    }

    // 최소 금액 사용
    public static void useChargeAmount(long amount){
        if(amount < PointChargeLimits.USE_CHARGE_AMOUNT){
            throw new IllegalArgumentException("사용 금액이 최소 사용금액보다 작습니다.");
        }
    }

}
