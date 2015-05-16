package gpps.service;

import gpps.model.Invite;
import gpps.service.exception.InviteException;

import java.util.List;

public interface IInviteService {
	public static final int MAX_ALLOC_NUMBER = 10;
	
	
	public List<String> allocate(Integer lenderId, int number, int batchCode) throws InviteException;
	public List<Invite> getUnRegistered(Integer lenderId) throws InviteException;
	public List<Invite> getRegistered(Integer lenderId) throws InviteException;
	public void check(String code) throws InviteException;
	public void register(String code, Integer lenderId) throws InviteException;
	public void release(String code) throws InviteException;
}
