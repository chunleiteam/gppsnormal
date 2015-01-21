package gpps.service.impl;

import gpps.dao.ICashStreamDao;
import gpps.dao.ILenderDao;
import gpps.dao.ISubmitDao;
import gpps.model.CashStream;
import gpps.service.CashStreamSum;
import gpps.service.IStatisticsService;
import gpps.service.TotalStatistics;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class StatisticsServiceImpl implements IStatisticsService{
	@Autowired
	ILenderDao lenderDao;
	@Autowired
	ISubmitDao submitDao;
	@Autowired
	ICashStreamDao cashStreamDao;
	@Override
	public TotalStatistics getTotalStatistics() {
		TotalStatistics totalStatistics=new TotalStatistics();
		totalStatistics.setLenderNum(lenderDao.countAll());
		totalStatistics.setInvestment(submitDao.countAll());
		List<Integer> actions=new ArrayList<Integer>();
		actions.add(CashStream.ACTION_PAY);
		CashStreamSum sum=cashStreamDao.sumCashStream(null, null, actions);
		if(sum!=null)
			totalStatistics.setRaiseAmount(sum.getChiefAmount().negate());
		actions.clear();
		actions.add(CashStream.ACTION_REPAY);
		sum=cashStreamDao.sumCashStream(null, null, actions);
		if(sum!=null)
			totalStatistics.setIncomeAmount(sum.getInterest());
		return totalStatistics;
	}
	
}
