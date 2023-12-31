package com.springbatch.reader;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

//So we're going to have an array list of product names as our data store, and this item reader will simply read our product names.
public class ProductNameItemReader implements ItemReader<String> { //In between arrows we have the type which we are going to read from the file

	private Iterator<String> productListIterator;

	public ProductNameItemReader(List<String> productList) {
		this.productListIterator = productList.iterator();
	}

	@Override //This is the unimplemented method whichwe get when we implement ItemReader interface
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		return this.productListIterator.hasNext() ? this.productListIterator.next() : null;
	}

}
