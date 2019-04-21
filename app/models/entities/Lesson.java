package models.entities;

import play.db.jpa.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;


@NamedQueries(value = {
        @NamedQuery(
                name = Lesson.NAMED_QUERY_GET_BY_ID,
                query = "FROM Lesson l " +
                        " WHERE l.id = :idlesson"
        )
})

@Entity
public class Lesson {

    public static final String NAMED_QUERY_GET_BY_ID = "getLessonById";

    @Id
    @Column(name = "id")
    public long id;

    @Basic
    @Column(name = "name")
    public String name;

    @Basic
    @Column(name = "url")
    public String url;

    @Basic
    @Column(name = "date")
    public Date date;

    @Basic
    @Column(name = "description")
    public String description;

    @Basic
    @Column(name = "author")
    public String author;

    @Basic
    @Column(name = "live")
    public boolean live;

    @Basic
    @Column(name = "views")
    public int views;

    @Basic
    @Column(name = "image")
    public String image;


    public static ArrayList<Lesson> getAll(EntityManager em){
        return (ArrayList<Lesson>) em.createQuery("SELECT l FROM Lesson l", Lesson.class).getResultList();
    }

    public static Lesson getById(EntityManager em, long id){
        try {
            return (Lesson) em.createNamedQuery(Lesson.NAMED_QUERY_GET_BY_ID)
                    .setParameter("idlesson", id)
                    .getSingleResult();

        } catch (NoResultException e){
            return null;
        }
    }

    public static ArrayList<Lesson> getLastTen(EntityManager em){
        return (ArrayList<Lesson>) em.createQuery("SELECT l from Lesson l order by date desc")
                .setMaxResults(10)
                .getResultList();
    }

    @Transactional
    public static void insertWithObject(EntityManager em, Lesson lesson){
        em.persist(lesson);
    }

    public static boolean update(EntityManager em, Lesson lesson){
        try{
            em.merge(lesson);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public static boolean remove(EntityManager em, Lesson lesson){
        try{
            Object u = em.merge(lesson);
            em.remove(u);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}