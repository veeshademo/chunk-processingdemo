package com.springbatch.config;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import com.springbatch.domain.OSProduct;
import com.springbatch.domain.Product;
import com.springbatch.domain.ProductFieldSetMapper;
import com.springbatch.domain.ProductRowMapper;
import com.springbatch.processor.MyProductItemProcessor;
import com.springbatch.reader.ProductNameItemReader;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired //This will autowire the data source which we have mentioned in our property files
	public DataSource dataSource;
	
	@Bean
	public ItemReader<String> itemReader() {
		List<String> productList = new ArrayList<>();
		productList.add("Product 1");
		productList.add("Product 2");
		productList.add("Product 3");
		productList.add("Product 4");
		productList.add("Product 5");
		productList.add("Product 6");
		productList.add("Product 7");
		productList.add("Product 8");
		return new ProductNameItemReader(productList);
	}
	
	@Bean
	public ItemReader<Product> flatFileItemReader() { //Takes data from the csv file
		FlatFileItemReader<Product> itemReader = new FlatFileItemReader<>();
		itemReader.setLinesToSkip(1);
		itemReader.setResource(new ClassPathResource("/data/Product_Details.csv"));

		DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setNames("product_id", "product_name", "product_category", "product_price");

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(new ProductFieldSetMapper());

		itemReader.setLineMapper(lineMapper);

		return itemReader;
	}
	
	@Bean
	public ItemReader<Product> jdbcCursorItemReader() {
		JdbcCursorItemReader<Product> itemReader = new JdbcCursorItemReader<>();
		itemReader.setDataSource(dataSource);
		//Always add order by in your SQL query for the data to appear in a particular order important
		itemReader.setSql("select * from product_details order by product_id");
		itemReader.setRowMapper(new ProductRowMapper());
		return itemReader;
	}
	
	@Bean
	public ItemReader<Product> jdbcPagingItemReader() throws Exception {
		JdbcPagingItemReader<Product> itemReader = new JdbcPagingItemReader<>();
		itemReader.setDataSource(dataSource);
		
		SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
		factory.setDataSource(dataSource);
		factory.setSelectClause("select product_id, product_name, product_category, product_price");
		factory.setFromClause("from product_details");
		factory.setSortKey("product_id");
		
		itemReader.setQueryProvider(factory.getObject());
		itemReader.setRowMapper(new ProductRowMapper());
		itemReader.setPageSize(3);
		
		return itemReader;
	}
	
	@Bean
	public ItemWriter<Product> flatFileItemWriter() {
		FlatFileItemWriter<Product> itemWriter = new FlatFileItemWriter<>();
		itemWriter.setResource(new FileSystemResource("output/Product_Details_Output.csv"));
		
		DelimitedLineAggregator<Product> lineAggregator = new DelimitedLineAggregator<>();
		lineAggregator.setDelimiter(","); //We want to write stuff with comma(delimiter) separated in our csv file
		
		BeanWrapperFieldExtractor<Product> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(new String[] { "productId", "productName", "productCategory", "productPrice" }); //In this order the data will be displayed in the output file

		/*
		So what happens is whenever our flat file item writer is going to write a chunk of data, it makes use of the delimited line aggregator first.
 		This delimited line aggregator internally makes use of a bean wrapper field extractor to extract the fields.
		The bean wrapper field extractor internally has an extract method which takes a single product as an
		argument, and it will return an array of objects containing all the values of the different fields of our product object.
		And then the delimited line aggregator will aggregate this object array into a single line, which will be a comma separated line.
		So this will repeat for all the products, and that is how ultimately we will get a multi line CSV file having all the product records
		 */
		lineAggregator.setFieldExtractor(fieldExtractor);
		
		itemWriter.setLineAggregator(lineAggregator);
		return itemWriter;
	}
	
//	@Bean
//	public JdbcBatchItemWriter<Product> jdbcBatchItemWriter() {
//		JdbcBatchItemWriter<Product> itemWriter = new JdbcBatchItemWriter<>();
//		itemWriter.setDataSource(dataSource);
//		itemWriter.setSql("insert into product_details_output values (:productId, :productName, :productCategory, :productPrice)");
//		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
//		return itemWriter;
//	}
	
	@Bean
	public JdbcBatchItemWriter<OSProduct> jdbcBatchItemWriter() {
		JdbcBatchItemWriter<OSProduct> itemWriter = new JdbcBatchItemWriter<>();
		itemWriter.setDataSource(dataSource);
		itemWriter.setSql("insert into os_product_details values (:productId, :productName, :productCategory, :productPrice, :taxPercent, :sku, :shippingRate)");
		//Earlier we were using question marks in field names to avoid confusion now we are using BeanPropertyItemSqlParameterSourceProvider to properly map these field values using this
		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
		return itemWriter;
	}
	
	@Bean
	public ItemProcessor<Product, OSProduct> myProductItemProcessor() {
		return new MyProductItemProcessor();
	}
	
	@Bean
	public Step step1() throws Exception {
		return this.stepBuilderFactory.get("chunkBasedStep1")
				// Below <Product,OSProduct> means the input and output what we are giving and taking from the chunk, 3 is chunk size
				.<Product,OSProduct>chunk(3)
				.reader(jdbcPagingItemReader())
				.processor(myProductItemProcessor())
				.writer(jdbcBatchItemWriter())
				.build();
	}
	
	@Bean
	public Job firstJob() throws Exception {
		return this.jobBuilderFactory.get("job1")
				.start(step1())
				.build();
	}
}
