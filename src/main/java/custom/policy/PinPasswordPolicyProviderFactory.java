package custom.policy;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.policy.PasswordPolicyProvider;
import org.keycloak.policy.PasswordPolicyProviderFactory;

public class PinPasswordPolicyProviderFactory implements PasswordPolicyProviderFactory {
    private final String PROVIDER_NAME = "Pin Password";
    public static final String PROVIDER_ID = "pin_password_policy";
    public static final int DEFAULT_VALUE = 4;

    @Override
    public String getDisplayName() {
        return PROVIDER_NAME;
    }

    @Override
    public String getConfigType() {
        return PasswordPolicyProvider.INT_CONFIG_TYPE;
    }

    @Override
    public String getDefaultConfigValue() {
        return Integer.toString(DEFAULT_VALUE);
    }

    @Override
    public boolean isMultiplSupported() {
        return false;
    }

    @Override
    public PasswordPolicyProvider create(KeycloakSession session) {
        return new PinPasswordPolicyProvider(session.getContext());
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
