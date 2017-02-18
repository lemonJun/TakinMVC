package learn.testioc.impl;

import learn.testioc.Hello2;

public class Hello2Impl implements Hello2{

	@Override
	public void function1() {
		System.out.println("Hello2Impl" + "function1" + "execute");
	}

	@Override
	public void function2() {
		System.out.println("Hello2Impl" + "function2" + "execute");
	}

}
