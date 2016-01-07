package cn.lbgongfu.multiddns.models;

/**
 * Created by gf on 2016/1/4.
 */
public class Domain {
    private int id;
    private String domain;
    private String password;
    private String registerDate;
    private String effectiveDate;
    private String activeDate;
    private String ip;
    private String resolveInterval;
    private String redirectCheck;
    private String redirectURL;
    //备案号
    private String reference;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(String activeDate) {
        this.activeDate = activeDate;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getResolveInterval() {
        return resolveInterval;
    }

    public void setResolveInterval(String resolveInterval) {
        this.resolveInterval = resolveInterval;
    }

    public String getRedirectCheck() {
        return redirectCheck;
    }

    public void setRedirectCheck(String redirectCheck) {
        this.redirectCheck = redirectCheck;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return getDomain();
    }
}
