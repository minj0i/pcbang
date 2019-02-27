package kr.co.sist.pcbang.client.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import kr.co.sist.pcbang.client.login.newuser.PUPolicyView;
import kr.co.sist.pcbang.client.main.PUMainView;

/**
 * 클라이언트 로그인
 * @author owner
 */
public class PULoginController extends WindowAdapter implements ActionListener{

	private PULoginView pulv;
	private PULoginDAO pul_dao;
	
	public PULoginController(PULoginView pulv) {
		this.pulv=pulv;
		pul_dao=PULoginDAO.getInstance();
		setNotice();
	}//PULoginController
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource()==pulv.getJbtStart()) {//로그인
			try {
			if(checkMember()) {//회원이면
				JTextField jtf=pulv.getJtfId();
				JPasswordField jpf=pulv.getJpfPass();
				
				String id=jtf.getText().trim();
				String pass=new String (jpf.getPassword());
				System.out.println(id+"/"+pass);
		
				//System.out.println(pul_dao.selectMember(id));
				if(!pul_dao.selectMember(id)) {//PC테이블에 조회되는 id가 없다면 로그인
					//System.out.println("로그인!!!");
					//로그인
					PUCertificationVO pucvo=new PUCertificationVO(id, pass);
					//String memberName=login(pucvo);
					//System.out.println("!!!");
					
					if(!login(pucvo)) {
						//System.out.println("아이디가 없어?");
						JOptionPane.showMessageDialog(pulv, "아이디나 비밀번호를 확인하세요.");
						jtf.setText("");
						jpf.setText("");
						jtf.requestFocus();
					}else {
						//System.out.println("로그인되었습니다.");
						PUMainView.userId=id;//로그인이 성공했다면 id를 모든객체에서 사용할 수 있도록 static 변수로 설정한다.
						PUMainView.cardNum="";
						new PUMainView();//new PUMainView(memberName);
						pulv.dispose();
					}//end else
				}else {//있다면 로그인이 안돼는데,자리이동이면~
					System.out.println("자리이동 유무");
					if(loginStatus().equals("c")) {
						//이미 로그인 되어있는데=>자리변경 신청함
						//System.out.println("자리변경한 상태입니다->로그인");
						
						PUCertificationVO pucvo=new PUCertificationVO(id, pass);
						//String memberName=login(pucvo);
						
						if(!login(pucvo)) {
							JOptionPane.showMessageDialog(pulv, "아이디나 비밀번호를 확인하세요.");
							jtf.setText("");
							jpf.setText("");
							jtf.requestFocus();
						}else {
							new PUMainView();
							PUMainView.userId=id;
							PUMainView.cardNum="";
							pulv.dispose();
						}//end else
					}else{
						//이미 로그인 되어있는데=>자리변경 신청안함
						JOptionPane.showMessageDialog(pulv, "자리변경을 먼저 신청해 주세요!");
					}//end else
				}//end else
				
			}else{//비회원이면
				JTextField jtf2=pulv.getJtfCardNum();
				try {
				int card=Integer.parseInt(jtf2.getText().trim());
				//System.out.println(pul_dao.selectGuest(card));
					if(!pul_dao.selectGuest(card)) {//PC테이블에 조회되는 id가 없다면 로그인
						//로그인
						if(login(card)) {
							PUMainView.userId="";//로그인이 성공했다면 id를 모든객체에서 사용할 수 있도록 static 변수로 설정한다.
							PUMainView.cardNum=String.valueOf(card);
							new PUMainView();
							pulv.dispose();
						}else {
							jtf2.setText("");
							jtf2.requestFocus();
							JOptionPane.showMessageDialog(pulv, "카드번호를 확인해주세요");
						}//end else
					}else {//있다면 로그인이 안돼는데,자리이동이면~
						if(loginStatus().equals("c")) {
							//이미 로그인 되어있는데=>자리변경 신청함
							if(!login(card)) {
								new PUMainView();
								PUMainView.userId="";//로그인이 성공했다면 id를 모든객체에서 사용할 수 있도록 static 변수로 설정한다.
								PUMainView.cardNum=String.valueOf(card);
								pulv.dispose();
							}else {
								jtf2.setText("");
								jtf2.requestFocus();
								JOptionPane.showMessageDialog(pulv, "카드번호를 확인해주세요");
							}//end else
						}else{
							//이미 로그인 되어있는데=>자리변경 신청안함
							jtf2.setText("");
							jtf2.requestFocus();
							JOptionPane.showMessageDialog(pulv, "자리변경을 먼저 신청해 주세요!");
						}//end else
				}//end else
					
				} catch (NumberFormatException nbfe) {
					jtf2.setText("");
					jtf2.requestFocus();
					JOptionPane.showMessageDialog(pulv, "카드번호는 숫자입니다.");
					//nbfe.printStackTrace();
				}//end catch
			}//end else
			
			} catch (SQLException e) {
				e.printStackTrace();
			}//end catch
		}//end if
		
		if(ae.getSource()==pulv.getJbtMembership()) {
			//JOptionPane.showMessageDialog(pulv, "회원가입!");
			new PUPolicyView();
			//pulv.dispose();
		}//end if
		if(ae.getSource()==pulv.getJbtFind()) {
			JOptionPane.showMessageDialog(pulv, "아이디/비밀번호 찾기!");
		}//end if
	}//actionPerformed
	
	@Override
	public void windowClosing(WindowEvent we) {
		int flag=JOptionPane.showConfirmDialog(pulv, "로그인 하지 않으시면 사용이 불가능 합니다.\n그래도 종료하시겠습니까?");
		if(flag==0) {
			pulv.dispose();
		}else {
			//아니요를 누르면 다시 로그인 창으로ㅠㅠㅠ
		}//end else
	}//windowClosing
	
	/**
	 * 회원, 비회원 구분
	 * @return false는 비회원 true는 회원
	 */
	private boolean checkMember() {
		boolean flagmember=true;
		boolean flagId=false;
		boolean flagPass=false;
		boolean flagCard=false;
		
		JTextField jtf1=pulv.getJtfId();
		JPasswordField jpf=pulv.getJpfPass();
		String pass=new String(jpf.getPassword());
		JTextField jtf2=pulv.getJtfCardNum();
		
		if(jtf2.getText().trim().equals("")) {
			if(jtf1.getText().trim().equals("")) {
				jtf1.setText("");
				jtf1.requestFocus();
				flagId=true;
				JOptionPane.showMessageDialog(pulv, "아이디를 입력해 주세요.");
			}else if(pass.trim().equals("")) {
				jpf.setText("");//공백을 입력하고 엔터친 경우 JPasswordField의 값을 초기화
				jpf.requestFocus();
				flagPass=true;
				JOptionPane.showMessageDialog(pulv, "비밀번호를 입력해 주세요.");
			}//end if
			if(!flagId&&!flagPass) {
				flagmember=true;//회원
			}//end if
		}else if(jtf1.getText().trim().equals("") && pass.trim().equals("")){
			if(jtf2.getText().trim().equals("")) {
				jtf2.setText("");
				jtf2.requestFocus();
				flagCard=true;
				JOptionPane.showMessageDialog(pulv, "비회원 카드번호를 입력해주세요");
			}//end if
			if(!flagCard) {
				flagmember=false;//비회원
			}//end if
//		}else {
//			System.out.println("로그인해 주세요!");
		}//end else
		return flagmember;
	}//checkNull
	
	/**
	 * 아이디가 로그인 중인지 확인, 이동가능한 상태인지 확인
	 * @return 
	 */
	private String loginStatus() {//c유무
		String flag="";
		String status;
		JTextField jtfid=pulv.getJtfId();
		JTextField jtfcard=pulv.getJtfCardNum();
		
		String id=jtfid.getText().trim();
		String cardNum=jtfcard.getText().trim();
		if(checkMember()) {//회원이면
			try {
				status = pul_dao.selectMemberIdStatus(id);
				flag=String.valueOf(status);
			} catch (SQLException e) {
				e.printStackTrace();
			}//end catch
		}else{//비회원이면
			try {
				status = pul_dao.selectGuestIdStatus(Integer.parseInt(cardNum));
				flag=String.valueOf(status);
			} catch (SQLException e) {
				e.printStackTrace();
			}//end catch
		}//end else
		return flag;
	}//loginStatus
	
	/**
	 * 비회원 로그인
	 * @param guestNum
	 * @return
	 */
	private boolean login(int guestNum) {
		boolean flag=false;
		try {
			PULoginDAO pul_dao=PULoginDAO.getInstance();
			InetAddress ip =InetAddress.getLocalHost();
			String pcIp=String.valueOf(ip).substring(InetAddress.getLocalHost().toString().indexOf("/")+1);

			if(pul_dao.selectGuestCheck(guestNum)) {//카드번호가 존재할때
				PUGuestStateVO pugsvo=new PUGuestStateVO(guestNum, pcIp);
				pul_dao.updateGuestState(pugsvo);//상태 업로드
				//서버에 접속할수 없습니다??
				flag=true;
			}//end if
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(pulv, "DB에서 문제가 발생했습니다.");
			e.printStackTrace();
		} catch (UnknownHostException uhe) {
			JOptionPane.showMessageDialog(pulv, "ip조회 실패");
			uhe.printStackTrace();
		} //end catch
		return flag;
	}//login guest
	
	/**
	 * 회원 로그인
	 * @param pucvo
	 * @return
	 */
	private boolean login(PUCertificationVO pucvo) {
		//String adminName="";
		boolean flag=false;
		try {
			PULoginDAO pul_dao=PULoginDAO.getInstance();

			InetAddress ip =InetAddress.getLocalHost();
			String pcIp=String.valueOf(ip).substring(InetAddress.getLocalHost().toString().indexOf("/")+1);
			//System.out.println(pcIp);
			//System.out.println(String.valueOf(pul_dao.selectMemberLogin(pucvo)).equals(""));
			if(pul_dao.selectMemberLogin(pucvo)!=-1) {
				
				flag=true;
				PUMemberStateVO pumsvo=new PUMemberStateVO(pucvo.getMemberId(),pcIp );
				//System.out.println("여기까지?");
				pul_dao.updateMemberState(pumsvo);
				//System.out.println("여기까지?2222");
//			}else {
//				JOptionPane.showMessageDialog(pulv, "비밀번호가 틀렸습니다.");
//				return false;
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(pulv, "DB에서 문제가 발생했습니다.");
			e.printStackTrace();
		} catch (UnknownHostException uhe) {
			JOptionPane.showMessageDialog(pulv, "ip조회 실패");
			uhe.printStackTrace();
		} //end catch
		return flag;
	}//login member
	
	/**
	 * 공지사항 불러오기
	 * @return
	 */
	private void setNotice() {
		String notice="";
		try {
			notice=pul_dao.selectNotice();
			JTextArea jt=pulv.getJtaNotice();
			jt.setText(notice);
		} catch (SQLException e) {
			e.printStackTrace();
		}//end catch
	}//setNotice

}//class
