package eu.software4you.ulib.minecraft.proxybridge.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class Message {
    @NotNull
    private UUID id;

    @NotNull
    private String from;

    @NotNull
    private MessageType type;

    private byte[] data;

}
