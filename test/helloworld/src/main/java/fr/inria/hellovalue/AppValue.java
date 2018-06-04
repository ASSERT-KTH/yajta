package fr.inria.hellovalue;

import java.util.ArrayList;
import java.util.List;

public class AppValue {
    public static void main(String[] args) {
        GetValue a = new GetValue();

        a.getByte();
        a.getShort();
        a.getInt();
        a.getLong();
        a.getFloat();
        a.getDouble();
        a.getBoolean();
        a.getString();
        a.getBooleans();
        a.getStrings();

        List<String> l = new ArrayList<>();
        List<String> l2 = new ArrayList<>();
        for(String s: l) {
            l2.add(s.toLowerCase());
        }
        l.stream().forEach(s -> l2.add(s));
        l.stream().parallel().forEach(s -> l2.add(s));

    }

}
