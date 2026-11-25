package com.tecnm.merida.market_backend.persistence.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;


@Entity
    @Table (name="clientes")
    public class Cliente {

        @Id
        private String id;
        private String nombre;
        @Column(name = "apellidos")
        private String apellido;
        private java.math.BigDecimal celular;
        private String  direccion;

        @Column (name ="correo_electronico")
        private String correoElectronico;

        @JsonIgnore
        @Column(length = 60)
        private String password;

        @OneToMany (mappedBy  = "cliente")
        private List<Compra> compras;

        public String getId() {
            return id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public void setApellido(String apellido) {
            this.apellido = apellido;
        }

        public java.math.BigDecimal getCelular() {
            return celular;
        }

        public void setCelular(java.math.BigDecimal celular) {
            this.celular = celular;
        }

        public String getDireccion() {
            return direccion;
        }

        public void setDireccion(String direccion) {
            this.direccion = direccion;
        }

        public String getCorreoElectronico() {
            return correoElectronico;
        }

    public List<Compra> getCompras() {
        return compras;
    }

    public void setCompras(List<Compra> compras) {
        this.compras = compras;
    }

    public void setCorreoElectronico(String correoElectronico) {
            this.correoElectronico = correoElectronico;



        }
    }

