package custom.jpa;

import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;

import java.util.Collections;
import java.util.List;

public class UserEntityJpaProvider implements JpaEntityProvider {
    @Override
    public List<Class<?>> getEntities() {
        return Collections.<Class<?>>singletonList(CustomUserEntity.class);
    }

    @Override
    public String getChangelogLocation() {
        return "META-INF/jpa-changelog-1.0.0.xml";
    }

    @Override
    public String getFactoryId() {
        return UserEntityJpaProviderFactory.FACTORY_ID;
    }

    @Override
    public void close() {

    }
}
