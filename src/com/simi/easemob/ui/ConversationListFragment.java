package com.simi.easemob.ui;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.easeui.ui.EaseConversationListFragment;
import com.easemob.util.NetUtils;
import com.meijialife.simi.Constants;
import com.meijialife.simi.MainActivity;
import com.meijialife.simi.R;
import com.simi.easemob.EMConstant;
import com.simi.easemob.db.InviteMessgeDao;

/**
 * 会话列表Fragment
 *
 */
public class ConversationListFragment extends EaseConversationListFragment{

    private RadioGroup radiogroup;//顶部Tab
    private RadioButton rb_friend,rb_feed;//好友
    
    private TextView errorText;
    
    private FragmentActivity activity;
    
    public ConversationListFragment(){}
    
    public ConversationListFragment(FragmentActivity activity){
        this.activity = activity;
    }

    @Override
    protected void initView() {
        super.initView();
        View errorView = (LinearLayout) View.inflate(getActivity(),R.layout.em_chat_neterror_item, null);
        errorItemContainer.addView(errorView);
        errorText = (TextView) errorView.findViewById(R.id.tv_connect_errormsg);
        
        initTab();
    }
    
    /**
     * 初始化顶部Tab View
     * @param v
     */
    private void initTab(){
        radiogroup = (RadioGroup)getView().findViewById(R.id.radiogroup);
        rb_friend = (RadioButton)getView().findViewById(R.id.rb_friend);
        rb_feed = (RadioButton)getView().findViewById(R.id.rb_dynamic);
        radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup grop, int checkedId) {
                
                if(checkedId == grop.getChildAt(0).getId()){
                	Constants.checkedIndex = 0;
                  //跳转到动态View
                    if(activity != null){
                        ((MainActivity)activity).change2Contacts();
                    }
                }
                if(checkedId == grop.getChildAt(1).getId()){
                	Constants.checkedIndex=1;
                	//跳转到好友View
                	 if(activity != null){
                         ((MainActivity)activity).change2Contacts();
                     }
                }
            }
        });
        
//        radiogroup.getChildAt(0).performClick();
    }
    
    @Override
    protected void setUpView() {
        super.setUpView();
        // 注册上下文菜单
        registerForContextMenu(conversationListView);
        conversationListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);
                String username = conversation.getUserName();
                if (username.equals(EMChatManager.getInstance().getCurrentUser()))
                    Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, 0).show();
                else {
                    // 进入聊天页面
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    if(conversation.isGroup()){
                        if(conversation.getType() == EMConversationType.ChatRoom){
                            // it's group chat
                            intent.putExtra(EMConstant.EXTRA_CHAT_TYPE, EMConstant.CHATTYPE_CHATROOM);
                        }else{
                            intent.putExtra(EMConstant.EXTRA_CHAT_TYPE, EMConstant.CHATTYPE_GROUP);
                        }
                        
                    }
                    // it's single chat
                    intent.putExtra(EMConstant.EXTRA_USER_ID, username);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onConnectionDisconnected() {
        super.onConnectionDisconnected();
        if (NetUtils.hasNetwork(getActivity())){
         errorText.setText(R.string.can_not_connect_chat_server_connection);
        } else {
          errorText.setText(R.string.the_current_network);
        }
    }
    
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.em_delete_message, menu); 
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean handled = false;
        boolean deleteMessage = false;
        /*if (item.getItemId() == R.id.delete_message) {
            deleteMessage = true;
            handled = true;
        } else*/ if (item.getItemId() == R.id.delete_conversation) {
            deleteMessage = true;
            handled = true;
        }
        EMConversation tobeDeleteCons = conversationListView.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
        // 删除此会话
        EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup(), deleteMessage);
        InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
        inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
        refresh();

        // 更新消息未读数
//        ((EMMainActivity) getActivity()).updateUnreadLabel();
        ((MainActivity) getActivity()).updateUnreadLabel();//modify by garry
        
        return handled ? true : super.onContextItemSelected(item);
    }

}
