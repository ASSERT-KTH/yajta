package fr.inria.align;

import org.json.JSONException;
import org.junit.Test;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class SyscallNGramTest {

    //@Test
    public void testDistances() throws JSONException {
        double delta = Double.MIN_VALUE * 100;
        File f1 = new File("/home/nharrand/out.json");
        File f2 = new File("/home/nharrand/out.json");
        SyscallNGram a = new SyscallNGram(f1,"practical_wescoff");
        SyscallNGram b = new SyscallNGram(f2,"sad_yalow");
        assertEquals(a.distance(b),b.distance(a), delta);
        System.out.println("d: " + a.distance(b));
    }

    //@Test
    public void testDistances2() throws JSONException {
        File f1 = new File("/home/nharrand/Documents/Seminaire/syscall_matrices_comp.json");
        Map<String, SyscallNGram> mat = new HashMap<>();
        mat.put("olivier", new SyscallNGram(f1,"wonderful_wozniak"));
        mat.put("max", new SyscallNGram(f1,"gracious_jepsen"));
        mat.put("manu", new SyscallNGram(f1,"brave_haibt"));
        mat.put("ludo", new SyscallNGram(f1,"musing_stonebraker"));
        mat.put("kevin", new SyscallNGram(f1,"focused_bhaskara"));
        mat.put("didier", new SyscallNGram(f1,"blissful_wing"));
        mat.put("caro", new SyscallNGram(f1,"eloquent_hawking"));
        mat.put("amine2", new SyscallNGram(f1,"vigorous_mclean"));
        mat.put("alex", new SyscallNGram(f1,"blissful_bassi"));

        File f2 = new File("/home/nharrand/Documents/Seminaire/syscall_matrices_comp2.json");
        mat.put("login-olivier", new SyscallNGram(f2,"serene_chandrasekhar"));
        mat.put("login-max", new SyscallNGram(f2,"jovial_ramanujan"));
        mat.put("login-manu", new SyscallNGram(f2,"heuristic_hoover"));
        mat.put("login-ludo", new SyscallNGram(f2,"tender_bohr"));
        mat.put("login-kevin", new SyscallNGram(f2,"optimistic_noether"));
        mat.put("login-didier", new SyscallNGram(f2,"romantic_wilson"));
        mat.put("login-caro", new SyscallNGram(f2,"festive_booth"));
        mat.put("login-amine2", new SyscallNGram(f2,"xenodochial_bartik"));
        mat.put("login-alex", new SyscallNGram(f2,"romantic_lovelace"));

        File f3 = new File("/home/nharrand/Documents/Seminaire/syscall_matrices_comp3.json");
        mat.put("failures-olivier", new SyscallNGram(f3,"keen_stonebraker"));
        mat.put("failures-max", new SyscallNGram(f3,"agitated_jang"));
        mat.put("failures-manu", new SyscallNGram(f3,"youthful_kowalevski"));
        mat.put("failures-ludo", new SyscallNGram(f3,"romantic_rosalind"));
        mat.put("failures-kevin", new SyscallNGram(f3,"naughty_perlman"));
        mat.put("failures-didier", new SyscallNGram(f3,"fervent_snyder"));
        mat.put("failures-caro", new SyscallNGram(f3,"affectionate_jones"));
        mat.put("failures-amine2", new SyscallNGram(f3,"cocky_cori"));
        mat.put("failures-alex", new SyscallNGram(f3,"xenodochial_shaw"));

        File f4 = new File("/home/nharrand/Documents/Seminaire/syscall_matrices_comp4.json");
        mat.put("still-olivier", new SyscallNGram(f4,"condescending_banach"));
        mat.put("still-max", new SyscallNGram(f4,"thirsty_khorana"));
        mat.put("still-manu", new SyscallNGram(f4,"elastic_swirles"));
        mat.put("still-ludo", new SyscallNGram(f4,"zen_almeida"));
        mat.put("still-kevin", new SyscallNGram(f4,"jovial_ardinghelli"));
        mat.put("still-didier", new SyscallNGram(f4,"blissful_lalande"));
        mat.put("still-caro", new SyscallNGram(f4,"hardcore_lewin"));
        mat.put("still-amine2", new SyscallNGram(f4,"inspiring_chandrasekhar"));
        mat.put("still-alex", new SyscallNGram(f4,"objective_bardeen"));


        HashMap<String, Double> map = new HashMap<String, Double>();
        ValueComparator bvc = new ValueComparator(map);
        TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);

        for(String t1: mat.keySet()) {
            for(String t2: mat.keySet()) {
                //System.out.println(t1 + " - " + t2 + ": " + mat.get(t1).distance(mat.get(t2)));
                //if(!t1.equals(t2)) map.put(t1 + " - " + t2, mat.get(t1).distance(mat.get(t2)));
                //if(!t1.equals(t2)) map.put(t1 + " - " + t2, mat.get(t1).distance2(mat.get(t2)));
                if(!t1.equals(t2)) map.put(t1 + " - " + t2, mat.get(t1).distance3(mat.get(t2)));
            }
        }

        //System.out.println("unsorted map: " + map);
        sorted_map.putAll(map);
        for(Map.Entry test: sorted_map.entrySet()) {
            System.out.println(test.getKey() + ": " + getSpaces(40 - ((String) test.getKey()).length()) + test.getValue());
        }
        //System.out.println("results: " + sorted_map);
    }

    public static String getSpaces(int i) {
        String res = "";
        for(int j = 0; j < i; j++) res += " ";
        return res;
    }


    class ValueComparator implements Comparator<String> {
        Map<String, Double> base;

        public ValueComparator(Map<String, Double> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with
        // equals.
        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }
}