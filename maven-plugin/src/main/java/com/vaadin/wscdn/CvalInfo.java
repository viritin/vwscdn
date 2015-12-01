package com.vaadin.wscdn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.beans.Transient;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Class used for binding the JSON gotten from server.
 *
 * It is not in a separate f le, so as it is easier to copy into any product
 * which does not depend on vaadin core.
 *
 * We are using elemental.json in order not to use additional dependency
 * like auto-beans, gson, etc.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CvalInfo {

    private Product product;
    private Boolean expired;
    private long expiredEpoch;

    private String startDate;
    private String expires;
    private String licenseKey;
    private String email;
    private String licensee;
    private String licenseName;
    private Boolean inUse;
    private Boolean nonProfit;
    private String type;
    private String message;

    @Transient
    public Date getExpiredEpochDate() {
        return new Date(getExpiredEpoch());
    }

    public long getExpiredEpoch() {
        return expiredEpoch;
    }

    public void setExpiredEpoch(long expiredEpoch) {
        this.expiredEpoch = expiredEpoch;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public String getLicensee() {
        return licensee;
    }

    public void setLicensee(String licensee) {
        this.licensee = licensee;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLicenseName() {
        return licenseName;
    }

    public void setLicenseName(String licenseName) {
        this.licenseName = licenseName;
    }

    public Boolean getInUse() {
        return inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
    }

    public Boolean getNonProfit() {
        return nonProfit;
    }

    public void setNonProfit(Boolean nonProfit) {
        this.nonProfit = nonProfit;
    }

    public boolean isLicenseExpired() {
        return (getExpired() != null && getExpired()) || (getExpiredEpochDate() != null && getExpiredEpochDate().
                before(new Date()));
    }

    public boolean isValidVersion(int majorVersion) {
        return getProduct().getVersion() == null || getProduct().getVersion() >= majorVersion;
    }

    boolean isValidInfo(String name, String key) {
        return getProduct() != null && getProduct().getName() != null && getLicenseKey() != null && getProduct().
                getName().equals(name) && getLicenseKey().equals(key);
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(CvalInfo.class.getName()).log(Level.SEVERE, null,
                    ex);
            return "{}";
        }
    }

}
