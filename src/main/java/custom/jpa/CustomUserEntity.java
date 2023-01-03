package custom.jpa;



import org.keycloak.models.utils.KeycloakModelUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "USERS")
@NamedQuery(name="findUserByRealmId",query = "from Users where realmId = :realmId")



public class CustomUserEntity {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name="EMAIL")
    private  String email;

    @Column(name = "EMAIL_CONSTRAINT")
    private String emailConstraint;

    @Column(name="EMAIL_VERIFIED")
    private Boolean emailVerified;

    @Column(name="ENABLED")
    private Boolean enabled;

    @Column(name="FEDERATION_LINK")
    private String federation_link;

    @Column(name="FIRST_NAME")
    private String firstName;

    @Column(name="LAST_NAME")
    private  String lastName;

    @Column(name="REALM_ID")
    private String realmId;

    @Column(name="USERNAME")
    private String username;

    @Column(name="CREATED_TIMESTAMP")
    private Long createdTimestamp;

    @Column(name="SERVICE_ACCOUNT_CLIENT_LINK")
    private String serviceAccountClientLink;

    @Column(name="NOT_BEFORE")
    private int notBefore;


    public  String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Long timestamp) {
        createdTimestamp = timestamp;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email, boolean allowDuplicate) {
        this.email = email;
        this.emailConstraint = email == null || allowDuplicate ? KeycloakModelUtils.generateId() : email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmailConstraint() {
        return emailConstraint;
    }

    public void setEmailConstraint(String emailConstraint) {
        this.emailConstraint = emailConstraint;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }


    public void setRealmId(String realmId) {
        this.realmId = realmId;
    }
}
