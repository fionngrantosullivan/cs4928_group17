package Payment;

import com.cafepos.domain.Order;

public final class CardPayment implements PaymentStrategy {
    private final String cardNumber;
    public CardPayment(String cardNumber) { this.cardNumber = cardNumber;
    }
    @Override
    public void pay(Order order) {
// mask card and print payment confirmation
    }

}
