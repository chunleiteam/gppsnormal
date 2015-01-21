package gpps.dao;

import gpps.model.Help;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface IHelpDao {
	public int countAll();
	public void create(Help help);
	public Help find(Integer id);
	/**翻页获取Public Help
	 * publicType:-1为不限
	 */
	public int countPublicHelps(@Param("publicType")int publicType);
	public List<Help> findPublicHelps(@Param("publicType")int publicType,@Param("offset")int offset,@Param("recnum")int recnum);
	/**翻页获取Private Help
	 * type -1不限 1:未回答 2：已回答
	 * questionerType:-1为不限
	 * questionerId:当questionerType不为-1时判断，null为不限
	 */
	public int countPrivateHelps(@Param("type")int type,@Param("questionerType")int questionerType,@Param("questionerId")Integer questionerId);
	public List<Help> findPrivateHelps(@Param("type")int type,@Param("questionerType")int questionerType,@Param("questionerId")Integer questionerId,@Param("offset")int offset,@Param("recnum")int recnum);
	public void answer(@Param("id")Integer id,@Param("answer")String answer,@Param("answertime")long answertime);
}
