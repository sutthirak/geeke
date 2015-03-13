package info.sutthirak.geeke;

import info.sutthirak.geeke.configuration.GeekeConfiguration;
import info.sutthirak.geeke.configuration.GeekeSiteConfiguration;
import info.sutthirak.geeke.persistence.SiteDAO;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MINUTES;

public class GeekeApplication extends Application<GeekeConfiguration> {

    private static final Logger log = LoggerFactory.getLogger(GeekeApplication.class);

    public static void main(String[] args) throws Exception {
        new GeekeApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<GeekeConfiguration> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<GeekeConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(GeekeConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(GeekeConfiguration configuration, Environment environment) {

        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "mysql");
        final SiteDAO siteDAO = jdbi.onDemand(SiteDAO.class);
        final JerseyClient jerseyClient = JerseyClientBuilder.createClient();

        ScheduledExecutorService execService = environment.lifecycle().scheduledExecutorService("geeke-%d").build();
        for(GeekeSiteConfiguration geekeSiteConfiguration:configuration.getSiteConfigurationList()) {
            execService.scheduleAtFixedRate(() -> {
                try {
                    log.info("start fetching job for = {}",geekeSiteConfiguration.getUrl());
                    Document doc = Jsoup.connect(geekeSiteConfiguration.getUrl()).get();
                    Elements titles = doc.select(geekeSiteConfiguration.getLinkPattern());
                    for (Element title : titles) {
                        Attributes attributes = title.attributes();
                        int count = siteDAO.findByLink(attributes.get("href"));
                        if (count == 0) {
                            log.info("found new title = {}",title.text());

                            String link = attributes.get("href");
                            if(!geekeSiteConfiguration.isAbsoluteTarget()){
                                link = geekeSiteConfiguration.getUrl()+link;
                            }

                            String message = URLEncoder.encode(title.text() + " (" + link + ")", "UTF-8");
                            String target = String.format(generateSlackUrl(configuration.getSlackToken(),geekeSiteConfiguration), message);
                            WebTarget webTarget = jerseyClient.target(target);
                            webTarget.request().get();
                            siteDAO.insert(geekeSiteConfiguration.getUrl(), attributes.get("href"));
                        }
                    }
                    log.info("done !!!");
                } catch (IOException e) {
                    log.error("found an error",e);
                }
            },geekeSiteConfiguration.getIntervalInMinute(), 1, MINUTES);
        }

    }

    private String generateSlackUrl(String token,GeekeSiteConfiguration geekeSiteConfiguration){
        StringBuilder uri = new StringBuilder();
        uri.append("https://slack.com/api/chat.postMessage?token=");
        uri.append(token);
        uri.append("&channel=");
        uri.append(geekeSiteConfiguration.getSlackChannel());
        uri.append("&text=%s&username=");
        uri.append(geekeSiteConfiguration.getUsername());
        return uri.toString();
    }

}
