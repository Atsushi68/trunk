package com.taketoritei.order.login.service;

import static com.taketoritei.order.jooq.tables.MAdmin.*;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taketoritei.order.jooq.tables.records.MAdminRecord;
import com.taketoritei.order.login.form.AdminUser;

/**
 * 認証、ユーザのサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AdminUserService implements UserDetailsService {

	@Autowired
	DSLContext jooq;

	/**
	 * 認証
	 *
	 * @param adminId
	 * @return
	 */
	@Transactional
	@Override
	public UserDetails loadUserByUsername(String adminId) throws UsernameNotFoundException {

		// ユーザーIDが無い
		if (null == adminId) {
			return new AdminUser(null);
		}

		// ユーザーテーブルのみ確認
		MAdminRecord mUserRec = jooq
				.selectFrom(M_ADMIN)
				.where(M_ADMIN.ADMIN_ID.eq(adminId))
				.and(M_ADMIN.DEL_FLG.eq(false))
				.fetchOne();

		// 空の場合
		return new AdminUser(mUserRec);
	}
}