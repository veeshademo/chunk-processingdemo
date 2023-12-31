package com.springbatch.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

//RowMapper is same as FieldSetmapper just difference is that its ResultSet here and FieldSet there
public class ProductRowMapper implements RowMapper<Product> {

	@Override
	public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
		System.out.println("ResultSet object = " + rs);
		Product product = new Product();
		product.setProductId(rs.getInt("product_id"));
		product.setProductName(rs.getString("product_name"));
		product.setProductCategory(rs.getString("product_category"));
		product.setProductPrice(rs.getInt("product_price"));
		return product;
	}

}
