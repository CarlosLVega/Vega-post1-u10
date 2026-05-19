package com.universidad.productosservice.service;

import com.universidad.productosservice.domain.Producto;
import com.universidad.productosservice.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    // Code Smell: campo no final (deberia ser inyeccion por constructor)
    @Autowired
    private ProductoRepository repo; // Code Smell: nombre generico

    // Code Smell: metodo largo con multiples responsabilidades (CC alta)
    public Producto procesarProducto(String n, Double p, Integer s,
                                     String cat, boolean activo, String proveedor) {
        Producto producto = new Producto();
        if (n == null || n.equals("")) { // Code Smell: usar isBlank()
            throw new IllegalArgumentException("nombre requerido");
        }
        if (p == null) {
            throw new IllegalArgumentException("precio requerido");
        } else if (p <= 0) {
            throw new IllegalArgumentException("precio invalido");
        } else if (p > 999999) {
            throw new IllegalArgumentException("precio excesivo");
        }
        if (s == null || s < 0) {
            throw new IllegalArgumentException("stock invalido");
        }
        producto.setNombre(n);
        producto.setPrecio(p);
        producto.setStock(s);
        // TODO: implementar logica de categoria y proveedor
        return repo.save(producto);
    }

    public List<Producto> listar() {
        return repo.findAll();
    }

    // Bug: no lanza excepcion si el producto no existe
    public Producto buscar(Long id) {
        return repo.findById(id).orElse(null); // Bug: retorna null
    }
}
