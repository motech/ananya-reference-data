package org.motechproject.ananya.referencedata.csv.utils;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;

import java.util.Collection;
import java.util.Iterator;

public class CollectionUtils extends org.apache.commons.collections.CollectionUtils {
    public static void forAllDo(Collection collection, Predicate predicate, Closure closure) {
        if (collection != null && closure != null) {
            for (Iterator it = collection.iterator(); it.hasNext(); ) {
                Object o = it.next();
                if (predicate.evaluate(o))
                    closure.execute(o);
            }
        }
    }
}
