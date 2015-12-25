package com.me.buddylocator.service;

import com.me.buddylocator.Constants;
import com.me.buddylocator.configuration.HibernateConfiguration;
import com.me.buddylocator.dao.UserDAO;
import com.me.buddylocator.model.User;
import com.me.buddylocator.representations.UserJson;
import com.me.buddylocator.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 * Service class to establish connection with the database
 *
 * @author Sridhar Anumandla
 */
public class DBApplication extends Application<HibernateConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBApplication.class);

    private final HibernateBundle<HibernateConfiguration> hibernate = new HibernateBundle<HibernateConfiguration>(User.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(HibernateConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    private final MigrationsBundle<HibernateConfiguration> migration = new MigrationsBundle<HibernateConfiguration>() {
        @Override
        public DataSourceFactory getDataSourceFactory(HibernateConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            LOGGER.error("Missing arguments. Please follow the instructions\n");
            LOGGER.info("args: db migrate <path/to/config.yml>\n");
            LOGGER.info("<< OR >>\n");
            LOGGER.info("args: server <path/to/config.yml>\n");
            return;
        }

        if (args.length == 2) {
            if (!args[0].trim().equals("server") || args[1].isEmpty()) {
                LOGGER.error("Invalid arguments");
                return;
            }
        } else if (args.length == 3) {
            if (!args[0].trim().equals("db") || !args[1].trim().equals("migrate") || args[2].isEmpty()) {
                LOGGER.error("Invalid arguments");
                return;
            }
        } else {
            LOGGER.error("2 or 3 arguments required but found more\n");
            return;
        }

        new DBApplication().run(args);
    }

    @Override
    public String getName() {
        return DBApplication.class.getName();
    }

    @Override
    public void initialize(Bootstrap<HibernateConfiguration> bootstrap) {
        LOGGER.debug("Initializing DBService class object and adding bundles to it...");
        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(migration);
    }

    @Override
    public void run(HibernateConfiguration hibernateConfiguration, Environment environment) throws Exception {
        final UserDAO userDAO = new UserDAO(hibernate.getSessionFactory());
        environment.jersey().register(new UserResource(userDAO));
    }

}
