package custom.spi;

import org.keycloak.provider.Provider;

import java.util.List;

public interface UserService extends Provider
{
 List<UserRepresentation> getUsers();
 UserRepresentation getUser(String id);

 UserRepresentation addUser(UserRepresentation user);


}
