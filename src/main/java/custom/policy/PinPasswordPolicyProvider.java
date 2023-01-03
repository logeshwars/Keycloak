package custom.policy;

import org.keycloak.models.KeycloakContext;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.policy.PasswordPolicyProvider;
import org.keycloak.policy.PolicyError;

public class PinPasswordPolicyProvider implements PasswordPolicyProvider {
    private final String PIN_ERROR_MESSAGE = "Password Must Be Pin Number";
    private  final String MAX_ERROR_MESSAGE = "Pin Max Length";

    private final KeycloakContext context;
    public PinPasswordPolicyProvider(KeycloakContext context) {
        this.context = context;
    }

    @Override
    public PolicyError validate(RealmModel realm, UserModel user, String password) {
     return validate(user.getUsername(),password);
    }

    @Override
    public PolicyError validate(String user, String password) {
        int max = context.getRealm().getPasswordPolicy().getPolicyConfig(PinPasswordPolicyProviderFactory.PROVIDER_ID);
        boolean matches = password.matches("[0-9]+");
        if(!matches)
        {
            return new PolicyError(PIN_ERROR_MESSAGE);
        }
        return password.length()<max ? new PolicyError(MAX_ERROR_MESSAGE,max) : null;
    }

    @Override
    public Object parseConfig(String value) {
        return parseInteger(value,PinPasswordPolicyProviderFactory.DEFAULT_VALUE);
    }

    @Override
    public void close() {

    }
}
