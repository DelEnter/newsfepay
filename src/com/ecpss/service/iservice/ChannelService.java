package com.ecpss.service.iservice;

import java.util.List;

import com.ecpss.model.channel.InternationalChannels;
import com.ecpss.model.permissions.Resource;
import com.ecpss.model.permissions.Role;

public interface ChannelService {
    public List<Resource> getResource();
    public List<Role> getRoleList() ;
    public void addRole(Role role);
    /**
     * ��ȡͨ���б�
     * @return
     */
    public List<InternationalChannels> getChannelList();
    
    /**
     * �����̻�id��ȡ���̻���ͨ��ͨ���б�
     * @param merid
     * @return
     */
    public List<InternationalChannels> getChannelByMerid(Long merid);
    
    public void addChannel(InternationalChannels channel);
    /**
     * ��ȡ�̻�ͨ������
     * @return
     */
    public List getMerChannelList(Long merno);
    
    
}
