package com.sara.store.order;

import com.sara.store.cart.Cart;
import com.sara.store.cart.CartLine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Crea el pedido a partir del carrito de la sesion. El total lo calcula el
 * SERVIDOR con el precio de BD: el cliente nunca envia precios ni cantidades
 * manipulables (decision pensada para QA/seguridad).
 */
@Service
public class OrderService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.BASIC_ISO_DATE;

    /**
     * El numero de pedido se genera en memoria ANTES de persistir (la columna
     * es NOT NULL). El contador es atomico: seguro entre sesiones concurrentes.
     */
    private final AtomicLong sequence = new AtomicLong();

    private final OrderRepository orders;

    public OrderService(OrderRepository orders) {
        this.orders = orders;
    }

    @Transactional
    public Order create(CreateOrderRequest request, Cart cart) {
        Order order = new Order();
        order.setCustomerName(request.getCustomerName().trim());
        order.setEmail(request.getEmail());
        order.setPhone(request.getPhone());
        order.setAddress(request.getAddress().trim());
        order.setCity(request.getCity().trim());
        order.setZip(request.getZip().trim());
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderNumber("ORD-" + DATE_FMT.format(LocalDate.now()) + "-" + String.format("%05d", sequence.incrementAndGet()));

        BigDecimal total = BigDecimal.ZERO;
        for (CartLine line : cart.lines()) {
            order.addLine(new OrderLine(
                    line.getProductId(), line.getSku(), line.getName(),
                    line.getUnitPrice(), line.getQuantity(), line.getImagePath()));
            total = total.add(line.total());
        }
        order.setTotalAmount(total);

        return orders.save(order);
    }
}