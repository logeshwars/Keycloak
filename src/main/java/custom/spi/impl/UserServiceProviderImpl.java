package custom.spi.impl;

import custom.jpa.CustomUserEntity;
import custom.spi.UserRepresentation;
import custom.spi.UserService;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.KeycloakModelUtils;

import javax.persistence.EntityManager;
import java.util.LinkedList;
import java.util.List;

public class UserServiceProviderImpl implements UserService {

    private KeycloakSession session;

    public UserServiceProviderImpl(KeycloakSession session) {
        this.session = session;
    }

    private EntityManager getEntityManager() {
        return session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

    private RealmModel getRealm() {
        return session.getContext().getRealm();
    }

    @Override
    public List<UserRepresentation> getUsers() {
        List<CustomUserEntity> userEntities = getEntityManager().createNamedQuery("findByRealm", CustomUserEntity.class)
                .setParameter("realmId", getRealm().getId())
                .getResultList();

        List<UserRepresentation> result = new LinkedList<>();
        for (CustomUserEntity entity : userEntities) {
            result.add(new UserRepresentation(entity));
        }
        return result;
    }

    @Override
    public UserRepresentation getUser(String id) {
        CustomUserEntity entity = getEntityManager().find(CustomUserEntity.class, id);
        return entity==null ? null : new UserRepresentation(entity);
    }

    @Override
    public UserRepresentation addUser(UserRepresentation user) {
        CustomUserEntity entity = new CustomUserEntity();
        String id = user.getId()==null ?  KeycloakModelUtils.generateId() : user.getId();
        entity.setId(id);
        entity.setUsername(user.getUsername());
        entity.setRealmId(getRealm().getId());
        getEntityManager().persist(entity);
        user.setId(id);
        return user;
    }

    @Override
    public void close() {

    }
}
