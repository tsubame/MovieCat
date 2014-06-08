package controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import dao.ProgramDao;
import entity.Program;
import util.OSChecker;
import view.ProgramsPanel;

/**
 * view.ProgramListPanalのコントローラクラス
 *　番組一覧パネルのロジックを記述
 * 
 * クラス作成と同時にパネル作成
 * 作成したパネルは getProgramsPanel()で他のクラスからうけとれる
 */
public class ProgramsPanelController {

	// ダウンロード履歴パネル
	private ProgramsPanel panel;
	// 番組のリスト
	private List<Program> programs;
	
	/**
	 * コンストラクタ
	 */
	public ProgramsPanelController() {
		// パネルを作成
		panel = new ProgramsPanel();
		// 背景色設定
		setBgColor();
		// チェックボックス描画
		draw();
		// アクションリスナーを追加
		addActionListner();
	}
	
	/**
	 * 番組選択用のチェックボックスを描画
	 * DBから番組一覧を取得して曜日ごとに出力
	 */
	public void draw() {
		// 番組一覧を取得
		getPrograms();
		if (programs.size() == 0) {
			return;
		}
		// 番組の件数ループ
		for (int i = 0; i < programs.size(); i++){
        	Program program = programs.get(i);
        	// 曜日のラベルを追加
            if (i == 0) {
            	JLabel weekLabel = new JLabel(program.getWeekDay());
            	panel.programChecksPanel.add(weekLabel);
            } else if ( !programs.get(i - 1).getWeekDay().equals(program.getWeekDay()) ) {
            	JLabel weekLabel = new JLabel(program.getWeekDay());
            	panel.programChecksPanel.add(weekLabel);
            }
            addProgramCheckBox(program.getName(), program.getDownloadCheck());          
        }
	}
	
	/**
	 * 番組選択用のチェックボックスを再描画
	 */
	public void redraw() {
		// パネルの初期化
		panel.init();
		// チェックボックスを描画
		draw();
		// チェックボックスにアクションリスナーを追加
		addActionListnertoCheckBox();
	}
	
	/**
	 * Mac用に背景色を設定
	 */
	private void setBgColor() {		
		OSChecker checker = new OSChecker();
		if (checker.isOsMac() == true) {
			panel.setBackground(new Color(0xE5E5E5));
		}
	}
	
	/**
	 * コンポーネントにアクションリスナーを追加
	 * 
	 */
	private void addActionListner() {
		// 「全て選択」ボタンにアクションリスナーを追加
		panel.checkAllButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				// チェックボックスをすべてチェック
				for (int i = 0; i < panel.programChecks.size(); i++) {
					panel.programChecks.get(i).setSelected(true);
					programs.get(i).setDownloadCheck(true);
				}
				// DBアップデート
				updateProgram(programs);
			}
		});
		// 「全ての選択を解除」ボタンにアクションリスナーを追加
		panel.uncheckAllButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				// チェックボックスをすべてチェック解除
				for (int i = 0; i < panel.programChecks.size(); i++) {
					panel.programChecks.get(i).setSelected(false);
					programs.get(i).setDownloadCheck(false);
				}
				// DBアップデート
				updateProgram(programs);
			}
		});
		// チェックボックスにアクションリスナーを追加
		addActionListnertoCheckBox();
	}
	
	/**
	 * チェックボックスにアクションリスナーを追加
	 * 
	 */
	private void addActionListnertoCheckBox() {
		for (int i = 0; i < panel.programChecks.size(); i++) {
			final JCheckBox cb = panel.programChecks.get(i);
			final Program program = programs.get(i);
			panel.programChecks.get(i).addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ev){
					program.setDownloadCheck(cb.isSelected());
					updateProgram(program);
				}
			});
		}
	}
	
	/**
	 * DBをアップデート
	 * 
	 * @param Program program
	 */
	private void updateProgram(Program program) {
		ProgramDao dao = new ProgramDao();
		dao.update(program);
	}
	
	/**
	 * DBをアップデート
	 * 
	 * @param Program program
	 */
	private void updateProgram(List<Program> programs) {
		ProgramDao dao = new ProgramDao();
		dao.update(programs);
	}
	
	/**
	 * 番組選択用のチェックボックスをパネルに追加
	 * 
	 * @param String  name
	 * @param Boolean check
	 */
	private void addProgramCheckBox(String name, Boolean check) {
		try {
			JCheckBox cb = new JCheckBox(name);
	        cb.setSelected(check);
	        cb.setBackground(Color.WHITE);
	        //cb.setFont(new Font("Hiragino Kaku Gothic Pro", Font.PLAIN, 12));
	        panel.programChecks.add(cb);
	        panel.programChecksPanel.add(cb);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
	}
	
	/**
	 * 番組一覧を取得
	 * 
	 * @return List<Program> 番組のリスト
	 */
	private void getPrograms() {
		ProgramDao dao = new ProgramDao();
		programs = dao.selectNotEndedPrograms();
	}
	
	/**
	 * アクセッサ
	 */
	public ProgramsPanel getPanel() {
		return panel;
	}
	public void setPanel(ProgramsPanel panel) {
		this.panel = panel;
	}
}
