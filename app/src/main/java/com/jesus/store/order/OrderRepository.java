package com.jesus.store.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Fetch EAGER de las lineas: evita LazyInitializationException al
     * renderizar la vista o mapear el DTO fuera de transaccion.
     */
    @Query("select distinct o from Order o left join fetch o.lines where o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumberWithLines(@Param("orderNumber") String orderNumber);
}