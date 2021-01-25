package eu.software4you.spigot.enchantment;

import java.util.List;
import java.util.Random;

class NMSWeightedRandom
{
    /**
     * Returns the total weight of all items in a collection.
     */
    static int getTotalWeight(List <? extends Weightable> collection)
    {
        int i = 0;
        int j = 0;

        for (int k = collection.size(); j < k; ++j)
        {
            Weightable weightable = collection.get(j);
            i += weightable.weight;
        }

        return i;
    }

    static <T extends Weightable> T getRandomItem(Random random, List<T> collection, int totalWeight)
    {
        if (totalWeight <= 0)
        {
            throw new IllegalArgumentException();
        }
        else
        {
            int i = random.nextInt(totalWeight);
            return (T)getRandomItem(collection, i);
        }
    }

    static <T extends Weightable> T getRandomItem(List<T> collection, int weight)
    {
        int i = 0;

        for (int j = collection.size(); i < j; ++i)
        {
            T t = collection.get(i);
            weight -= t.weight;

            if (weight < 0)
            {
                return t;
            }
        }

        return (T)null;
    }

    static <T extends Weightable> T getRandomItem(Random random, List<T> collection)
    {
        return (T)getRandomItem(random, collection, getTotalWeight(collection));
    }

    static class Weightable
    {
        protected int weight;

        Weightable(int itemWeightIn)
        {
            this.weight = itemWeightIn;
        }
    }
}