package ua.softgroup.matrix.desktop.model;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class UserProfile {
    private Long id;
    private String username;
    private String trackerToken;
    private String password;
    private Integer externalHourlyRate;
    private Integer externalHourlyRateCurrencyId;
    private Integer internalHourlyRate;
    private Integer internalHourlyRateCurrencyId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrackerToken() {
        return trackerToken;
    }

    public void setTrackerToken(String trackerToken) {
        this.trackerToken = trackerToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getExternalHourlyRate() {
        return externalHourlyRate;
    }

    public void setExternalHourlyRate(Integer externalHourlyRate) {
        this.externalHourlyRate = externalHourlyRate;
    }

    public Integer getExternalHourlyRateCurrencyId() {
        return externalHourlyRateCurrencyId;
    }

    public void setExternalHourlyRateCurrencyId(Integer externalHourlyRateCurrencyId) {
        this.externalHourlyRateCurrencyId = externalHourlyRateCurrencyId;
    }

    public Integer getInternalHourlyRate() {
        return internalHourlyRate;
    }

    public void setInternalHourlyRate(Integer internalHourlyRate) {
        this.internalHourlyRate = internalHourlyRate;
    }

    public Integer getInternalHourlyRateCurrencyId() {
        return internalHourlyRateCurrencyId;
    }

    public void setInternalHourlyRateCurrencyId(Integer internalHourlyRateCurrencyId) {
        this.internalHourlyRateCurrencyId = internalHourlyRateCurrencyId;
    }

    @Override
    public String toString() {
        return  username ;
    }
}
