package alexthw.starbunclemania.starbuncle;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface AuthorizedBehavior {

    @NotNull
    UUID getOwnerUUID();

    void setOwnerUUID(UUID ownerUUID);

}
