package eu.software4you.ulib.minecraft.api.proxybridge.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class Message {
    private UUID id;

    private String from;

    private MessageType type;

    private byte[] data;

}
