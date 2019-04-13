package models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import play.db.jpa.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;

@NamedQueries(value = {
        @NamedQuery(
                name = User.NAMED_QUERY_GET_BY_EMAIL,
                query = "FROM User u " +
                        " WHERE u.email = :email"
        ),
        @NamedQuery(
                name = User.NAMED_QUERY_GET_BY_TOKEN,
                query = "FROM User u " +
                        " WHERE u.token = :token"
        )
})

@Entity()
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    public static final String NAMED_QUERY_GET_BY_EMAIL = "getUserByEmail";
    public static final String NAMED_QUERY_GET_BY_TOKEN = "getUserByToken";

    @Id
    @Column(name = "email")
    public String email;

    @Basic
    @Column(name = "password")
    public String password;

    @Basic
    @Column(name = "token")
    public String token;

    @Basic
    @Column(name = "name")
    public String name;

    @Basic
    @Column(name = "lastname")
    public String lastname;

    @Basic
    @Column(name = "borndate")
    public Date borndate;

    @Basic
    @Column(name = "student")
    public boolean student;


    public static ArrayList<User> getAll(EntityManager em){
        return (ArrayList<User>) em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Transactional
    public static void insertWithObject(EntityManager em, User user){
        em.persist(user);
    }

    public static User getByEmail(EntityManager em, String email){
        try {
            return (User) em.createNamedQuery(User.NAMED_QUERY_GET_BY_EMAIL)
                    .setParameter("email", email)
                    .getSingleResult();

        } catch (NoResultException e){
            return null;
        }
    }

    public static User getByToken(EntityManager em, String token){
        try {
            return (User) em.createNamedQuery(User.NAMED_QUERY_GET_BY_TOKEN)
                    .setParameter("token", token)
                    .getSingleResult();

        } catch (NoResultException e){
            return null;
        }
    }

    public static boolean update(EntityManager em, User user){
        try{
            em.merge(user);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public static boolean remove(EntityManager em, User user){
        try{
            Object u = em.merge(user);
            em.remove(u);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}