package org.sdrc.scslmobile.customclass;

import org.sdrc.scslmobile.model.realm.Indicator;

import java.util.Comparator;

/**
 * Created by Jagat Bandhu Sahoo(jagat@sdrc.co.in) on 6/5/2017.
 */

public class CustomComparator implements Comparator<Indicator> {
    @Override
    public int compare(Indicator o1, Indicator o2) {
        return o1.getIndicatorOrder() - o2.getIndicatorOrder();
    }
}