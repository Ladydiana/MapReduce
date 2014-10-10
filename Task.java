
import java.util.LinkedList;

public class Task {
    public int type;
    public String doc;
    public int docId;
    public String piece;
    public LinkedList<LinkedList<Index>> initList;
    public LinkedList<LinkedList<LinkedList<Index>>> returnList;
    public LinkedList<Record> reduceReturn;

    public Task(String doc, int docId, String piece, LinkedList<LinkedList<LinkedList<Index>>> returnList) {
        this.type = 1;
        this.doc = doc;
        this.docId = docId;
        this.piece = piece;
        this.returnList = returnList;
    }

    public Task(int docId, String doc, LinkedList<LinkedList<Index>> initList, LinkedList<Record> reduceReturn) {
        this.type = 2;
        this.docId = docId;
        this.doc = doc;
        this.initList = initList;
        this.reduceReturn = reduceReturn;
    }
}
