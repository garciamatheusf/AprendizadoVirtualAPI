package models.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Aula {
    @Id
    @Column(name = "idAula")
    public long idAula;

    @Basic
    @Column(name = "nomeAula")
    public String nomeAula;

    @Basic
    @Column(name = "urlAula")
    public String urlAula;

    @Basic
    @Column(name = "dataCriacao")
    public Date dataCriacao;

    @Basic
    @Column(name = "cenario")
    public String cenario;

    @ManyToOne
    @JoinColumn(name = "autor")
    public Usuario autor;

    @Basic
    @Column(name = "aoVivoAgora")
    public boolean aoVivoAgora;
}