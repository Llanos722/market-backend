package com.tecnm.merida.market_backend.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;

import java.time.LocalDateTime;
@Entity
@Table (name= "compras")

public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column (name= "Compra")
    private Integer idCompra;

    @Column (name="id_cliente")
    private String idCliente;

    private LocalDateTime fecha;

    @Column (name="medio_pago")
    private String medioPago;

    private String Comentario;
    private String estado;
}
