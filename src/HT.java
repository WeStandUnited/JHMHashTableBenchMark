import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HT {

    static class Node {

        String key;
        int value;
        Node next = null;
        Lock lock = new ReentrantLock();

        public Node(String k, int v, Node n) {

            key = k;
            value = v;
            next = n;

        }

    }// End of Node Class

    // HT Variables
    int size;
    Node[] table;
    Set<String> keyset = new HashSet<String>();
    private int count = 0;
    private int threshold;
//setters and getters

    public HT(int s){

        size = s;
        table = new Node[size];
    }



    public void clear() {
        size = 16;
        keyset.clear();
        table = new Node[size];
        count = 0;
    }





    public int get(String k) {

        int i = hash(k);
        for (Node p = table[i]; p != null; p = p.next) {
            if (k.equals(p.key)) {
                return p.value;
            }
        }

        return 0;// only play where it can return zero

    }

    public void add(String k) {



        int i = hash(k);// make hash
        boolean contains = false;

        for (Node p = table[i]; p != null; p = p.next) {
            p.lock.lock();
            try {
                if (k.equals(p.key)) {

                    p.value++;
                    contains = true;
                }
            }catch (Exception e){//i dont like this but IDE made me do it
                e.printStackTrace();
            }finally {
                p.lock.unlock();

            }

        }
        if (!contains) {
            Node q = new Node(k, 1, table[i]);// create node
            try {
                table[i].lock.lock();
                count++;
                keyset.add(k);
                table[i] = q;

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                table[i].lock.unlock();
            }
        }

        threshold = (int) (0.75 * size);
        if (count > threshold) {

            resize();
        }

    }
    public void add(String k,int value) {

        int i = hash(k);// make hash
        boolean contains = false;
//		for (Node p = table[i]; p != null; p = p.next) {
//			if (k.equals(p.key)) {
//
//				p.value++;
//				contains = true;
//			}
//
//		}
        if (!this.contains(k)) {
            Node q = new Node(k, value, table[i]);// create node
            count++;
            keyset.add(k);
            table[i] = q;
        }

        threshold = (int) (0.75 * size);
        if (count > threshold) {

            resize();
        }

    }
    public boolean contains(String k) {

        int i = hash(k);
        for (Node p = table[i]; p != null; p = p.next) {
            if (k.equals(p.key)) {
                return true;
            }
        }

        return false;// only play where it can return zero

    }

    // place in table

    private void resize() {
        keyset.clear();
        Node[] oldtable = table;// make old table have all elements of the oringal table
        size = size * 2; // set size to a largeer value

        table = new Node[size];// clear oringal table and give new size
        count = 0;
        for (int i = 0; i < oldtable.length; i++) {// cycle through table till there isnt any more elements to cycle
            // through

            for (Node p = oldtable[i]; p != null; p = p.next) {// Node p= table[i] p equals the place in the table, if
                // nodes to copy over those elements too
                for (int j = 0; j <= p.value; j++) {


                    this.add(p.key);

                }
            }

        }

    }

    private int hash(String s) {
        // System.out.println(s);
        char[] arr = s.toCharArray();
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += sum + arr[i];

        }
        sum = 1013 * sum;
        // System.out.println(sum & size - 1);
        return sum & size - 1;
    }

}
