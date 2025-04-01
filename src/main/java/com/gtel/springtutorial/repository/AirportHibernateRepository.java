package com.gtel.springtutorial.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public class AirportHibernateRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Object[]> getAirports(int page, int size) {
        int offset = (page -1 ) * size;
        Session session = entityManager.unwrap(Session.class);
        String sqlString = "Select * from airports limit "+ String.valueOf(size) + " offset " + String.valueOf(offset) + ";";
        StringBuffer sb = new StringBuffer();
        sb.append(sqlString);
        NativeQuery<Object[]> query = session.createNativeQuery(sqlString);

        return query.getResultList();
    }
}
