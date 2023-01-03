package custom.spi;

import custom.jpa.CustomUserEntity;

public class UserRepresentation {

    private String id ;
    private String name;

    public UserRepresentation(CustomUserEntity customUserEntity)
    {

        this.id = customUserEntity.getId();
        this.name = customUserEntity.getUsername();
    }

    public String getUsername() {
        return name;
    }

    public void setUsername(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
