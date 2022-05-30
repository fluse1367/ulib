package eu.software4you.ulib.core.reflect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class CallFrame {
    @NotNull
    private final String name;
    private final boolean field;
    @NotNull
    private final List<Param<?>> params;
}
