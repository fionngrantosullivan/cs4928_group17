package Payment;

import com.cafepos.domain.Order;

public final class WalletPayment implements PaymentStrategy {
    private final String walletId;
    public WalletPayment(String walletId, String walletId1) {this.walletId = walletId1;
    }
    @Override
    public void pay(Order order) {
        System.out.println("[Wallet] Customer paid " +
                order.totalWithTax(10) + " EUR via wallet " + walletId);
    }
}
