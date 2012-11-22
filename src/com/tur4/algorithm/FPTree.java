package com.tur4.algorithm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.tur4.pojo.TreeNode;

/**
 * 
 * @author cstur4
 * email cstur4@gmail.com
 *
 */
public class FPTree {
	
	private static Logger LOG = Logger.getLogger(FPTree.class);
	private List<LinkedList<String>> transactions = new LinkedList<LinkedList<String>>();
	private List<String> frequenceSet = new LinkedList<String>();
	public List<LinkedList<String>> readTransactionFile(String fileName, String separator){
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			String str = null;
			while((str = br.readLine()) != null){
				LinkedList<String> strs = new LinkedList<String>();
				String[] ss = str.split(separator);
				for(String s: ss)
					strs.add(s);
				transactions.add(strs);
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			
			if(br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			LOG.debug("transactions=" + transactions);
		}
		
		return transactions;
	}
	
	public List<TreeNode> buildHeaderTable(List<LinkedList<String>> records, int minSupport ){
		
		//LOG.debug("before header table(src)=" + records);
		Map<String, Long> map = new HashMap<String, Long>();
		for(LinkedList<String> record:records){
			for(String s:record){
				if(map.containsKey(s))
					map.put(s, map.get(s)+1);
				else
					map.put(s, 1L);
			}
		}
		
		List<Entry<String, Long>> table = new LinkedList<Entry<String, Long>>(map.entrySet());
		Collections.sort(table, new Comparator<Entry<String, Long>>() {

			@Override
			public int compare(Entry<String, Long> o1,
					Entry<String, Long> o2) {
				return o1.getValue()>o2.getValue()?-1:1;
			}
			
		});
		
		
		List<TreeNode> res = new LinkedList<TreeNode>();
		
		for(Entry<String, Long> entry: table){
			if(entry.getValue() < minSupport)
				break;
			TreeNode node = new TreeNode();
			node.setName(entry.getKey());
			node.setCount(entry.getValue());
			res.add(node);
		}
		//LOG.debug("after header table=" + res);
		return res;
		
	}
	
	private void sortByFrequence(List<LinkedList<String>> records, List<TreeNode> headerTable){
		
		final Map<String, Integer> map = new HashMap<String, Integer>();
		for(int i=0;i<headerTable.size();++i)
			map.put(headerTable.get(i).getName(), i);
		
		/*(can't just compare the number occurs because of the same numbers, we need total order(全序) instead of 
			partial order(偏序) */
		for(LinkedList<String> record:records){
			Collections.sort(record, new Comparator<String>(){
				@Override
				public int compare(String o1, String o2) {
					if(!map.containsKey(o1))
						return 1;
					if(!map.containsKey(o2))
						return -1;
					return map.get(o1)>map.get(o2)?1:-1;
				}
			});
		}
		LOG.debug("sorted List=" + records);
			
	}
	
	private void insertNodes(TreeNode root, LinkedList<String> records, List<TreeNode> headerTable){
	
		TreeNode subRoot = root;
		
		while(records.size()!=0){
			
			TreeNode node = new TreeNode();
			node.setName(records.pop());
			node.increase();
			TreeNode lastNode = getLastHomonyNode(node.getName(), headerTable);
			if(lastNode == null){
				records.poll();
				continue;
			}
			
			lastNode.setNextHomony(node);
			node.setParent(subRoot);
			subRoot.addChild(node);
			subRoot = node;
		}
	}
	private TreeNode getLastHomonyNode(String name, List<TreeNode> headerTable){
		TreeNode node = null;
		for(TreeNode treeNode: headerTable)
			if(treeNode.getName()!=null && treeNode.getName().equals(name)){
				node = treeNode;
				break;
			}
		if(node == null)
			return null;
		
		while(node.getNextHomony()!=null && node.getNextHomony().getName()!=null)
			node = node.getNextHomony();
		return node;
	}
	
	private void traceTree(TreeNode root, int blank){
		
		for(int i=0;i<blank;++i)
			System.out.print("  ");
		System.out.println(root.getName()+"="+root.getCount());
		if(root.getChildren()!=null)
			for(TreeNode node:root.getChildren()){
				traceTree(node, blank+1);
			}
		
	}
	public TreeNode buildFPTree(List<LinkedList<String>> records, List<TreeNode> headerTable){
		
		sortByFrequence(records, headerTable);
		
		TreeNode root = new TreeNode();
		TreeNode subRoot = null;
		TreeNode tmpNode;
		
		for(LinkedList<String> record:records){
			//LOG.debug("records for build tree=" + records);
			subRoot = root;	
			while(record.size()>0 && getLastHomonyNode(record.peek(), headerTable)!=null //not frequency item
					&& (tmpNode = subRoot.findChild(record.peek())) != null){
				
				tmpNode.increase();
				subRoot = tmpNode;
				record.poll();
			}
			insertNodes(subRoot, record, headerTable);
			
		}
		traceTree(root, 0);
		
		
		return root;
	}
	private void combination(List<TreeNode> nodes, int i, String itemset, List<String> posfix){
		if(i == nodes.size()){
			StringBuilder sb = new StringBuilder();
			sb.append(itemset);
			for(String s:posfix)
	    	   sb.append(s).append("/");
	        if(sb.toString().length()>2){
				frequenceSet.add(sb.toString());
				LOG.debug(sb.toString() + " added");
			}
			return;
		}
		TreeNode node = nodes.get(i);
		combination(nodes, i+1, itemset+node.getName()+"/", posfix);
		combination(nodes, i+1, itemset, posfix);
      
	}
	
	
	public void FPGrowth(List<LinkedList<String>> records, List<String> pattern, int minSupport){
		
		List<TreeNode> headerTable = buildHeaderTable(records, minSupport);
		LOG.debug("pattern="+pattern+"\theaderTable:"+headerTable);
		TreeNode root = buildFPTree(records, headerTable);
		
		if(records.size() == 1){//单路径
			
			combination(headerTable, 0, "", pattern);
			return;
			
		}
		if(root.getChildren()==null || root.getChildren().size()==0)
			return;
		
		for(int i=headerTable.size()-1;i>=0;i--){
			TreeNode header = headerTable.get(i);
			
			TreeNode headerNode = header;
			List<LinkedList<String>> CPB = new LinkedList<LinkedList<String>>();
			while((headerNode = headerNode.getNextHomony()) != null){
				TreeNode backNode = headerNode;
				LinkedList<String> preNodes = new LinkedList<String>();
				while((backNode = backNode.getParent()).getName() != null){
					preNodes.add(backNode.getName());
				}
				long count = headerNode.getCount();
				if(preNodes.size()!=0)
					while(count-- > 0 )
						CPB.add((LinkedList<String>) preNodes.clone());		
			}
			LinkedList<String> postPattern = new LinkedList<String>();
			postPattern.add(header.getName());
			if(pattern != null)
				postPattern.addAll(pattern);
		
			FPGrowth(CPB, postPattern, minSupport);
		}
	}

	public void showFrequenceSet() {
		for(String s:frequenceSet)
			System.out.println(s);
	}

}
