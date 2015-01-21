package gpps.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import gpps.model.Borrower;
import gpps.model.Lender;

public interface ILenderDao {
	public int countAll();
	public List<Lender> findAll(@Param("offset")int offset,@Param("recnum")int recnum);
	public Lender find(Integer id);
	public Lender findByLoginId(String loginId);
	public Lender findByLoginIdAndPassword(@Param("loginId") String loginId,@Param("password") String password);
	public List<Lender> findByTel(String tel);
	public Lender findByIdentityCard(String identityCard);
	public List<Lender> findByEmail(String email);
	public Lender findByAccountID(Integer accountId);
	public void create(Lender lender);
//	public void update(Lender lender);
	public void changePrivilege(@Param("id") Integer id,@Param("privilege") int privilege);
	public void changeLevel(@Param("id") Integer id,@Param("level") int level);
	public void changePassword(@Param("id") Integer id,@Param("password") String password);
	public void changeGradeAndLevel(@Param("id")Integer id,@Param("grade")int grade,@Param("level")int level);
	public void delete(Integer id);
	public void registerSecondStep(@Param("id")Integer id,@Param("name")String name,@Param("identityCard")String identityCard,@Param("sex")int sex,@Param("address")String address,@Param("annualIncome")String annualIncome);
	public void registerThirdPartyAccount(@Param("id")Integer id,@Param("thirdPartyAccount")String thirdPartyAccount,@Param("accountNumber")String accountNumber);
	
	/**
	 * @param state -1 不限
	 * @return
	 */
	public int countByPrivilege(@Param("privilege")int privilege);
	/**
	 * @param state -1 不限
	 * @return
	 */
	public List<Lender> findByPrivilegeWithPaging(@Param("privilege")int privilege,@Param("offset")int offset,@Param("recnum")int recnum);
	public void bindCard(@Param("id")Integer id,@Param("cardId")Integer cardId);
	public Lender findByThirdPartyAccount(String thirdPartyAccount);
	public void updateTelAndEmail(@Param("id")Integer id,@Param("tel")String tel,@Param("email")String email);
	
	public void updateName(@Param("id")Integer id,@Param("name")String name);
	public void updateEmail(@Param("id")Integer id,@Param("email")String email);
	public void updateAddress(@Param("id")Integer id,@Param("address")String address);
}
