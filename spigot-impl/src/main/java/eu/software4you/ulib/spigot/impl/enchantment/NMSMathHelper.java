package eu.software4you.ulib.spigot.impl.enchantment;

class NMSMathHelper {
    static int clamp(int num, int min, int max) {
        if (num < min) {
            return min;
        } else {
            return Math.min(num, max);
        }
    }
}
