package com.springbatch.domain;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

/*
it implements the item prepared statement setter interface of generic type product.
And it has a single method called set values, which takes a product object as an argument along with a prepared statement object.
Now inside the set values method, we are going to set the values for the different parameters on the prepared statement.
 */
public class ProductItemPreparedStatementSetter implements ItemPreparedStatementSetter<Product> {

	@Override
	public void setValues(Product item, PreparedStatement ps) throws SQLException {
		ps.setInt(1, item.getProductId()); //This will replace the first ? in the ProductItemPreparedStatementSetter
		ps.setString(2, item.getProductName()); // Second ?
		ps.setString(3, item.getProductCategory()); // Third ?
		ps.setInt(4, item.getProductPrice()); // Fourth ?
	}

}
