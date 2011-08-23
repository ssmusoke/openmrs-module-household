/**
 * 
 */
package org.openmrs.module.household.web.dwr;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.household.model.Household;
import org.openmrs.module.household.model.HouseholdLocationEntry;
import org.openmrs.module.household.model.HouseholdLocationLevel;
import org.openmrs.module.household.model.HouseholdMembership;
import org.openmrs.module.household.service.HouseholdService;

/**
 * @author Ampath developers
 *
 */
public class DWRHouseholdService {
	private static final Log log = LogFactory.getLog(DWRHouseholdService.class);
	
	public String getSubLocations(String strLoc) {
		// hack to make sure other Locations aren't still hanging around
		HouseholdService service = Context.getService(HouseholdService.class);
		Context.clearSession();
		
		//Get all the levels locations,sub-locations,village
		List<HouseholdLocationLevel> levels = service.getOrderedHouseholdLocationLevels(false);
		
		//Get all location entries
		List<HouseholdLocationEntry> hl = service.getHouseholdLocationEntriesByLevel(levels.get(0));
		
		//Get location entry for given name
		List<HouseholdLocationEntry> hln = service.getHouseholdLocationEntriesByLevelAndName(hl.get(0).getHouseholdLocationLevel(), strLoc);
		
		String strVal="";
		for (int i = 0; hln.size()>0; i++) {
			//Get sub-locations for this location given the id
			List<HouseholdLocationEntry> hl1 = service.getChildHouseholdLocationEntries(hln.get(0).getHouseholdLocationEntryId());
			for (int n = 0; n<hl1.size(); n++) {
				if(!(n == hl1.size()-1))
					strVal += hl1.get(n) + ",";
				else
					strVal = strVal + hl1.get(n);
			}
			break;
		}
		
		
		
		//log.info("\n =====" + strVal);
		return strVal;
	}
	
	public String getVillage(String strSubLoc, String strLoc) {
		// hack to make sure other Locations aren't still hanging around
		HouseholdService service = Context.getService(HouseholdService.class);
		Context.clearSession();
		
		//Get all the levels locations,sub-locations,village
		List<HouseholdLocationLevel> levels = service.getOrderedHouseholdLocationLevels(false);
		//log.info("\n =====1=" + levels.size());
		//Get all location entries
		//List<HouseholdLocationEntry> hl = service.getHouseholdLocationEntriesByLevel(levels.get(0));
		//log.info("\n =====2=" + hl.size());
		//Get location entry for given name
		//List<HouseholdLocationEntry> hln = service.getHouseholdLocationEntriesByLevelAndName(hl.get(0).getHouseholdLocationLevel(), strLoc);
		//log.info("\n =====3=" + hln.size());
		//Get all sub-location for this entry
		List<HouseholdLocationEntry> hln2 = service.getHouseholdLocationEntriesByLevel(levels.get(1));
		//log.info("\n =====4=" + hln2.size());
		//Get sub-location entry for given name
		List<HouseholdLocationEntry> hln3 = service.getHouseholdLocationEntriesByLevelAndName(hln2.get(0).getHouseholdLocationLevel(), strSubLoc);
		//log.info("\n =====5=" + hln3.size());
		
		String strVal="";
		for (int i = 0; hln3.size()>0; i++) {
			//Get villages for this sub-location given the id
			List<HouseholdLocationEntry> hl1 = service.getChildHouseholdLocationEntries(hln3.get(0).getHouseholdLocationEntryId());
			for (int n = 0; n<hl1.size(); n++) {
				if(!(n == hl1.size()-1))
					strVal += hl1.get(n) + ",";
				else
					strVal = strVal + hl1.get(n);
			}
			break;
		}
		
		
		
		//log.info("\n =====" + strVal);
		return strVal;
	}
	
	public String getHousehold(String strHh) {
		HouseholdService service = Context.getService(HouseholdService.class);
		Household hh = new Household();
		hh = service.getHouseholdGroup(Integer.parseInt(strHh));
		List<HouseholdMembership> hm = service.getAllHouseholdMembershipsByGroup(hh);
		
		String strHousehold = null;
		
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = hm.iterator(); iterator.hasNext();) {
			HouseholdMembership householdMembership = (HouseholdMembership) iterator.next();
			
			if(householdMembership.isHouseholdMembershipHeadship()){
				strHousehold = householdMembership.getHouseholdMembershipMember().getNames() +
					"," +
					householdMembership.getHouseholdMembershipMember().getAddresses();
			}
		}
		
		return strHousehold;
	}
	
	public List<HouseholdMembership> getHouseholdMembers(String grpids){
		HouseholdService service = Context.getService(HouseholdService.class);
		Context.clearSession();
		Household grp=service.getHouseholdGroup(Integer.parseInt(grpids));
		List<HouseholdMembership> householdsMem = service.getAllHouseholdMembershipsByGroup(grp);
		return householdsMem;
	}
	
	public String getHouseholdMembersPortlet(String grpids){
		HouseholdService service = Context.getService(HouseholdService.class);
		Context.clearSession();
		Household grp=service.getHouseholdGroup(Integer.parseInt(grpids));
		List<HouseholdMembership> householdsMem = service.getAllHouseholdMembershipsByGroup(grp);

		String strHousehold = "";
		for (int i=0; i<householdsMem.size(); i++) {
			if(StringUtils.isEmpty(strHousehold))
				strHousehold="";
			else
				strHousehold+=",";
			strHousehold += householdsMem.get(i).getHouseholdMembershipMember().getNames() +
					"," + householdsMem.get(i).getHouseholdMembershipMember().getGender() +
					"," + householdsMem.get(i).getHouseholdMembershipMember().getBirthdate();
		}
		return strHousehold;
	}

}
