package custom.eventlisteners;

import custom.jpa.CustomUserEntity;
import custom.spi.UserRepresentation;
import custom.spi.UserService;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

import org.jboss.logging.Logger;
import org.keycloak.models.UserModel;

public class UserRegisterationEventListenerProvider implements EventListenerProvider {
    private final KeycloakSession session;

    private final Logger logger = Logger.getLogger(UserRegisterationEventListenerProvider.class);

    public UserRegisterationEventListenerProvider(KeycloakSession keycloakSession) {
        this.session = keycloakSession;
    }

    @Override
    public void onEvent(Event event) {
        if(event.getType() == EventType.REGISTER)
        {
            CustomUserEntity userEntity = new CustomUserEntity();
            UserModel user = session.users().getUserById(session.getContext().getRealm(),event.getUserId());
            userEntity.setUsername(user.getUsername());
            userEntity.setEmail(user.getEmail(),false);
            userEntity.setFirstName(user.getFirstName());
            userEntity.setEnabled(user.isEnabled());
            userEntity.setRealmId(session.getContext().getRealm().getId());
            userEntity.setEmailConstraint(user.getEmail());
            userEntity.setCreatedTimestamp(user.getCreatedTimestamp());
            userEntity.setEmailVerified(user.isEmailVerified());
            session.getProvider(UserService.class).addUser(new UserRepresentation(userEntity));
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
    }

    @Override
    public void close() {

    }
}
