package com.cibertec.sga.payment.domain.model;

import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.receipt.domain.model.Receipt;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Pago de una o más cuentas por cobrar de un puesto o socio, emitido con su {@link Receipt}
 * correspondiente (RF-19–RF-23, RNF-04, RNF-05).
 */
public final class Payment {

    private final UUID uuid;
    private final Receipt receipt;
    private final LocalDate paymentDate;
    private final BigDecimal totalAmount;
    private final List<PaymentDetailRef> details;
    private final CreatedByRef createdBy;
    private final Currency currency;

    private Payment(Builder builder) {
        this.uuid = builder.uuid;
        this.receipt = builder.receipt;
        this.paymentDate = builder.paymentDate;
        this.totalAmount = builder.totalAmount;
        this.details = builder.details;
        this.createdBy = builder.createdBy;
        this.currency = builder.currency;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<PaymentDetailRef> getDetails() {
        return details;
    }

    public CreatedByRef getCreatedBy() {
        return createdBy;
    }

    public Currency getCurrency() {
        return currency;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID uuid;
        private Receipt receipt;
        private LocalDate paymentDate;
        private BigDecimal totalAmount;
        private List<PaymentDetailRef> details = List.of();
        private CreatedByRef createdBy;
        private Currency currency;

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder receipt(Receipt receipt) {
            this.receipt = receipt;
            return this;
        }

        public Builder paymentDate(LocalDate paymentDate) {
            this.paymentDate = paymentDate;
            return this;
        }

        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder details(List<PaymentDetailRef> details) {
            this.details = details;
            return this;
        }

        public Builder createdBy(CreatedByRef createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Payment build() {
            if (receipt == null) {
                throw new IllegalArgumentException("El comprobante del pago es obligatorio");
            }
            if (totalAmount == null || totalAmount.signum() <= 0) {
                throw new IllegalArgumentException("El monto total del pago debe ser mayor a cero");
            }
            if (details == null || details.isEmpty()) {
                throw new IllegalArgumentException("El pago debe incluir al menos una cuenta por cobrar");
            }
            if (currency == null) {
                throw new IllegalArgumentException("La moneda del pago es obligatoria");
            }
            return new Payment(this);
        }
    }
}
