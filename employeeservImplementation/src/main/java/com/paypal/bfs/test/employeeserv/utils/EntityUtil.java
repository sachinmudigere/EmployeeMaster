package com.paypal.bfs.test.employeeserv.utils;

import org.springframework.beans.BeanUtils;

public class EntityUtil<U, M> {
	
	private U u;
	private M m;
	
	public M copyProperties(U u, M m) {
		
		BeanUtils.copyProperties(u, m);
		return m;
		
	}

}