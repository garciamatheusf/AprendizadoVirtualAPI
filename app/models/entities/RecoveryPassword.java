package models.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class RecoveryPassword {
    @Id
    @Column(name = "senhaTemp")
    public String senhaTemp;

    @Basic
    @Column(name = "email")
    public String email;

}