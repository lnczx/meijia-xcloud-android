package com.meijialife.simi;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;


/**
 * Fragment基类
 * 
 */
public class BaseFragment extends Fragment{
	
	private ProgressDialog m_pDialog;
	public void showDialog() {
		if(m_pDialog == null){
			m_pDialog = new ProgressDialog(getActivity());
			m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			m_pDialog.setMessage("请稍等...");
			m_pDialog.setIndeterminate(false);
			m_pDialog.setCancelable(false);
		}
		m_pDialog.show();
	}

	public void dismissDialog() {
		if (m_pDialog != null && m_pDialog.isShowing()) {
			m_pDialog.hide();
		}
	}
    
 
}
