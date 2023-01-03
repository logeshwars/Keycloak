package custom.spi.impl;

import custom.spi.UserService;
import custom.spi.UserServiceProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class UserServiceProviderFactoryImpl implements UserServiceProviderFactory {
    public final String PROVIDER_ID = "customUserServiceProvider";
    @Override
    public UserService create(KeycloakSession session) {
        return new UserServiceProviderImpl(session);
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
