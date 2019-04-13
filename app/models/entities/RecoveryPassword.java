package models.entities;

import play.db.jpa.Transactional;

import javax.persistence.*;
import java.util.ArrayList;

@NamedQueries(value = {
        @NamedQuery(
                name = RecoveryPassword.NAMED_QUERY_GET_BY_EMAIL,
                query = "FROM RecoveryPassword u " +
                        " WHERE u.email = :email"
        )
})

@Entity
public class RecoveryPassword {

    public static final String NAMED_QUERY_GET_BY_EMAIL = "getRecoveryByEmail";

    @Id
    @Column(name = "temppassword")
    public String temppassword;

    @Basic
    @Column(name = "email")
    public String email;


    public static ArrayList<RecoveryPassword> getAll(EntityManager em){
        return (ArrayList<RecoveryPassword>) em.createQuery("SELECT u FROM RecoveryPassword u", RecoveryPassword.class).getResultList();
    }

    public static RecoveryPassword getByEmail(EntityManager em, String email){
        try {
            return (RecoveryPassword) em.createNamedQuery(RecoveryPassword.NAMED_QUERY_GET_BY_EMAIL)
                    .setParameter("email", email)
                    .getSingleResult();

        } catch (NoResultException e){
            return null;
        }
    }

    @Transactional
    public static void insertWithObject(EntityManager em, RecoveryPassword recoveryPassword){
        em.persist(recoveryPassword);
    }

    public static boolean update(EntityManager em, RecoveryPassword recoveryPassword){
        try{
            em.merge(recoveryPassword);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public static boolean remove(EntityManager em, RecoveryPassword recoveryPassword){
        try{
            Object u = em.merge(recoveryPassword);
            em.remove(u);
            return true;
        }catch(Exception e){
            return false;
        }
    }

}