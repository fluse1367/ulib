package eu.software4you.ulib.core.reflect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class CallFrame {
    private final String name;
    private final boolean field;
    private final List<Param<?>> params;
}
