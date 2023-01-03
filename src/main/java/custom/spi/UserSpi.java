package custom.spi;

import org.hibernate.criterion.Example;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

public class UserSpi implements Spi {
    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return "Custom_user_spi";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return UserService.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return UserServiceProviderFactory.class;
    }
}
