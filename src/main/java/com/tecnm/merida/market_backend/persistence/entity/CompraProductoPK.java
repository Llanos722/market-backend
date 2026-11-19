package com.tecnm.merida.market_backend.persistence.entity;
import jakarta.persistence.*;

import java.io.Serializable;

@Embeddable

public class CompraProductoPK implements Serializable {

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof CompraProductoPK key)) return false;
        return java.util.Objects.equals(idCompra, key.idCompra)
                && java.util.Objects.equals(idProducto, key.idProducto);
    }

    @Override
    public int hashCode() { return java.util.Objects.hash(idCompra, idProducto); }

   @Column (name = "id_compra")
    private Integer idCompra;

   @Column (name = "id_producto")
    private Integer idProducto;

    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }


}
