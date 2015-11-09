package com.money.dao.auditActivityDAO;

import com.money.config.Config;
import com.money.dao.BaseDao;
import com.money.dao.TransactionSessionCallback;
import com.money.model.ActivityVerifyCompleteModel;
import com.money.model.ActivityVerifyModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by happysky on 15-7-22.
 * 审核项目dao
 */
@Repository
public class AuditActivityDao extends BaseDao {
    public ActivityVerifyModel getOldestActivity() {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            ActivityVerifyModel activityVerifyModel = (ActivityVerifyModel) session.createCriteria(ActivityVerifyModel.class)
                    .setMaxResults(1)
                    .addOrder(Order.asc("id"))
                    .add(Restrictions.eq("auditorStatus", ActivityVerifyModel.STATUS_FIRST_AUDITING))
                    .uniqueResult();
            t.commit();
            return activityVerifyModel;
        } catch (Exception e) {
            t.rollback();
            return null;
        }
    }

    public ActivityVerifyModel getNewestActivity() {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            ActivityVerifyModel activityVerifyModel = (ActivityVerifyModel) session.createCriteria(ActivityVerifyModel.class)
                    .setMaxResults(1)
                    .addOrder(Order.desc("id"))
                    .add(Restrictions.eq("auditorStatus", ActivityVerifyModel.STATUS_FIRST_AUDITING))
                    .uniqueResult();

            t.commit();
            return activityVerifyModel;
        } catch (Exception e) {
            t.rollback();
            return null;
        }
    }

    public boolean setActivityPass(final ActivityVerifyModel verifyModel, final ActivityVerifyCompleteModel completeModel) {
        String result = excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                //session.delete(verifyModel);
                session.update(verifyModel);
                session.saveOrUpdate(completeModel);
                return true;
            }
        });

        return result.compareTo(Config.SERVICE_SUCCESS) == 0;
    }

    @SuppressWarnings("unchecked")
    public List<ActivityVerifyModel> getAuditingActivityList() {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            List<ActivityVerifyModel> activityVerifyModels = session.createCriteria(ActivityVerifyModel.class)
                    .addOrder(Order.desc("id"))
                    .add(Restrictions.eq("auditorStatus", ActivityVerifyModel.STATUS_FIRST_AUDITING))
                    .list();

            t.commit();
            return activityVerifyModels;
        } catch (Exception e) {
            t.rollback();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<ActivityVerifyModel> getActivityList(ArrayList<Integer> status, int pageIndex, int pageNum) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            List<ActivityVerifyModel> activityVerifyModels = session.createCriteria(ActivityVerifyModel.class)
                    .addOrder(Order.asc("id"))
                    .add(Restrictions.in("auditorStatus", status))
                    .setFirstResult(pageIndex * pageNum)
                    .setMaxResults(pageNum)
                    .list();

            t.commit();
            return activityVerifyModels;
        } catch (Exception e) {
            t.rollback();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<ActivityVerifyModel> getUsersActivityList(String userId, int pageIndex, int pageNum) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            List<ActivityVerifyModel> activityVerifyModels = session.createCriteria(ActivityVerifyModel.class)
                    .addOrder(Order.asc("id"))
                    .add(Restrictions.eq("creatorId", userId))
                    .setFirstResult(pageIndex * pageNum)
                    .setMaxResults(pageNum)
                    .list();
            t.commit();

            ArrayList<String> ids = new ArrayList();
            for (ActivityVerifyModel model : activityVerifyModels) {
                if (model.getAuditorStatus() == ActivityVerifyModel.STATUS_AUDITOR_PASS_AND_KEEP) {
                    ids.add(String.valueOf(model.getId()));
                }
            }

            if (!ids.isEmpty()) {
                Session session1 = getNewSession();
                Transaction t1 = session1.beginTransaction();
                try {
                    List<ActivityVerifyCompleteModel> activityVerifyCompleteModels = session1.createCriteria(ActivityVerifyCompleteModel.class)
                            .add(Restrictions.in("activityId", ids))
                            .list();
                    t1.commit();

                    for (ActivityVerifyCompleteModel model : activityVerifyCompleteModels) {
                        for (ActivityVerifyModel verifyModel : activityVerifyModels) {
                            if (model.getActivityId().compareTo(String.valueOf(verifyModel.getId())) == 0) {
                                verifyModel.setAuditorStatus(model.getStatus());
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    t1.rollback();
                    return null;
                }

            }

            return activityVerifyModels;
        } catch (Exception e) {
            t.rollback();
            return null;
        }


    }

    public List<ActivityVerifyModel> getAuditActivityList(int page, int findNum, int status) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            List<ActivityVerifyModel> activityVerifyModels = session.createCriteria(ActivityVerifyCompleteModel.class)
                    .addOrder(Order.asc("id"))
                    .add(Restrictions.eq("status", status))
                    .setFirstResult(page * findNum)
                    .setMaxResults(findNum)
                    .list();

            t.commit();
            return activityVerifyModels;
        } catch (Exception e) {
            t.rollback();
            return null;
        }

    }
}
