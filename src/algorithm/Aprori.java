package algorithm;

import java.util.*;
import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

class Tuple {
Set<Integer> itemset;
int support;

Tuple() {
	itemset = new TreeSet<>();
	support = -1;
}

Tuple(TreeSet<Integer> s) {
	itemset = s;
	support = -1;
}

Tuple(Set<Integer> s, int i) {
	itemset = s;
	support = i;
}


@Override
public String toString() {
	
	String op = "[";
	for(int temp : itemset) {
		
		op += (temp + " ");
	}
	op += "]";
	return op;
}
}

class Apriori {
static Set<Tuple> c;
static Set<Tuple> l;
static int d[][];
static int min_support;
static Map<Tuple, Integer> interest = new HashMap<Tuple, Integer>();

public static void main(String args[]) throws Exception {
	
	getDatabase();
	c = new HashSet<>();
	l = new HashSet<>();
	
	int i, j;

	//Assume the support to be 10
	min_support = 10;
	
	FileWriter fileWriter = new FileWriter("E:\\Sample_Data_Aprori\\intermediate_cart_details.txt");
	BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

	Set<Integer> candidate_set = new HashSet<>();
	for(i=0 ; i < d.length ; i++) {
		//System.out.println("Transaction Number: " + (i+1) + ":");
		bufferedWriter.newLine();
		bufferedWriter.write("--------------------------------------------------");
		bufferedWriter.write("\nTransaction Number: " + (i+1) + ":");
		for(j=0 ; j < d[i].length ; j++) {
			
			//System.out.print("Item number " + (j+1) + " = ");
			bufferedWriter.write("\nItem number " + (j+1) + " = ");
			
			//System.out.println(d[i][j]);
			bufferedWriter.write(d[i][j]+"");
			candidate_set.add(d[i][j]);
		}
	}
	bufferedWriter.close();
	
	Iterator<Integer> iterator = candidate_set.iterator();
	while(iterator.hasNext()) {
		TreeSet<Integer> s = new TreeSet<>();
		s.add(iterator.next());
		Tuple t = new Tuple(s, count(s));
		c.add(t);
	}
	
	System.out.println("Calculating the frequent itemsets and their support...(Assumung the minumum support to be: " + min_support + ")");
	prune();
	generateFrequentItemsets();
	
	System.out.println("\n\nInterest for each subset:");
	for(Entry<Tuple, Integer> e: interest.entrySet()) {
		
		System.out.print(e.getKey());
		System.out.println(": " + e.getValue() + "%");
	}
	
	int min_confidence = 70;
	System.out.println("\n\n***Assuming the minimum confidence to be: " + min_confidence + "***");
	
	Map<Integer, Integer> output = new HashMap<Integer, Integer>();
	Scanner sc = new Scanner(System.in);
	Scanner sc2 = new Scanner(System.in);
	Set<Integer> input = new HashSet<Integer>();
	String ch = "";
	System.out.println("\tPress 'e' to exit");
	while(!ch.toLowerCase().equals("e")) {
		
		System.out.println("Continue shopping?(y/n)");
		ch = sc.nextLine();
		if(ch.toLowerCase().equals("y")) {
			
			System.out.println("Enter the item number to be added to your cart : ");
			input.add(sc2.nextInt());
			System.out.println("CART = " + input);
			Map<Integer, Integer> temp_output = predict(input, min_confidence);
			if(temp_output.size() > 0)
				output = temp_output; 
			
			if(output.size() > 0) {
				System.out.println("\nSuggested items:");
				System.out.println("ITEM\tPROBABILITY");
				for(Entry<Integer, Integer> e: output.entrySet()) {
					
					System.out.println(e.getKey() + "\t" + e.getValue()+"%");
				}
			}
			else
				System.out.println("No suggestions for you...");
		}
		else if(ch.toLowerCase().equals("n")) {
			
			System.out.println("Thank you for shopping...");
			System.out.println("\n\nNext customer.....");
			input.clear();
		}
		else if(ch.toLowerCase().equals("e")) {
			
			System.out.println("\n\n\t*** EXITING ***");
		}
		else
			System.out.println("Invalid input");
	}

	sc.close();
	sc2.close();
}

static int count(Set<Integer> s) {
	int i, k;
	int support = 0;
	int count;
	boolean containsElement;
	for(i=0 ; i < d.length ; i++) {
		count = 0;
		Iterator<Integer> iterator = s.iterator();
		while(iterator.hasNext()) {
			int element = iterator.next();
			containsElement = false;
			for(k=0 ; k < d[i].length ; k++) {
				if(element == d[i][k]) {
					containsElement = true;
					count++;
					break;
				}
			}
			if(!containsElement) {
				break;
			}
		}
		if(count == s.size()) {
			support++;
		}
	}
	return support;
}

static void prune() {
	
	l.clear();
	Iterator<Tuple> iterator = c.iterator();
	
	while(iterator.hasNext()) {
		Tuple t = iterator.next();
		if(t.support >= min_support) {
			l.add(t);
		}
	}
	System.out.println("-+- L -+-");
	for(Tuple t : l) {
		
		calculateInterest(t);
		System.out.println(t.itemset + " : " + t.support);
	}
}

private static void calculateInterest(Tuple t) {
	
	try {
		FileReader fileReader = 
            new FileReader("E:\\Sample_Data_Aprori\\processed_retail.txt");

        // Always wrap FileReader in BufferedReader.
        BufferedReader bufferedReader = 
            new BufferedReader(fileReader);
    
	    String line = null;
	    int count = 0;
	    while((line = bufferedReader.readLine()) != null) {
	
	    	for(int temp : t.itemset) {
	    		
	    		if(line.contains(temp+" ") || line.contains(" "+temp)) {
	    			
	    			count++;
	    			break;
	    		}
	    	}
	    }
	    
	    interest.put(t, (count/min_support));
	    bufferedReader.close();
    }
    
    catch(Exception e) {}
}

public static Map<Integer, Integer> predict(Set<Integer> input, int min_confidence) {
	
	Map<Integer, Integer> output2 = new HashMap<Integer, Integer>();
	
	for(java.util.Map.Entry<Tuple, Integer> e: interest.entrySet()) {
		
		Map<Integer, Integer> temp_output2 = new HashMap<Integer, Integer>();
		int cnt = input.size();
		while(temp_output2.size()==0 && cnt > 0) {
			
			int count = 0;
			for(int ip: input) {
				
				if(e.getKey().itemset.contains(ip))
					count++;
			}
			if(count == cnt) {
				
				Tuple temp = e.getKey();
				temp.itemset.removeAll(input);
				for(int ee:temp.itemset)
					if(!temp_output2.containsKey(ee) && e.getValue() > min_confidence)
						temp_output2.put(ee, e.getValue());
			}
			cnt--;
		}
		output2.putAll(temp_output2);
	}
	
	Collection<Integer> op = output2.values();
	List<Integer> vs = new ArrayList<Integer>();
	for(int tt:op)
		vs.add(tt);
	Collections.sort(vs);
	//System.out.println("\tVS : " + vs);
	int n = vs.size();
	if(n > 5)
		n = vs.get(n-5);
	Map<Integer, Integer> output3 = new TreeMap<Integer, Integer>();
	for(java.util.Map.Entry<Integer, Integer> e:output2.entrySet()) {
		
		if(e.getValue() >= n)
			output3.put(e.getKey(),e.getValue());
	}
	
	return(output3);
}

static void generateFrequentItemsets() {
	boolean toBeContinued = true;
	int element = 0;
	int size = 1;
	Set<Set<Integer>> candidate_set = new HashSet<>();
	
	while(toBeContinued) {
		candidate_set.clear();
		c.clear();
		Iterator<Tuple> iterator = l.iterator();
		while(iterator.hasNext()) {
			Tuple t1 = iterator.next();
			Set<Integer> temp = t1.itemset;
			Iterator<Tuple> it2 = l.iterator();
			while(it2.hasNext()) {
				Tuple t2 = it2.next();
				Iterator<Integer> it3 = t2.itemset.iterator();
				while(it3.hasNext()) {
					try {
						element = it3.next();
					} catch(ConcurrentModificationException e) {
						// Sometimes this Exception gets thrown, so simply break in that case.
						break;
					}
					temp.add(element);
					if(temp.size() != size) {
						Integer[] int_arr = temp.toArray(new Integer[0]);
						Set<Integer> temp2 = new HashSet<>();
						for(Integer x : int_arr) {
							temp2.add(x);
						}
						candidate_set.add(temp2);
						temp.remove(element);
					}
				}
			}
		}
		Iterator<Set<Integer>> candidate_set_iterator = candidate_set.iterator();
		while(candidate_set_iterator.hasNext()) {
			Set<Integer> s = candidate_set_iterator.next();
			// These lines cause warnings, as the candidate_set Set stores a raw set.
			c.add(new Tuple(s, count(s)));
		}
		
		//Any subset of the frequent itemsets will be frequent
		//Therefore prune the result again
		//And perform join operation
		prune();
		if(l.size() <= 1) {
			toBeContinued = false;
		}
		size++;
	}
	System.out.println("\n=+= FINAL LIST =+=");
	for(Tuple t : l) {
		System.out.println(t.itemset + " : " + t.support);
	}
}

static void getDatabase() throws Exception {
	
	FileReader fileReader = 
            new FileReader("E:\\Sample_Data_Aprori\\processed_retail.txt");

    //Wrap FileReader in BufferedReader.
    BufferedReader bufferedReader = 
        new BufferedReader(fileReader);
    
    String line = null;
    int cart_no = 0;
    Map<Integer, List <Integer>> m = new HashMap<>();
	
    while((line = bufferedReader.readLine()) != null) {
    	
    	List<Integer> temp = new ArrayList<Integer>();
    	String[] splitted_ip = line.split("\\s");
    	for(String item : splitted_ip) {
    		
    		temp.add(Integer.parseInt(item));
    	}
    	m.put(cart_no, temp);
    	cart_no++;
    }
	
	Set<Integer> keyset = m.keySet();
	d = new int[keyset.size()][];
	Iterator<Integer> iterator = keyset.iterator();
	int count = 0;
	List<Integer> temp;
	while(iterator.hasNext()) {
		temp = m.get(iterator.next());
		Integer[] int_arr = temp.toArray(new Integer[0]);
		d[count] = new int[int_arr.length];
		for(int i=0 ; i < d[count].length ; i++) {
			d[count][i] = int_arr[i].intValue();
		}
		count++;
	}
	
	bufferedReader.close();
	}
}

/*

* Sample Output:

Calculating the frequent itemsets and their support...(Assumung the minumum support to be: 10)
-+- L -+-
[32] : 121
[976] : 16
[55] : 10
[1659] : 13
[47] : 10
[612] : 10
[155] : 13
[334] : 12
[535] : 10
[76] : 10
[49] : 16
[824] : 17
[592] : 14
[1] : 13
[179] : 14
[548] : 16
[259] : 12
[286] : 17
[570] : 17
[242] : 10
[39] : 609
[79] : 13
[359] : 11
[338] : 11
[95] : 10
[41] : 240
[9] : 14
[855] : 17
[270] : 11
[740] : 17
[11] : 11
[664] : 23
[201] : 10
[1393] : 10
[271] : 16
[255] : 31
[589] : 18
[161] : 13
[1233] : 11
[249] : 17
[110] : 42
[438] : 20
[208] : 10
[101] : 27
[170] : 54
[533] : 14
[381] : 11
[48] : 438
[36] : 44
[123] : 19
[65] : 34
[677] : 13
[371] : 11
[89] : 36
[37] : 19
[117] : 15
[956] : 10
[413] : 11
[237] : 25
[604] : 14
[694] : 10
[225] : 12
[522] : 19
[310] : 26
[475] : 24
[441] : 15
[185] : 10
[38] : 245
[264] : 11
[60] : 12
[340] : 12
[105] : 12
[147] : 12
[550] : 13
[45] : 10
-+- L -+-
[48, 589] : 11
[48, 249] : 10
[41, 89] : 15
[38, 170] : 53
[438, 39] : 11
[39, 522] : 10
[39, 45] : 10
[39, 271] : 14
[36, 39] : 30
[37, 39] : 12
[38, 41] : 87
[48, 36] : 18
[38, 89] : 13
[38, 39] : 155
[41, 110] : 21
[39, 110] : 25
[48, 110] : 22
[65, 41] : 11
[101, 39] : 17
[49, 39] : 13
[39, 475] : 18
[39, 237] : 17
[41, 170] : 14
[39, 89] : 27
[548, 39] : 10
[533, 39] : 11
[48, 101] : 15
[310, 41] : 10
[38, 105] : 11
[48, 310] : 14
[36, 38] : 41
[32, 38] : 31
[39, 170] : 33
[38, 286] : 15
[65, 39] : 22
[38, 110] : 41
[371, 38] : 11
[36, 41] : 17
[48, 65] : 20
[39, 286] : 10
[48, 170] : 21
[48, 533] : 12
[39, 855] : 11
[39, 255] : 22
[48, 37] : 10
[48, 89] : 24
[48, 255] : 25
[48, 438] : 11
[48, 41] : 130
[48, 237] : 11
[39, 79] : 10
[48, 49] : 13
[310, 39] : 18
[48, 39] : 321
[32, 48] : 52
[38, 55] : 10
[48, 475] : 17
[39, 604] : 10
[32, 41] : 34
[39, 249] : 15
[48, 123] : 11
[48, 664] : 11
[39, 824] : 10
[48, 38] : 112
[39, 123] : 10
[740, 39] : 15
[48, 824] : 10
[37, 38] : 19
[39, 589] : 12
[39, 41] : 188
[32, 39] : 68
[39, 664] : 17
[39, 570] : 10
-+- L -+-
[38, 41, 110] : 20
[36, 38, 39] : 27
[48, 65, 39] : 15
[48, 36, 39] : 12
[38, 39, 110] : 25
[32, 39, 41] : 25
[38, 41, 170] : 14
[48, 32, 39] : 36
[36, 39, 41] : 13
[48, 101, 39] : 11
[48, 38, 170] : 21
[38, 39, 170] : 32
[48, 39, 237] : 11
[48, 49, 39] : 12
[48, 37, 38] : 10
[38, 39, 41] : 68
[48, 38, 110] : 22
[39, 41, 89] : 14
[48, 36, 38] : 18
[48, 39, 89] : 19
[39, 41, 170] : 12
[48, 533, 39] : 10
[48, 39, 110] : 17
[48, 41, 110] : 13
[48, 38, 39] : 83
[38, 39, 89] : 11
[48, 39, 475] : 13
[38, 41, 89] : 10
[32, 48, 38] : 15
[39, 41, 110] : 17
[48, 39, 255] : 19
[36, 38, 41] : 17
[32, 38, 39] : 20
[48, 41, 89] : 12
[32, 38, 41] : 10
[48, 32, 41] : 16
[48, 310, 39] : 11
[48, 39, 41] : 107
[48, 39, 170] : 14
[48, 38, 41] : 49
[37, 38, 39] : 12
-+- L -+-
[48, 32, 38, 39] : 11
[32, 48, 39, 41] : 13
[48, 36, 38, 39] : 12
[38, 39, 41, 170] : 12
[36, 38, 39, 41] : 13
[48, 38, 41, 110] : 13
[48, 38, 39, 110] : 17
[48, 38, 39, 41] : 42
[48, 39, 41, 89] : 12
[38, 39, 41, 110] : 17
[48, 38, 39, 170] : 14
[48, 39, 41, 110] : 12
-+- L -+-
[48, 38, 39, 41, 110] : 12

=+= FINAL LIST =+=
[48, 38, 39, 41, 110] : 12


Interest for each subset:
[32 ]: 17%
[38 170 ]: 32%
[438 39 ]: 65%
[39 522 ]: 64%
[1659 ]: 1%
[36 39 ]: 67%
[37 39 ]: 67%
[38 39 110 ]: 75%
[48 36 38 39 ]: 83%
[48 36 ]: 51%
[48 32 39 ]: 80%
[49 ]: 11%
[41 110 ]: 32%
[65 41 ]: 36%
[101 39 ]: 65%
[49 39 ]: 66%
[39 475 ]: 64%
[242 ]: 2%
[39 237 ]: 65%
[39 ]: 64%
[39 89 ]: 67%
[95 ]: 8%
[38 39 170 ]: 74%
[48 49 39 ]: 76%
[310 41 ]: 31%
[1393 ]: 1%
[110 ]: 6%
[371 38 ]: 31%
[36 41 ]: 35%
[48 65 ]: 51%
[208 ]: 2%
[48 89 ]: 51%
[48 255 ]: 48%
[48 41 ]: 60%
[48 ]: 47%
[39 79 ]: 67%
[32 48 38 ]: 69%
[39 41 110 ]: 72%
[48 39 255 ]: 76%
[48 49 ]: 50%
[89 ]: 10%
[117 ]: 3%
[956 ]: 1%
[48 39 ]: 75%
[38 55 ]: 38%
[32 38 41 ]: 57%
[48 32 41 ]: 67%
[48 39 41 ]: 79%
[225 ]: 2%
[48 123 ]: 48%
[522 ]: 2%
[310 ]: 3%
[48 38 ]: 63%
[441 ]: 1%
[48 38 41 ]: 71%
[264 ]: 2%
[740 39 ]: 64%
[48 824 ]: 48%
[340 ]: 1%
[147 ]: 3%
[48 65 39 ]: 77%
[48 36 39 ]: 77%
[39 45 ]: 66%
[39 271 ]: 64%
[38 41 170 ]: 50%
[32 48 39 41 ]: 83%
[48 38 39 41 110 ]: 85%
[39 110 ]: 66%
[48 110 ]: 49%
[824 ]: 2%
[1 ]: 80%
[286 ]: 2%
[41 170 ]: 34%
[359 ]: 1%
[48 38 41 110 ]: 71%
[41 ]: 29%
[855 ]: 1%
[48 101 ]: 49%
[270 ]: 2%
[255 ]: 4%
[38 286 ]: 31%
[65 39 ]: 67%
[48 39 89 ]: 77%
[39 41 170 ]: 72%
[48 170 ]: 51%
[48 533 ]: 47%
[438 ]: 2%
[101 ]: 5%
[38 39 41 110 ]: 79%
[36 ]: 11%
[123 ]: 4%
[65 ]: 10%
[48 475 ]: 47%
[39 604 ]: 64%
[413 ]: 1%
[604 ]: 1%
[39 824 ]: 64%
[185 ]: 2%
[105 ]: 3%
[550 ]: 1%
[39 570 ]: 64%
[976 ]: 2%
[36 38 39 ]: 76%
[55 ]: 11%
[47 ]: 10%
[32 39 41 ]: 76%
[155 ]: 2%
[38 39 41 170 ]: 79%
[38 41 ]: 49%
[38 39 ]: 74%
[592 ]: 1%
[179 ]: 3%
[548 ]: 1%
[570 ]: 1%
[79 ]: 10%
[338 ]: 1%
[48 101 39 ]: 76%
[9 ]: 80%
[48 38 170 ]: 64%
[533 39 ]: 64%
[11 ]: 21%
[664 ]: 2%
[201 ]: 2%
[48 310 ]: 49%
[36 38 ]: 35%
[589 ]: 2%
[32 38 ]: 42%
[1233 ]: 1%
[48 38 110 ]: 63%
[39 41 89 ]: 73%
[48 36 38 ]: 64%
[39 855 ]: 64%
[39 255 ]: 65%
[48 39 41 89 ]: 81%
[48 438 ]: 48%
[533 ]: 1%
[48 39 110 ]: 77%
[48 41 110 ]: 61%
[48 38 39 ]: 82%
[381 ]: 1%
[38 39 89 ]: 76%
[38 41 89 ]: 54%
[36 38 41 ]: 51%
[371 ]: 1%
[310 39 ]: 65%
[48 38 39 170 ]: 82%
[237 ]: 3%
[48 310 39 ]: 76%
[38 ]: 31%
[37 38 ]: 35%
[39 589 ]: 64%
[39 41 ]: 70%
[32 39 ]: 70%
[39 664 ]: 64%
[48 589 ]: 47%
[48 249 ]: 48%
[38 41 110 ]: 50%
[41 89 ]: 36%
[48 32 38 39 ]: 85%
[612 ]: 1%
[334 ]: 1%
[535 ]: 1%
[36 38 39 41 ]: 80%
[76 ]: 9%
[36 39 41 ]: 73%
[38 89 ]: 38%
[259 ]: 2%
[548 39 ]: 64%
[740 ]: 1%
[48 39 237 ]: 76%
[48 38 39 110 ]: 83%
[271 ]: 2%
[38 105 ]: 33%
[48 37 38 ]: 64%
[161 ]: 2%
[38 39 41 ]: 78%
[39 170 ]: 66%
[249 ]: 2%
[38 110 ]: 32%
[39 286 ]: 65%
[48 533 39 ]: 75%
[48 38 39 41 ]: 85%
[48 37 ]: 51%
[170 ]: 7%
[48 237 ]: 48%
[677 ]: 1%
[48 39 475 ]: 75%
[37 ]: 10%
[32 48 ]: 55%
[32 38 39 ]: 79%
[48 41 89 ]: 63%
[32 41 ]: 41%
[694 ]: 1%
[39 249 ]: 64%
[48 664 ]: 48%
[48 39 170 ]: 77%
[475 ]: 2%
[48 39 41 110 ]: 80%
[39 123 ]: 65%
[60 ]: 8%
[45 ]: 6%
[37 38 39 ]: 76%


***Assuming the minimum confidence to be: 70***
	Press 'e' to exit
Continue shopping?(y/n)
y
Enter the item number to be added to your cart : 
39
CART = [39]

Suggested items:
ITEM	PROBABILITY
32	79%
41	80%
48	80%
65	77%
110	80%
170	77%
Continue shopping?(y/n)
y
Enter the item number to be added to your cart : 
110
CART = [39, 110]

Suggested items:
ITEM	PROBABILITY
38	83%
41	80%
48	80%
Continue shopping?(y/n)
y
Enter the item number to be added to your cart : 
45
CART = [39, 45, 110]

Suggested items:
ITEM	PROBABILITY
38	83%
41	80%
48	80%
Continue shopping?(y/n)
y
Enter the item number to be added to your cart : 
41
CART = [39, 41, 45, 110]

Suggested items:
ITEM	PROBABILITY
32	76%
38	85%
48	80%
89	81%
170	79%
Continue shopping?(y/n)
n
Thank you for shopping...


Next customer.....
Continue shopping?(y/n)
y
Enter the item number to be added to your cart : 
100
CART = [100]

Suggested items:
ITEM	PROBABILITY
32	76%
38	85%
48	80%
89	81%
170	79%
Continue shopping?(y/n)
38
Invalid input
Continue shopping?(y/n)
y
Enter the item number to be added to your cart : 
38
CART = [100, 38]

Suggested items:
ITEM	PROBABILITY
32	79%
36	80%
37	76%
48	85%
89	76%
170	82%
Continue shopping?(y/n)
y
Enter the item number to be added to your cart : 
32
CART = [32, 100, 38]

Suggested items:
ITEM	PROBABILITY
48	85%
Continue shopping?(y/n)
y
Enter the item number to be added to your cart : 
48
CART = [32, 48, 100, 38]

Suggested items:
ITEM	PROBABILITY
36	77%
49	76%
65	77%
89	81%
101	76%
170	77%
237	76%
255	76%
310	76%
Continue shopping?(y/n)
n
Thank you for shopping...


Next customer.....
Continue shopping?(y/n)
e


	*** EXITING ***

*/