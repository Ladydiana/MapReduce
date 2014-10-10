
import java.util.Collections;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Worker extends Thread {

    public WorkPool wp;

    public Worker(WorkPool wp) {
        this.wp = wp;
    }

    void processPartialSolution(Task ps, int type) { //type 1 = map, type 2 = reduce
        if (type == 1) {
            //Formateaza Stringul
            ps.piece = ps.piece.toLowerCase();
            ps.piece = ps.piece.replaceAll("[^a-z]-_.?!;:'(),", " ");
            StringTokenizer stringTok = new StringTokenizer(ps.piece);
            LinkedList<Index> list = new LinkedList<>();
            while (stringTok.hasMoreTokens()) {
                //Numara cuvintele
                String s = stringTok.nextToken();
                boolean done = false;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).word.equals(s)) {
                        list.get(i).number_of_apps++;
                        done = true;
                    }
                }
                if (!done) {
                    list.add(new Index(s, 1));
                }
            }
            //Scrie in lista de return rezultatele obtinute, la pozitia adecvata
            ps.returnList.get(ps.docId).add(list);
        } else {
            //Reduce
            LinkedList<Index> ret = new LinkedList<>();
            //Combina rezultatele pentru a obtine informatii globale despre fisier
            for (int i = 0; i < ps.initList.size(); i++) {
                for (int j = 0; j < ps.initList.get(i).size(); j++) {
                    boolean done = false;
                    for (int k = 0; k < ret.size(); k++) {
                        if (ret.get(k).word.equals(ps.initList.get(i).get(j).word)) {
                            ret.get(k).number_of_apps+=ps.initList.get(i).get(j).number_of_apps;
                            done = true;
                        }
                    }
                    if (!done) {
                        ret.add(ps.initList.get(i).get(j));
                    }
                }
            }
            //Numara cuvintele
            int nr_wds = 0;
            for (int i = 0; i < ret.size(); i++) {
                nr_wds += ret.get(i).number_of_apps;
            }
            //Calculeaza frecventele
            for (int i = 0; i < ret.size(); i++) {
                float x = ret.get(i).number_of_apps;
                float y = x/nr_wds*100;
                ret.get(i).frequency = y;
            }
            
            //Scrie rezultatele in lista de return
            ps.reduceReturn.add(new Record(ps.doc, ret)); 
        }
    }

    @Override
    public void run() {
        while (true) {
            Task ps = null;
            try {
                ps = wp.getWork();
            } catch (InterruptedException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (ps == null) {
                break;
            }
            processPartialSolution(ps, ps.type);
        }
    }
}
