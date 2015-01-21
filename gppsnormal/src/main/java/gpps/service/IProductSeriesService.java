package gpps.service;

import gpps.model.ProductSeries;

import java.util.List;

public interface IProductSeriesService {
	public void create(ProductSeries productSeries);
	public ProductSeries find(Integer id);
	public List<ProductSeries> findAll();
	public ProductSeries findByType(Integer type);
}
