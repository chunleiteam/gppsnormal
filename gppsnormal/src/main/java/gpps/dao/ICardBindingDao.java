package gpps.dao;

import gpps.model.CardBinding;

public interface ICardBindingDao {
	public void create(CardBinding cardBinding);
	public CardBinding find(Integer id);
}
