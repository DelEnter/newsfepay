package com.ecpss.action.pay.hn;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.ecpss.action.pay.tc.XMLGetMessage;

public class ClientBoc {
	
	private HNGetMessage hnGetMessage;
	
	public HNGetMessage getMessage() {
		String poststr = hnGetMessage.getMessage();
		System.out.println(poststr);
//		String socket_ip = "172.20.5.100";
//		int socket_port = 3800;
//		Socket socket = null;
//		OutputStream output = null;
//		InputStream input = null;
//		byte clientByte[] = new byte[2]; // ���͸�����˵��ֽ�����
//		byte serverByte[] = new byte[1024];// ���ո�����˵��ֽ�����
//		clientByte[0] = (byte) 0xc1; // �����鸳ֵ
//		clientByte[1] = (byte) 0x01; // �����鸳ֵ
//		String answerMSG="";
//		try {
//			socket = new Socket(socket_ip, socket_port); // ���ӷ����
//			output = socket.getOutputStream(); // ���socket ���ӵ������
//			input = socket.getInputStream(); // ���socket
//			//System.out.println("request:"+poststr);									// ���ӵ�������,��Ҫ��ǰ���죬�����޷���������
//			//output.write(clientByte); // ��clientByteд�������
//			byte bb[] = poststr.getBytes();
//			output.write(bb); // ��clientByteд�������
//			output.flush(); // ˢ�´��������ǿ��д�����л��������ֽڡ�
//
//			// ��ȡ����������Ϊ0����ʾû�յ��ֽڣ�
//			// ��Ϊʹ���˻�������1024������һ���Զ���1024������ʹ��while����ѭ����ȡ��ֱ��input.read(serverByte)
//			// == -1
//			
//			int len = input.read(serverByte);
//			if (len > 0) {// ��ȡ���ֽ�
//				//System.out.println("��ȡ���ֽڡ��� " + len);
//			} else if (len == 0) {// û�ж�ȡ���ֽ�
//				//System.out.println("û��ȡ���ֽڡ���");
//				// ��ȡ����������Ϊ-1����ʾû�п��õ��ֽ� ��
//			} else if (len == -1) {// ��ȡ����
//				//System.out.println("��ȡ����");
//			}
//			// ѭ����ʾ��ȡ��������
////			//System.out.println(new String(serverByte));
////			answerMSG = new String(serverByte);
////			//System.out.println("response:"+answerMSG);
////			int index = answerMSG.lastIndexOf("</ap>");
////			answerMSG = answerMSG.substring(0,index+5);
////			answerMSG = answerMSG.replaceAll("\n","").substring(4);
////			HNGetMessage xmlset = new HNGetMessage();
////			xmlset.reciveRes(answerMSG);
////			hnGetMessage = xmlset.getXmlget();
////			System.out.println("rrn:"+hnGetMessage.getRrn());
////			System.out.println("amount:"+hnGetMessage.getTxn_amt());
////			System.out.println("authno:"+hnGetMessage.getAuth_no());   
////			System.out.println("invoice:"+hnGetMessage.getInvoice());   
////			System.out.println("response: "+hnGetMessage.getRespcode());
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (input != null) {
//				try {
//					input.close();
//				} catch (Exception e) {
//				}
//			}
//			if (output != null) {
//				try {
//					output.close();
//				} catch (Exception e) {
//				}
//			}
//			if (socket != null) {
//				try {
//					socket.close();
//				} catch (Exception e) {
//				}
//			}
//		}
		return hnGetMessage;
	}

	public HNGetMessage getHnGetMessage() {
		return hnGetMessage;
	}

	public void setHnGetMessage(HNGetMessage hnGetMessage) {
		this.hnGetMessage = hnGetMessage;
	}


}
