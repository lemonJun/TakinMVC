package learn.testioc.impl;

import learn.testioc.Hello;

public class HelloImpl implements Hello{

	@Override
	public void function1() {
		
		System.out.println("HelloImpl" + "function1" + "execute");
	}

	@Override
	public void function2() {
		
		System.out.println("HelloImpl" + "function2" + "execute");
	}

}
