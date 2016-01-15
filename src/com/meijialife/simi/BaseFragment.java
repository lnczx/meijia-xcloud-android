package com.meijialife.simi;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;


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
	            // m_pDialog.hide();
	            m_pDialog.dismiss();
	            m_pDialog = null;
	        }
	    }
	 
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        dismissDialog();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); // 透明状态栏
        }*/
    }
 
}
