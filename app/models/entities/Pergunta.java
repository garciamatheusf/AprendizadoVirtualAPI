package models.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Pergunta {
    @Id
    @Column(name = "idPergunta")
    public long idPergunta;

    @Lob
    @Column(name = "pergunta")
    public byte[] pergunta;

    @ManyToOne
    @JoinColumn(name = "autor")
    public Usuario autor;

    @Lob
    @Column(name = "resposta")
    public byte[] resposta;

    @Basic
    @Column(name = "dataCriacao")
    public Date dataCriacao;

    @ManyToOne
    @JoinColumn(name = "fkAula")
    public Aula aula;

}