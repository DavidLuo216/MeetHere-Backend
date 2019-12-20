package cn.ecnuer996.meetHereBackend.model;

public class UserAuth {
    private Integer userId;

    private String identityType;

    private String identifier;

    private String credential;

    public UserAuth(){

    }

    public UserAuth(String identityType,String identifier,String credential){
        this.userId = null;
        this.identityType = identityType;
        this.identifier = identifier;
        this.credential = credential;
    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType == null ? null : identityType.trim();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier == null ? null : identifier.trim();
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential == null ? null : credential.trim();
    }

}