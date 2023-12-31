package com.springbatch.domain;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/*
In Spring Batch, a FieldSetMapper is an interface used to map data from a FieldSet (representing a record or line of input data)
 to a domain object, facilitating the transformation of raw data into structured Java objects during batch processing.
 It's commonly employed in the reading phase of batch jobs to convert input data into meaningful business objects.
 */
public class ProductFieldSetMapper implements FieldSetMapper<Product> {

	@Override
	public Product mapFieldSet(FieldSet fieldSet) throws BindException {
		Product product = new Product();
		product.setProductId(fieldSet.readInt("product_id"));
		product.setProductName(fieldSet.readString("product_name"));
		product.setProductCategory(fieldSet.readString("product_category"));
		product.setProductPrice(fieldSet.readInt("product_price"));
		return product;
	}

}
