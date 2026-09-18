package com.jesus.store.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderSummaryDto(
        String orderNumber,
        LocalDateTime createdAt,
        String customerName,
        BigDecimal totalAmount,
        List<LineDto> lines) {

    public record LineDto(Long productId, String sku, String name, BigDecimal unitPrice,
                          int quantity, BigDecimal total) {

        static LineDto from(OrderLine line) {
            return new LineDto(
                    line.getProductId(), line.getSku(), line.getName(),
                    line.getUnitPrice(), line.getQuantity(), line.total());
        }
    }

    public static OrderSummaryDto from(Order order) {
        List<LineDto> lines = order.getLines().stream().map(LineDto::from).toList();
        return new OrderSummaryDto(
                order.getOrderNumber(), order.getCreatedAt(),
                order.getCustomerName(), order.getTotalAmount(), lines);
    }
}