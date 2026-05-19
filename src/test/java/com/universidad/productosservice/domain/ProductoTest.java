package com.universidad.productosservice.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductoTest {

    @Test
    void getEstadoRetornaBajoCuandoStockEsCinco() {
        Producto producto = new Producto();
        producto.setStock(5);

        assertThat(producto.getEstado()).isEqualTo("BAJO");
    }
}
