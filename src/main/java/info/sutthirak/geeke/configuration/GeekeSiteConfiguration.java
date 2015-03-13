package info.sutthirak.geeke.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeekeSiteConfiguration {

    @JsonProperty
    private String url;

    @JsonProperty
    private int intervalInMinute;

    @JsonProperty
    private String slackChannel;

    @JsonProperty
    private String username;

    @JsonProperty
    private String linkPattern;

    @JsonProperty
    private boolean absoluteTarget;

    public int getIntervalInMinute() {
        return intervalInMinute;
    }

    public String getSlackChannel() {
        return slackChannel;
    }

    public String getUsername() {
        return username;
    }

    public String getUrl() {
        return url;
    }

    public String getLinkPattern() {
        return linkPattern;
    }

    public boolean isAbsoluteTarget() {
        return absoluteTarget;
    }
}
