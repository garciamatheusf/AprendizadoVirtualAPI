package models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import play.db.jpa.Transactional;

import javax.persistence.*;
import java.util.Date;

@NamedQueries(value = {
        @NamedQuery(
                name = Usuario.NAMED_QUERY_GET_BY_EMAIL,
                query = "FROM Usuario u " +
                        " WHERE u.email = :email"
        )
})

@Entity()
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Usuario {

    public static final String NAMED_QUERY_GET_BY_EMAIL = "getUserByEmail";

    @Id
    @Column(name = "email")
    public String email;

    @Basic
    @Column(name = "senha")
    public String senha;

    @Basic
    @Column(name = "token")
    public String token;

    @Basic
    @Column(name = "nome")
    public String nome;

    @Basic
    @Column(name = "sobrenome")
    public String sobrenome;

    @Basic
    @Column(name = "dataNascimento")
    public Date dataNasc;

    @Basic
    @Column(name = "isAluno")
    public boolean isAluno;

    @Transactional
    public static void insertWithQuery(EntityManager em, Usuario usuario) {
        em.createNativeQuery("INSERT INTO usuario(email, senha, nome, sobrenome, dataNascimento, isAluno) values(?,?,?,?,?,?)")
                .setParameter(1, usuario.email)
                .setParameter(2, usuario.senha)
                .setParameter(3, usuario.nome)
                .setParameter(4, usuario.sobrenome)
                .setParameter(5, usuario.dataNasc)
                .setParameter(6, usuario.isAluno)
                .executeUpdate();
    }

    public static Usuario getByEmail(EntityManager em, String email){
        try {
            return (Usuario) em.createNamedQuery(Usuario.NAMED_QUERY_GET_BY_EMAIL)
                    .setParameter("email", email)
                    .getSingleResult();

        } catch (NoResultException e){
            return null;
        }
    }
}