
import java.io.*;
import java.util.*;

public class Main {

    public static LinkedList<String> readFile(String f, int D) throws UnsupportedEncodingException, FileNotFoundException {
        //Citeste din fisier numarul cerut de bytes si intoarce o lista de Stringuri obtinute
        File file = new File(f);
        Scanner S = new Scanner(file);
        LinkedList<String> strings = new LinkedList<>();
        int bytecount = 0;
        String a = "";
        byte[] b;
        while (S.hasNext()) {
            String n = S.next();
            a += " " + n;
            b = a.getBytes();
            bytecount = b.length;
            if (bytecount > D) {
                strings.add(a);
                bytecount = 0;
                a = "";
            }
        }
        if (!a.equals("")) {
            strings.add(a);
        }
        return strings;
    }

    public static LinkedList<LinkedList<Task>> readFiles(LinkedList<String> files, LinkedList<LinkedList<LinkedList<Index>>> returnList, int D) throws UnsupportedEncodingException, FileNotFoundException {
        //Preia numele fisierelor introduse ca input si le trimite functiei readFile pentru citire
        //Returneaza o lista de Task-uri ce vor fi trimise WorkPoolurilor
        LinkedList<LinkedList<String>> strings = new LinkedList<>();
        LinkedList<LinkedList<Task>> tasks = new LinkedList<>();
        for (int i = 0; i < files.size(); i++) {
            strings.add(readFile(files.get(i), D));
            tasks.add(new LinkedList<Task>());
        }
        for (int i = 0; i < strings.size(); i++) {
            for (int j = 0; j < strings.get(i).size(); j++) {
                Task t = new Task(files.get(i), i, strings.get(i).get(j), returnList);
                tasks.get(i).add(t);
            }
        }
        return tasks;
    }

    public static boolean contains(LinkedList<Index> list, String s) {
        //Verifica existenta unui String intr-o lista de Indecsi
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).word.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static LinkedList<Index> trim(LinkedList<Index> list, LinkedList<String> s) {
        //Pastraza intr-o lista doar cuvintele dorite
        //Daca cuvantul cautat nu se afla in document, facem o noua intrare cu frecventa 0
        LinkedList<Index> ret = new LinkedList<>();
        LinkedList<String> w= new LinkedList<>();
        for (int i = 0; i < list.size(); ++i) //{
            w.add(list.get(i).word);
        for (int i = 0; i < s.size(); ++i) {
            if (w.contains(s.get(i))) {
                for(int j=0; j<list.size(); ++j)
                    if(list.get(j).word.equals(s.get(i)))
                        ret.add(list.get(j));
            }
            else {
                Index u= new Index(s.get(i),0);
                ret.add(u);
            }
        }
        return ret;
    }

    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        int D, ND;
        float X;
        LinkedList<String> words = new LinkedList<>();
        LinkedList<String> documents = new LinkedList<>();
        String doc;
        
        
        File file = new File("input.txt");
        Scanner s = new Scanner(file);
        
        doc= s.next();
        documents.add(doc);
        D = s.nextInt();
        X = s.nextFloat();
        ND = s.nextInt();
        for (int i = 1; i <= ND; i++) {
            documents.add(s.next());
        }
        
        BufferedReader in = new BufferedReader(new FileReader(new File(doc)));
        String line;
        StringTokenizer tok;
         while((line=in.readLine())!=null){
             line= line.toLowerCase();
             tok= new StringTokenizer(line,"[^a-z]()_?!.,;:'|-\n\t ");
             while (tok.hasMoreElements())
                 words.add(tok.nextToken());
         }

        LinkedList<LinkedList<Task>> tasks = new LinkedList<>();
        LinkedList<LinkedList<LinkedList<Index>>> index = new LinkedList<>();
        for (int i = 0; i < ND+1; i++) {
            index.add(new LinkedList<LinkedList<Index>>());
        }

        tasks = readFiles(documents, index, D);
        WorkPool wp = new WorkPool(ND+1, "workPool");

        LinkedList<Worker> workers = new LinkedList<>();
        //Task-urile sunt trimise in workpool
        int no_tasks = 0;
        for (int i = 0; i < tasks.size(); i++) {
            for (int j = 0; j < tasks.get(i).size(); j++) {
                wp.putWork(tasks.get(i).get(j));
                no_tasks++;
            }
        }
        //Sunt creati workerii
        for (int i = 0; i < ND+1; i++) {
            workers.add(new Worker(wp));
        }
        //Workerii sunt porniti
        for (int i = 0; i < ND+1; i++) {
            workers.get(i).start();
        }
        int completed = 0;
        while (no_tasks > completed) {
            //Cat timp nu s-au terminat toate taskurile, se asteapta
            completed = 0;
            for (int i = 0; i < index.size(); i++) {
                completed += index.get(i).size();
            }
        }
        LinkedList<Record> reduceReturn = new LinkedList<>();
        no_tasks = 0;
        //Sunt create noile task-uri (pentru reduce si trimise la workpool
        for (int i = 0; i < index.size(); i++) {
            wp.putWork(new Task(i, documents.get(i), index.get(i), reduceReturn));
            no_tasks++;
        }
        workers.clear();
        for (int i = 0; i < ND+1; ++i) {
            workers.add(new Worker(wp));
        }
        for (int i = 0; i < ND+1; ++i) {
            workers.get(i).start();
        }
        while (no_tasks > reduceReturn.size()) {
        }
        //Lista finala de elemente de tip Record (nume_doc, lista de cuvinte)
        LinkedList<Record> fin = new LinkedList<>();

        for (int i = 0; i < reduceReturn.size(); ++i) {
            
            boolean ok = false;
            for (int j = 0; j < words.size(); ++j) {
                boolean cont = contains(reduceReturn.get(i).list, words.get(j));
                if (cont) {
                    ok = true;
                }
            }
            if (ok == true) {
                reduceReturn.get(i).list = trim(reduceReturn.get(i).list, words);
                fin.add(reduceReturn.get(i));
            }
        
            
        }
        
        //Calculam similitudinea
        ArrayList <Sums> ss = new ArrayList <>();
        float suma;
        //for (int j = 1; j < documents.size(); ++j) {
            for (int i = 0; i < fin.size(); ++i) {
                suma = 0;
                //if (documents.get(j).equals(fin.get(i).docname)) {
                    for (int k = 0; k < fin.get(i).list.size(); k++) {
                        if(fin.get(i).list.get(k).word.equals(fin.get(0).list.get(k).word))
                        suma+= (fin.get(i).list.get(k).frequency * fin.get(0).list.get(k).frequency);
                    }
                    ss.add(new Sums(fin.get(i).docname, suma/1000));
                }
           // }
        //}
        
        // Sortam ca sa fie in ordine descrescatoare
        Collections.sort(ss);
        //Rezultatele se scriu in fisier
        BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"));
        out.write("Rezultate pentru: "+ doc+"\n");
        for(int i=0; i< ss.size(); ++i){
            if(ss.get(i).sum>=X && !ss.get(i).docname.equals(doc))
                out.write(""+ss.get(i).docname+ " ("+ss.get(i)+")\n");
            else
                break;
        }
        out.close();
    }
}
