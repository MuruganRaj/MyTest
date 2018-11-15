package iniyan.com.facebookintegrate;

import iniyan.com.facebookintegrate.model.GetgroupsResponse;

/**
 * Created by Murugan on 15-11-2018.
 */

public interface IAddGroupJion {

     void addjoin(int groupId, int customer_id, String join_status, int no_multy, String payment_status, GetgroupsResponse[] GetgroupsResponse);
}
