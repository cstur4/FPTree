package com.tur4;

import java.util.LinkedList;
import java.util.List;

import com.tur4.algorithm.FPTree;
/**
 * 
 * @author cstur4
 * email cstur4@gmail.com
 *
 */
public class test {
	public static void main(String[] args) {
		
		FPTree tree = new FPTree();
		List<LinkedList<String>> records = tree.readTransactionFile("in.txt", ",");
		tree.FPGrowth(records, null, 1);
		tree.showFrequenceSet();
		
		
	}
}
