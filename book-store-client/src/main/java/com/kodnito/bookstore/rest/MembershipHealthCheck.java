package com.kodnito.bookstore.rest;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

@Health
@ApplicationScoped
public class MembershipHealthCheck implements HealthCheck {
	@Inject
    private DataSource datasource;
	
	@Override
	public HealthCheckResponse call() {
		// TODO Auto-generated method stub

        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("membership");
        try {
            Connection connection = datasource.getConnection();
            boolean isValid = connection.isValid(timeout);

            DatabaseMetaData metaData = connection.getMetaData();

            responseBuilder = responseBuilder
                    .withData("databaseProductName", metaData.getDatabaseProductName())
                    .withData("databaseProductVersion", metaData.getDatabaseProductVersion())
                    .withData("driverName", metaData.getDriverName())
                    .withData("driverVersion", metaData.getDriverVersion())
                    .withData("isValid", isValid);

            return responseBuilder.state(isValid).build();


        } catch(SQLException  e) {
           // log.log(Level.SEVERE, null, e);
            responseBuilder = responseBuilder
                    .withData("exceptionMessage", e.getMessage());
            return responseBuilder.down().build();
        }
    }
	
	@Inject @ConfigProperty(name = "health.membership.dbtimeout", defaultValue = "5")
    private int timeout;
}
