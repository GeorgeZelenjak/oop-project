package nl.tudelft.oopp.livechat.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Objects;


/**
 * Configuration for Hibernate to communicate with the database.
 * @PropertySource reads the specified file to get configs
 *      For specific fields see the provided application-dev.properties and
 *      application-postgres.properties.
 */
@Configuration
@EnableJpaRepositories("nl.tudelft.oopp.livechat")
//change to application-postgres.properties to use postgres
@PropertySource("classpath:./application-dev.properties")
@EnableTransactionManagement
public class H2Config {

    @Autowired
    private Environment environment;

    /**
     * Set up the connection to the database.
     * @return the data source
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(
                Objects.requireNonNull(environment.getProperty("jdbc.driverClassName")));
        dataSource.setUrl(environment.getProperty("jdbc.url"));
        dataSource.setUsername(environment.getProperty("jdbc.user"));
        dataSource.setPassword(environment.getProperty("jdbc.pass"));

        return dataSource;
    }
}
