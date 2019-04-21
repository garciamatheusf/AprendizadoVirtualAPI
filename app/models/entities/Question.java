package models.entities;

import play.db.jpa.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;

@NamedQueries(value = {
        @NamedQuery(
                name = Question.NAMED_QUERY_GET_BY_ID,
                query = "FROM Question l " +
                        " WHERE l.id = :id"
        )
})

@Entity
public class Question {

    public static final String NAMED_QUERY_GET_BY_ID = "getQuestionById";

    @Id
    @Column(name = "id")
    public long id;

    @Lob
    @Column(name = "question")
    public byte[] question;

    @Basic
    @Column(name = "author")
    public String author;

    @Lob
    @Column(name = "answer")
    public byte[] answer;

    @Basic
    @Column(name = "date")
    public Date date;

    @Basic
    @Column(name = "idlesson")
    public int idlesson;

    public static ArrayList<Question> getAll(EntityManager em){
        return (ArrayList<Question>) em.createQuery("SELECT l FROM Question l", Question.class).getResultList();
    }

    public static Question getById(EntityManager em, long id){
        try {
            return (Question) em.createNamedQuery(Question.NAMED_QUERY_GET_BY_ID)
                    .setParameter("id", id)
                    .getSingleResult();

        } catch (NoResultException e){
            return null;
        }
    }

    @Transactional
    public static void insertWithObject(EntityManager em, Question question){
        em.persist(question);
    }

    public static boolean update(EntityManager em, Question question){
        try{
            em.merge(question);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public static boolean remove(EntityManager em, Question question){
        try{
            Object u = em.merge(question);
            em.remove(u);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}