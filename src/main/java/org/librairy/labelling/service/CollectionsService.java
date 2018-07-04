package org.librairy.labelling.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class CollectionsService {

    private static final Logger LOG = LoggerFactory.getLogger(CollectionsService.class);

    public static List<String> combine(List<List<String>> list){
        return list.stream().reduce((a, b) -> join(a, b)).get().stream().distinct().collect(Collectors.toList());
    }

    public static List<String> permute(List<String> list){
        if (list.isEmpty()) return Collections.emptyList();
        if (list.size() == 1) return list;
        return combine(list.get(0), list.subList(1,list.size()));
    }

    public static List<String> combine(String x, List<String> list){
        return list.stream().flatMap( w -> {
            String ref = (w.compareTo(x)<0)? w + " "+ x : x +" " + w;
//            List<String> l2 = new ArrayList(list);
            int index = list.indexOf(w);
//            l2.remove(w);
            List<String> l1 = new ArrayList<String>();
            l1.add(ref);
            l1.addAll(combine(ref,list.subList(index+1, list.size())).stream().collect(Collectors.toList()));
            return l1.stream();

        }).collect(Collectors.toList());
    }


    public static List<String> join(List<String> l1, List<String> l2){
        return l1.stream().flatMap(x -> l2.stream().map(y -> x + " " + y)).collect(Collectors.toList());
    }

    public static List union(List l1, List l2){
        l1.addAll(l2);
        return l1;
    }

    public static List<Double> sum(List<? extends Number> l1, List<? extends Number> l2){

        if (l1.size() != l2.size()) return Collections.emptyList();

        List l3 = new ArrayList();

        for(int i=0;i<l1.size();i++){
            l3.add(l1.get(i).doubleValue()+l2.get(i).doubleValue());
        }
        return l3;

    }


}
