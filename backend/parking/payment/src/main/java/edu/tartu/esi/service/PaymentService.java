package edu.tartu.esi.service;

import edu.tartu.esi.mapper.PaymentEntityMapper;
import edu.tartu.esi.model.Booking;
import edu.tartu.esi.model.Payment;
import edu.tartu.esi.model.PaymentStatusEnum;
import edu.tartu.esi.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    private PaymentEntityMapper paymentMapper;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public PaymentStatusEnum makePayment(String bookingId) {
        Booking booking = getBooking(bookingId);

        String oldBalanceStr = getPaymentMethodDtoForUser(booking.getCustomerId());
        PaymentStatusEnum paymentStatus;

        Duration duration = Duration.between(booking.getTimeFrom(), booking.getTimeUntil());
        BigDecimal hours = new BigDecimal(duration.toHours());

        BigDecimal oldBalance = new BigDecimal(oldBalanceStr);
        String pricePerHour = booking.getPrice();
        BigDecimal price = new BigDecimal(pricePerHour);
        BigDecimal amount = price.multiply(hours);

        if (oldBalance.compareTo(amount) >= 0) {
            BigDecimal newBalance = oldBalance.subtract(amount);
            updateBalance(booking.getCustomerId(), newBalance.toString());

            paymentStatus = PaymentStatusEnum.COMPLETED;

            BigDecimal balanceLandlord = new BigDecimal(getPaymentMethodDtoForUser(booking.getLandlordId()));
            updateBalance(booking.getLandlordId(), balanceLandlord.add(amount).toString());
        } else {
            paymentStatus = PaymentStatusEnum.DECLINED;
        }

        Payment payment = Payment.builder()
                .payerId(booking.getCustomerId())
                .receiverId(booking.getLandlordId())
                .bookingId(bookingId)
                .time(LocalDateTime.now())
                .amount(amount)
                .status(paymentStatus)
                .build();

        paymentRepository.save(payment);

        return paymentStatus;
    }


    public void updateBalance(String userId, String newBalanceStr) {
        webClientBuilder.build()
                .put()
                .uri("http://localhost:8083/api/v1/users/" + userId + "/balance")
                .bodyValue(newBalanceStr)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public Booking getBooking(String bookingId) {
        return webClientBuilder
                .build()
                .get()
                .uri("http://localhost:8086/api/v1/bookings/" + bookingId)
                .retrieve()
                .bodyToMono(Booking.class)
                .block();
    }

    public String getPaymentMethodDtoForUser(String userId) {
        return webClientBuilder
                .build()
                .get()
                .uri("http://localhost:8083/api/v1/users/" + userId + "/balance")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
