package models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import play.db.jpa.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;

@NamedQueries(value = {
        @NamedQuery(
                name = Usuario.NAMED_QUERY_GET_BY_EMAIL,
                query = "FROM Usuario u " +
                        " WHERE u.email = :email"
        ),
        @NamedQuery(
                name = Usuario.NAMED_QUERY_GET_BY_TOKEN,
                query = "FROM Usuario u " +
                        " WHERE u.token = :token"
        )
})

@Entity()
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Usuario {

    public static final String NAMED_QUERY_GET_BY_EMAIL = "getUserByEmail";
    public static final String NAMED_QUERY_GET_BY_TOKEN = "getUserByToken";

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


    public static ArrayList<Usuario> getAll(EntityManager em){
        return (ArrayList<Usuario>) em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
    }

    @Transactional
    public static void insertWithObject(EntityManager em, Usuario usuario){
        em.persist(usuario);
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

    public static Usuario getByToken(EntityManager em, String token){
        try {
            return (Usuario) em.createNamedQuery(Usuario.NAMED_QUERY_GET_BY_TOKEN)
                    .setParameter("token", token)
                    .getSingleResult();

        } catch (NoResultException e){
            return null;
        }
    }

    public static boolean update(EntityManager em, Usuario usuario){
        try{
            em.merge(usuario);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public static boolean remove(EntityManager em, Usuario usuario){
        try{
            Object u = em.merge(usuario);
            em.remove(u);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}