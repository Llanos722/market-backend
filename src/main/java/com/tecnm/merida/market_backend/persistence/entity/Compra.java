package com.tecnm.merida.market_backend.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;

import java.time.LocalDateTime;
import java.util.List;

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

    //relacion con el cliente
    //Muchas compras para un cliente

    @ManyToOne
    @JoinColumn (name= "id_cliente",insertable = false, updatable = false)
    private Cliente cliente;

    //relacion con compraproducto
    @OneToMany (mappedBy = "compra" )
    private List<CompraProducto> productos;

    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public String getComentario() {
        return Comentario;
    }

    public void setComentario(String comentario) {
        Comentario = comentario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
