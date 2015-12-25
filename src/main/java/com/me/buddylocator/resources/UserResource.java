package com.me.buddylocator.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.buddylocator.dao.UserDAO;
import com.me.buddylocator.model.User;
import com.me.buddylocator.representations.UserJson;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * API to update user information along with location
 *
 * @author Sridhar Anumandla
 */

@Path("/user")
@Produces(value = MediaType.APPLICATION_JSON)
@Consumes(value = MediaType.APPLICATION_JSON)
public class UserResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    private UserDAO userDAO;
    private final ObjectMapper objectMapper;

    public UserResource(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    @GET
    @Timed
    @UnitOfWork
    @Path("{id}")
    @Produces("application/json")
    public Response getUserById(@PathParam("id") long id) {
        LOGGER.debug("Get user with id = " + id);
        User user = userDAO.findById(id);
        return Response.ok(user).build();
    }

    @GET
    @Timed
    @UnitOfWork
    @Produces("application/json")
    public Response getUser(@QueryParam("phone") String phone, @QueryParam("imei") String imei, @QueryParam("email") String email) {
        User user;
        if (phone != null && !phone.isEmpty()) {
            LOGGER.debug("Get user with phone number = " + phone);
            user = userDAO.findByPhoneNumber(phone);
        } else if (imei != null && !imei.isEmpty()) {
            LOGGER.debug("Get user with imei number = " + imei);
            user = userDAO.findByIMEI(imei);
        } else if (email != null && !email.isEmpty()) {
            LOGGER.debug("Get user with email = " + email);
            user = userDAO.findByEmail(email);
        } else {
            return Response.status(400).entity("Missing phone number / imei / email").build();
        }

        return Response.ok(user).build();
    }

    @GET
    @Timed
    @UnitOfWork
    @Path("/list")
    @Produces("application/json")
    public Response getUsers() {
        LOGGER.debug("Get list of users");
        List<User> userList = userDAO.findAll();
        return Response.ok(userList).build();
    }

    @POST
    @Timed
    @UnitOfWork
    @Consumes("application/json")
    public Response createUser(String json) {
        if (json == null || json.isEmpty()) {
            LOGGER.error("Invalid request body");
            return Response.status(400).entity("Invalid request body").build();
        }

        LOGGER.debug("Create user: " + json);

        try {
            Date date = new Date();
            UserJson userJson = objectMapper.readValue(json, UserJson.class);

            User user = new User();
            user.setPhoneNumber(userJson.getPhoneNumber());
            user.setImei(userJson.getImei());
            user.setEmail(userJson.getEmail());
            user.setFirstName(userJson.getFirstName());
            user.setLastName(userJson.getLastName());
            user.setPetName(userJson.getPetName());
            user.setNotified(false);
            user.setDateCreated(date.toString());
            user.setDateUpdated(date.toString());
            long userId = userDAO.create(user);
            return Response.status(200).entity(userId).build();

        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Error in parsing json body: " + e.getMessage());
            return Response.status(400).entity("Error in parsing json body: " + e.getMessage()).build();
        }
    }

    @PUT
    @Timed
    @UnitOfWork
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateUser(String json) {
        if (json == null || json.isEmpty()) {
            LOGGER.error("Invalid request body");
            return Response.status(400).entity("Invalid request body").build();
        }

        LOGGER.debug("Create user: " + json);

        try {
            Date date = new Date();
            UserJson userJson = objectMapper.readValue(json, UserJson.class);

            User dbUser = userDAO.findByPhoneNumber(userJson.getPhoneNumber());
            if (userJson.getImei() != null && !userJson.getImei().isEmpty()) {
                dbUser.setImei(userJson.getImei());
            }

            if (userJson.getEmail() != null && !userJson.getEmail().isEmpty()) {
                dbUser.setEmail(userJson.getEmail());
            }

            dbUser.setFirstName(userJson.getFirstName());
            dbUser.setLastName(userJson.getLastName());
            dbUser.setPetName(userJson.getPetName());
            dbUser.setNotified(false);
            dbUser.setDateUpdated(date.toString());
            long userId = userDAO.create(dbUser);
            return Response.status(200).entity(userId).build();

        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Error in parsing json body: " + e.getMessage());
            return Response.status(400).entity("Error in parsing json body: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Timed
    @UnitOfWork
    @Path("{id}")
    public Response deleteUser(@PathParam("id") String id) {
        LOGGER.debug("Delete user with id: " + id);
        return Response.noContent().build();
    }

}
