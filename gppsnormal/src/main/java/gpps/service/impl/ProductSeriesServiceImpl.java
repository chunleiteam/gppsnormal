package gpps.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpps.dao.IProductSeriesDao;
import gpps.model.ProductSeries;
import gpps.service.IProductSeriesService;
@Service
public class ProductSeriesServiceImpl implements IProductSeriesService{
	@Autowired
	IProductSeriesDao productSeriesDao;
	@Override
	public void create(ProductSeries productSeries) {
		productSeriesDao.create(productSeries);
	}

	@Override
	public ProductSeries find(Integer id) {
		return productSeriesDao.find(id);
	}

	@Override
	public List<ProductSeries> findAll() {
		return productSeriesDao.findAll();
	}
	
	@Override
	public ProductSeries findByType(Integer type){
		return productSeriesDao.findByType(type);
	}

}
