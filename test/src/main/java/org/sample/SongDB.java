//package org.sample;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;


public class SongDB extends Thread {
    private final Random random = new Random();
    private final ThreadLocal<Random> threadRNG = ThreadLocal.withInitial(Random::new);
    private int MODE_TEST;
    static ArrayList<String> userLookups = new ArrayList<>();
    static volatile ConcurrentHashMap<String, Integer> jhashT = new ConcurrentHashMap<>();
    static volatile HT hashT = new HT(80);
    static int accesses =0;

    public SongDB(int mode,int ac) {

        MODE_TEST = mode;
        accesses = ac;
    }


    public void run() {

        int option;//used as temp to hold random value
        int currentlookup;//used as a temp variable in each thread to store current lookup not to be shared
        int value;//used as temp to store current lookups value

        switch (MODE_TEST) {
            case 1://80% Read |  20% Write HT
                for (int i = 0; i < accesses; i++) {
                    option = ThreadLocalRandom.current().nextInt(10);
                    if (option < 8) {
                        // System.out.println("Read!");
                        hashT.get(userLookups.get(ThreadLocalRandom.current().nextInt(userLookups.size())));

                    } else if (option >= 8) {
                        // System.out.println("Write");
                        //WRITE
                        currentlookup =  ThreadLocalRandom.current().nextInt(userLookups.size());
                        //This gets a random index in the table

                        value = hashT.get(userLookups.get(currentlookup));
                        //this is the value of the index

                        hashT.add(userLookups.get(currentlookup));//custom add function alread adds
                        //mimicing user adding it to playlist then inc count

                    }
                }
                break;

            case 2://80% Write | 20% Read HT
                for (int i = 0; i < accesses; i++) {
                    option = ThreadLocalRandom.current().nextInt(10);
                    if (option <= 8) {

                        // System.out.println("Read");
                        hashT.get(userLookups.get(ThreadLocalRandom.current().nextInt(userLookups.size())));

                    } else if (option > 8) {
                        // System.out.println("Write");
                        //WRITE
                        currentlookup =  ThreadLocalRandom.current().nextInt(userLookups.size());
                        //This gets a random index in the table

                        value = hashT.get(userLookups.get(currentlookup));
                        //this is the value of the index
                        String htlookup = userLookups.get(currentlookup);
                        // System.out.println(htlookup+"V:"+value);
                        hashT.add(htlookup);//custom add function alread adds
                        //mimicing user adding it to playlist then inc count

                    }
                }
                break;
            case 3://50% Write | 50% Read CCHM
                for (int i = 0; i < accesses; i++) {
                    option = ThreadLocalRandom.current().nextInt(10);
                    if (option < 5) {
                        //WRITE
                        // System.out.println("Write");
                        currentlookup =  ThreadLocalRandom.current().nextInt(userLookups.size());
                        //This gets a random index in the table

                        value = jhashT.get(userLookups.get(currentlookup));
                        //this is the value of the index

                        jhashT.put(userLookups.get(currentlookup),++value);
                        //mimicing user adding it to playlist then inc count

                    } else if (option >= 5) {
                        // System.out.println("Read");
                        jhashT.get(userLookups.get(ThreadLocalRandom.current().nextInt(userLookups.size())));

                    }
                }

                break;
            case 4://80% Write | 20% Read CCHMs
                for (int i = 0; i < accesses; i++) {
                    option = ThreadLocalRandom.current().nextInt(10);
                    if (option < 8) {
                        //WRITE
                        // System.out.println("Write");
                        currentlookup =  ThreadLocalRandom.current().nextInt(userLookups.size());
                        //This gets a random index in the table

                        value = jhashT.get(userLookups.get(currentlookup));
                        //this is the value of the index

                        jhashT.put(userLookups.get(currentlookup),++value);
                        // System.out.println(userLookups.get(currentlookup)+"Value:"+jhashT.get(userLookups.get(currentlookup)));
                        // System.out.println("Size"+jhashT.size());
                        //mimicing user adding it to playlist then inc count

                    } else if (option >= 8) {
                        //READ
                        // System.out.println("Read");
                        String lookup = userLookups.get(ThreadLocalRandom.current().nextInt(userLookups.size()));
                        // System.out.println(lookup);
                        int  s =  jhashT.get(lookup);
                        // System.out.println(s);
                    }
                }

                break;


        }
    }


    private static void fillTable(File file) throws IOException {
        // MODE 1 = JAVA Concurrent HASHTABLE
        // MODE 2 = MY Conurrent HASHTABLE

        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                hashT.add(line, 0);
                jhashT.put(line, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            userLookups.addAll(jhashT.keySet());
            // System.out.println("SIZE"+userLookups.size());
            br.close();
        }


    }
    public static void Test(int clients,int mode,int iterations) throws IOException {
        fillTable(new File("MasterPlaylist.txt"));
        //    fillTable(new File("/home/cchiass2/csc375/Assignment2/MasterPlaylist.txt"));

        for (int i = 0; i < clients; i++) {
            SongDB s = new SongDB(mode,iterations);
            s.start();
        }
    }








    public static void main(String[] args) throws IOException, InterruptedException {
        ArrayList<Integer> arguements = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(new File("Commands.txt")));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                arguements.add(Integer.parseInt(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            br.close();
        }
        Test(arguements.get(0),arguements.get(1),arguements.get(2));

    }
}
