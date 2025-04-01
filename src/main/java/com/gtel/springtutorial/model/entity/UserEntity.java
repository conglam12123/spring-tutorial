package com.gtel.springtutorial.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class UserEntity extends BaseEntity implements Serializable {

    @Id
    private String id;


    @Column(name="password")
    private String password;

    @Basic
    @Column(name="phone_number")
    private String phoneNumber;

    @Basic
    @Column(name="status")
    private Integer status;



    public UserEntity(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.status = 0;

    }

}
