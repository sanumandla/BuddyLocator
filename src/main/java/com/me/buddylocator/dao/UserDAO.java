package com.me.buddylocator.dao;

import com.me.buddylocator.Constants;
import com.me.buddylocator.model.User;
import com.me.buddylocator.representations.UserJson;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Objects;

/**
 * Data Access Object class for User table
 *
 * @author Sridhar Anumandla
 */
public class UserDAO extends AbstractDAO<User> {

    public UserDAO(SessionFactory factory) {
        super(factory);
    }

    public User findById(Long id) {
        return get(id);
    }

    public long create(User user) {
        return persist(user).getId();
    }

    public User findByPhoneNumber(String phoneNumber) {
        return (User) criteria().add(Restrictions.eq(Constants.PHONE_NUMBER, phoneNumber)).uniqueResult();
    }

    public User findByIMEI(String imei) {
        return (User) criteria().add(Restrictions.eq(Constants.IMEI, imei)).uniqueResult();
    }

    public User findByEmail(String email) {
        return (User) criteria().add(Restrictions.eq(Constants.EMAIL, email)).uniqueResult();
    }

    public List<User> findAll() {
        return list(criteria());
    }

}
