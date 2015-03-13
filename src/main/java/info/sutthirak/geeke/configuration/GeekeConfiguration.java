package info.sutthirak.geeke.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class GeekeConfiguration extends Configuration {

    @JsonProperty
    private String slackToken;

    public String getSlackToken() {
        return slackToken;
    }

    @JsonProperty("site")
    private List<GeekeSiteConfiguration> siteConfigurationList;

    public List<GeekeSiteConfiguration> getSiteConfigurationList() {
        return siteConfigurationList;
    }

    @Valid
    @NotNull
    @JsonProperty("httpClient")
    private JerseyClientConfiguration httpClient = new JerseyClientConfiguration();

    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return httpClient;
    }

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

}
