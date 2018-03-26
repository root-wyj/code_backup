package com.shixi.web.tools;

import java.math.BigDecimal;

public class MoneyUtils {
	public static final BigDecimal ZERO = MoneyUtils.toBigDecimal(0.0);
	public static final int TRADE_AMOUNT_SCALE = 2; // 保留两位小数
	public static final int TRADE_AMOUNT_ROUNDING_MODE = BigDecimal.ROUND_HALF_UP; // 四舍五入
	public static final double TRADE_AMOUNT_ZERO = 0.00;
	
	private static final BigDecimal AMOUNT_MIN_VALUE = BigDecimal.valueOf(0).setScale(TRADE_AMOUNT_SCALE, TRADE_AMOUNT_ROUNDING_MODE);
	private static final BigDecimal DISCOUNT_MAX_VALUE = BigDecimal.valueOf(1).setScale(TRADE_AMOUNT_SCALE, TRADE_AMOUNT_ROUNDING_MODE);
	private static final BigDecimal DISCOUNT_MIN_VALUE = BigDecimal.valueOf(0).setScale(TRADE_AMOUNT_SCALE, TRADE_AMOUNT_ROUNDING_MODE);
	
	public static BigDecimal getDiscountPrice(double price, double discount) {
		return MoneyUtils.toBigDecimal(price).multiply(MoneyUtils.toBigDecimal(discount));
	}
	
	public static boolean isValidAmount(double amount) {
		return (MoneyUtils.toBigDecimal(amount).compareTo(AMOUNT_MIN_VALUE) > 0);
	}
	
	public static boolean isValidDiscount(double discount) {
		BigDecimal d = BigDecimal.valueOf(discount);
		if (d.scale() > TRADE_AMOUNT_SCALE) {
			return false;
		}
		
		d.setScale(TRADE_AMOUNT_SCALE, TRADE_AMOUNT_ROUNDING_MODE);
		if (d.compareTo(DISCOUNT_MIN_VALUE) < 0 || d.compareTo(DISCOUNT_MAX_VALUE) > 0) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 计算用户购买分成，五五分成
	 * @param totalAmount
	 * @return
	 * @author heyong
	 */
	public static BigDecimal getUserSharingFromPurchase(double totalAmount) {
		BigDecimal result = MoneyUtils.toBigDecimal(totalAmount).multiply(MoneyUtils.toBigDecimal(0.5));
		return result.setScale(TRADE_AMOUNT_SCALE, BigDecimal.ROUND_FLOOR);
	}
	
	/**
	 * 计算用户打赏分成，三七分成
	 * @param totalAmount
	 * @return
	 * @author heyong
	 */
	public static BigDecimal getUserSharingFromReward(double totalAmount) {
		BigDecimal result = MoneyUtils.toBigDecimal(totalAmount).multiply(MoneyUtils.toBigDecimal(0.7));
		return result.setScale(TRADE_AMOUNT_SCALE, BigDecimal.ROUND_FLOOR);
	}
	
	public static BigDecimal toBigDecimal(Double val) {
		return MoneyUtils.toBigDecimal(val, null);
	}
	
	public static BigDecimal toBigDecimal(Double val, Double defaultValue) {
		if (null == val && null == defaultValue) {
			return null;
		}
		return BigDecimal.valueOf(null != val ? val : defaultValue).setScale(TRADE_AMOUNT_SCALE, TRADE_AMOUNT_ROUNDING_MODE);
	}
	
	public static BigDecimal toBigDecimal(String val) {
		return new BigDecimal(val).setScale(TRADE_AMOUNT_SCALE, TRADE_AMOUNT_ROUNDING_MODE);
	}
}
