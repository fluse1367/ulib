package eu.software4you.litetransform;

import eu.software4you.litetransform.injection.Inject;
import eu.software4you.litetransform.injection.InjectionPoint;

public class TestMod {
    @Inject(method = "tar", signature = "(Ljava/lang/String;I)Z", clazz = "eu/software4you/litetransform/TestMod",
            at = InjectionPoint.RETURN)
    public void src(String name, int date, Callback<Boolean> cb) {
        // my code
        cb.setReturnValue(false);
    }

    public boolean tar(String name, int date) {
        return name.hashCode() == date;
    }
}
