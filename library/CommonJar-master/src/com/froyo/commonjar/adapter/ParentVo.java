package com.froyo.commonjar.adapter;

import java.util.List;

public class ParentVo<E> {
	
	private List<E> child;

	public List<E> getChild() {
		return child;
	}

	public void setChild(List<E> child) {
		this.child = child;
	}
	
	public void addChild(E e){
		child.add(e);
	}
	
	public void addChild(List<E> childs){
		child.addAll(childs);
	}
	public void addChild(int position,E e){
		child.add(position, e);
	}
	
	public void addChild(int position,List<E> childs){
		child.addAll(position,childs);
	}
}
