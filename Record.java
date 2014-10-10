
import java.util.LinkedList;

public class Record {
    String docname;
    LinkedList<Index> list;
    
    public Record(String d, LinkedList<Index> l){
        this.docname = d;
        this.list = l;
    }
}
