package com.tur4.pojo;

import java.util.LinkedList;
import java.util.List;

import javax.management.RuntimeErrorException;

/**
 * 
 * @author cstur4
 * email cstur4@gmail.com
 *
 */
public class TreeNode implements Comparable<TreeNode>{

	private String name;
	private Long count = 0L;
	private TreeNode nextHomony;
	private List<TreeNode> children;
	private TreeNode parent;

	public TreeNode findChild( String name){
		
		if(children !=  null)
			for(int i=0;i<children.size();++i)
				if(children.get(i).getName()!=null && children.get(i).getName().equals(name))
					return children.get(i);
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public TreeNode getNextHomony() {
		return nextHomony;
	}

	public void setNextHomony(TreeNode nextHomony) {
		this.nextHomony = nextHomony;
	}

	public List<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}

	public TreeNode getParent() {
		return parent;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	
	public void increase(Long increaseNum){
		this.count += increaseNum;
	}
	
	public void increase(){
		this.count ++;
	}
	
	public void addChild(TreeNode node){
		if(children==null)
			children = new LinkedList<TreeNode>();
		children.add(node);
		
	}
	@Override
	public String toString() {
		return name + "=" + count;
	}
	
	@Override
	public boolean equals(Object obj) {
		TreeNode node = (TreeNode)obj;
		return node.name.equals(name);
	}

	@Override
	public int compareTo(TreeNode o) {
		return  this.count-o.count>0?-1:1;
	}
	
	

}
